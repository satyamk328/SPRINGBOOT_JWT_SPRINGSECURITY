package com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bean.JwtModel;

@Repository
public class UserJwtTokenDao {

	@Value("${consumer_jwt_token.udpate.IsValid}")
	private String setValid;

	/** The jdbc template object. */
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplateObject;

	public boolean checkJwt(final String jwt) {
		return true;
	}
	public int insertJwt(final JwtModel jwt) {
		final BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(jwt);
		return jdbcTemplateObject.update("", params);
	}

	public int updateJwtIsValid(final JwtModel jwt) {
		final MapSqlParameterSource mapParam = new MapSqlParameterSource();
		mapParam.addValue("Token", jwt.getToken());
		mapParam.addValue("Valid", jwt.isValid());
		return jdbcTemplateObject.update(setValid, mapParam);
	}

}
