package com.auth.handler;

import static java.util.Collections.emptyList;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.auth.bean.UserPrincipal;
import com.auth.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Satyam Kumar.
 */
@Component
@Slf4j
public class TokenAuthenticationService {

	@Autowired
	private UserService userService;

	private static final String UNSUPPORTED_JWT_TOKEN = "Unsupported Jwt token";
	private static final String MALFORMED_JWT_TOKEN = "Malformed Jwt token";
	private static final String JWT_TOKEN_EXPIRED = "Jwt token expired";
	private static final String JWT_SIGNATURE_MISMATCH = "Jwt Signature mismatch";
	private static final String ILLEGAL_ARGUEMENT_EXCEPTION = "Illegal Arguement Exception";
	private static long expirationTime = 30; // 30 min
	public static final String SECRET = "jwt$TokenKey2";
	public static final String BEARER_TOKEN_PREFIX = "Bearer";
	public static final String AUTHORIZATION_HEADER = "Authorization";

	public String addAuthentication(final HttpServletResponse res, final String username,
			final HttpServletRequest req) {
		Long userId = userService.findByName(username).getId();
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
		if (userId == null) {
			userId = userDetails.getId();
		}

		final String sessionId = req.getSession().getId();
		log.debug("Session Id getting saved is:{} ", sessionId);
		log.debug("Adding SessionId to Jwt for SessionId = {}", sessionId);
		log.debug("res {}", res);

		final String JWT = Jwts.builder().setSubject(username).setId(sessionId)
				.setExpiration(new Date(System.currentTimeMillis() + (expirationTime * 60 * 1000)))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		// This is temporary fix to check the datapower flow
		res.addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + " dp-jwt-token");

	/*	final JwtModel jwtModel = new JwtModel();
		jwtModel.setToken(BEARER_TOKEN_PREFIX + " " + JWT);
		jwtModel.setIssueTime(new Date());
		jwtModel.setUserId(userId);
		jwtModel.setExpirationTime(new Date(jwtModel.getIssueTime().getTime() + (expirationTime * 60 * 1000)));
		jwtModel.setValid(true);
		jwtDao.insertJwt(jwtModel);*/

		return BEARER_TOKEN_PREFIX + " " + JWT;

	}

	public Authentication getAuthentication(final HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZATION_HEADER);
		if (token != null && token.contains(BEARER_TOKEN_PREFIX)) {
			token = getAuthenticationToken(token);
			// parse the token.
			Claims claims;
			try {
				claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(BEARER_TOKEN_PREFIX, ""))
						.getBody();

			} catch (final ExpiredJwtException e) {
				log.info(JWT_TOKEN_EXPIRED, e);
				return null;
			} catch (final UnsupportedJwtException e) {
				log.info(UNSUPPORTED_JWT_TOKEN, e);
				return null;
			} catch (final MalformedJwtException e) {
				log.info(MALFORMED_JWT_TOKEN, e);
				return null;
			} catch (final SignatureException e) {
				log.info(JWT_SIGNATURE_MISMATCH, e);
				return null;
			} catch (final IllegalArgumentException e) {
				log.info(ILLEGAL_ARGUEMENT_EXCEPTION, e);
				return null;
			}
			final String user = claims.getSubject();
			final String sessionId = claims.getId();

			log.debug("Getting Authenticate for sessionId = {}", sessionId);
			return user != null ? new UsernamePasswordAuthenticationToken(user, null, emptyList()) : null;
		}

		return null;
	}

	public String getSessionId(final HttpServletRequest request) {
		final String token = request.getHeader(AUTHORIZATION_HEADER);
		return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(BEARER_TOKEN_PREFIX, "")).getBody()
				.getId();
	}

	public String getUserName(final HttpServletRequest req) {
		String user = null;
		final String token = req.getHeader(AUTHORIZATION_HEADER);
		if (token != null && token.contains(BEARER_TOKEN_PREFIX)) {
			try {
				user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(BEARER_TOKEN_PREFIX, ""))
						.getBody().getSubject();
			} catch (final Exception e) {
				log.debug("Error parsing Jwt Token", e);
			}
		}
		return user;
	}

	public String getAuthenticationToken(String token) {
		final String[] tokenArray = token.split(",");
		if (tokenArray.length > 1) {
			// In case of Data Power the second token will belong to ssc.
			token = tokenArray[1];
			if (!token.contains(BEARER_TOKEN_PREFIX)) {
				token = BEARER_TOKEN_PREFIX + " " + token;
			}
		}
		return token;
	}

	public static long getExpirationTime() {
		return expirationTime;
	}

	public static void setExpirationTime(final long expirationTime1) {
		expirationTime = expirationTime1;
	}
}
