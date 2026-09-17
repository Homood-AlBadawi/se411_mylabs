package edu.spu.se411.lab06_logging.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsufficientFundsException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(InsufficientFundsException.class);

	public InsufficientFundsException(String message) {
		super(message);
		// Required by the lab table: WARN when the exception object is created.
		// Creating one is not yet a failure - it becomes an error only if it is
		// thrown and not handled, which is logged at ERROR by the handler.
		logger.warn("InsufficientFundsException created: {}", message);
	}
}
