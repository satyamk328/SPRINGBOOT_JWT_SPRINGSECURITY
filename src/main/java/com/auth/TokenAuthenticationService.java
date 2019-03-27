package com.auth;

import static java.util.Collections.emptyList;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.bean.JwtModel;
import com.bean.User;
import com.dao.UserDao;
import com.dao.UserJwtTokenDao;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by rajeevkumarsingh on 19/08/17.
 */
@Component
@Slf4j
public class TokenAuthenticationService {

	private static long expirationTime = 30; // 30 minutes
	@Value("${jwt.encrption.secret}")
	private String jwtSecret;

	static final String AUTHORIZATION_HEADER = "Authorization";
	static final String BEARER_TOKEN_PREFIX = "Bearer";

	static final String ACCOUNT_NUMBERS = "AccountNumbers";

	@Autowired
	private UserJwtTokenDao jwtDao;

	@Autowired
	private UserDao userDao;

	private static final String UNSUPPORTED_JWT_TOKEN = "Unsupported Jwt token";
	private static final String MALFORMED_JWT_TOKEN = "Malformed Jwt token";
	private static final String JWT_TOKEN_EXPIRED = "Jwt token expired";
	private static final String JWT_SIGNATURE_MISMATCH = "Jwt Signature mismatch";
	private static final String ILLEGAL_ARGUEMENT_EXCEPTION = "Illegal Arguement Exception";

	public String addAuthentication(final HttpServletResponse res, final String username,
			final HttpServletRequest req) {
		Long userId = userDao.getIdByName(username);
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final User userDetails = (User) authentication.getPrincipal();
		if (userId == null) {
			userId = userDetails.getUserId();
		}

		final String sessionId = req.getSession().getId();
		log.debug("\n\n\nSession Id getting saved is: " + sessionId);
		log.debug("Adding UserId to Jwt for userId = " + userId + "\n\n");
		Date expiryDate = new Date(System.currentTimeMillis() + (expirationTime * 60 * 1000));
		final JwtBuilder jwtBuilder = Jwts.builder().setSubject(username).setId("" + userId).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret);

		final String JWT = jwtBuilder.compact();
		// This is temporary fix to check the datapower flow
		res.addHeader(AUTHORIZATION_HEADER, BEARER_TOKEN_PREFIX + " dp-jwt-token");
		final JwtModel jwtModel = new JwtModel();
		jwtModel.setToken(BEARER_TOKEN_PREFIX + " " + JWT);
		jwtModel.setIssueTime(new Date());
		jwtModel.setUserId(userId);
		jwtModel.setExpirationTime(new Date(jwtModel.getIssueTime().getTime() + (expirationTime * 60 * 1000)));
		jwtModel.setValid(true);

		jwtDao.insertJwt(jwtModel);
		return BEARER_TOKEN_PREFIX + " " + JWT;

	}

	public Authentication getAuthentication(final HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZATION_HEADER);

		if (token != null && token.contains(BEARER_TOKEN_PREFIX)) {
			token = getAuthenticationToken(token);

			// parse the token.
			Claims claims;
			try {
				claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token.replace(BEARER_TOKEN_PREFIX, ""))
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

			final String userId = claims.getId();
			final List<String> accountNumbers = (List<String>) claims.get(ACCOUNT_NUMBERS);

			log.debug("Getting Authenticate for the userId = " + userId + "\n\n");

			if (!jwtDao.checkJwt(token)) {
				return null;
			}
			return user != null ? new UsernamePasswordAuthenticationToken(user, null, emptyList())
					: null;
		}
		return null;
	}

	public static long getExpirationTime() {
		return expirationTime;
	}

	public static void setExpirationTime(final long expirationTimeInMin) {
		expirationTime = expirationTimeInMin;
	}

	public String getUserName(final HttpServletRequest req) {
		String user = null;
		final String token = req.getHeader(AUTHORIZATION_HEADER);
		if (token != null && token.contains(BEARER_TOKEN_PREFIX)) {
			try {
				user = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token.replace(BEARER_TOKEN_PREFIX, ""))
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
	
	public Long getUserId(final HttpServletRequest req) {
		String userId = null;
		final String token = req.getHeader(AUTHORIZATION_HEADER);
		if (token != null && token.contains(BEARER_TOKEN_PREFIX)) {
			try {
				userId = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token.replace(BEARER_TOKEN_PREFIX, ""))
						.getBody().getId();
			} catch (final Exception e) {
				log.debug("Error parsing Jwt Token", e);
			}
		}
		if (userId == null) {
			return null;
		} else {
			return Long.parseLong(userId);
		}
	}
}
