package org.dice_research.opal.launuts.lau;

/**
 * Default exception thrown by {@link LauReaderInterface}.
 * 
 * @author Adrian Wilke
 */
public class LauReaderException extends Exception {

	private static final long serialVersionUID = 1L;

	public LauReaderException() {
		super();
	}

	public LauReaderException(String message) {
		super(message);
	}

	public LauReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public LauReaderException(Throwable cause) {
		super(cause);
	}

	protected LauReaderException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}