package com.erp.spring.model;
/**
 * @author Satyam Kumar
 *
 */
public class RestResponse<T> {

	private RestStatus<T> status;
	private T data;
	
	public RestResponse() {
	}

    public RestResponse(final T data, final RestStatus<T> status) {
        this.data = data;
        this.status = status;
    }
    
    public RestResponse(final T data) {
        this.data = data;
    }
    
	
	/**
	 * @return the status
	 */
	public RestStatus<T> getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(RestStatus<T> status) {
		this.status = status;
	}
	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}

	
}
