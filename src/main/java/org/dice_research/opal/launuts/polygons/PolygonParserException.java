package org.dice_research.opal.launuts.polygons;

/**
 * Default exception thrown by {@link PolygonParserInterface}.
 * 
 * @author Adrian Wilke
 */
public class PolygonParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public PolygonParserException() {
		super();
	}

	public PolygonParserException(String message) {
		super(message);
	}

	public PolygonParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public PolygonParserException(Throwable cause) {
		super(cause);
	}

	protected PolygonParserException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}