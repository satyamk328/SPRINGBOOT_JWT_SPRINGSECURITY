package com.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class UnAuthorizedException  extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	/**
     * Constructs a <code>BadCredentialsException</code> with the specified message.
     *
     * @param msg the detail message
     */
    public UnAuthorizedException(String msg) {
        super(msg);
    }

    /**
     * Constructs a <code>BadCredentialsException</code> with the specified message and
     * root cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public UnAuthorizedException(String msg, Throwable t) {
        super(msg, t);
    }
}
