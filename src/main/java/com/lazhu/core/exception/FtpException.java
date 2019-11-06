package com.lazhu.core.exception;

/**
 * FTP异常
 * 
 * @author naxj
 * 
 */
@SuppressWarnings("serial")
public class FtpException extends RuntimeException {
	public FtpException() {
	}

	public FtpException(String message) {
		super(message);
	}

	public FtpException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
