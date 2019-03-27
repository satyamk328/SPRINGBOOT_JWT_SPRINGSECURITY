package com.erp.spring.model;

import java.util.Date;
/**
 * @author Satyam Kumar
 *
 */
public class RestStatus<T> {

	private String code;
    private String message;
    private String uniqueErrorId;
    private T messageCode;
    private Date timestamp = new Date();

    public RestStatus(final String statusCode, final String statusMessage) {
        this.code = statusCode;
        this.message = statusMessage;
    }

    public RestStatus(final String statusCode, final String statusMessage, final T messageCode) {
        this.code = statusCode;
        this.message = statusMessage;
        this.messageCode = messageCode;
    }
    
    public RestStatus() {

    }

    public RestStatus(final String statusCode, final String statusMessage, final String uniqueErrorId, final T messageCode) {
        this.code = statusCode;
        this.message = statusMessage;
        this.uniqueErrorId = uniqueErrorId;
        this.messageCode = messageCode;
    }

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the uniqueErrorId
	 */
	public String getUniqueErrorId() {
		return uniqueErrorId;
	}

	/**
	 * @param uniqueErrorId the uniqueErrorId to set
	 */
	public void setUniqueErrorId(String uniqueErrorId) {
		this.uniqueErrorId = uniqueErrorId;
	}

	/**
	 * @return the messageCode
	 */
	public T getMessageCode() {
		return messageCode;
	}

	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(T messageCode) {
		this.messageCode = messageCode;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	

}
