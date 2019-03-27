package com.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileVO {

	private String user;
	private String status;
	private String type;
	private String theme;
	private Object userContext;
	private String locale;
	private String timeZone;
	private String dateFormat;
	private String accessToken;
	private Long expiresIn;
	private String tokenType;
	private String currency;
	private String timeFormat;
	private Boolean use24HourFormat;

	public Boolean getUse24HourFormat() {
		return use24HourFormat;
	}

	public void setUse24HourFormat(final Boolean use24HourFormat) {
		this.use24HourFormat = use24HourFormat;
	}

}
