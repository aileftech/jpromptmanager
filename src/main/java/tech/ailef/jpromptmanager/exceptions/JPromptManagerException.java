package tech.ailef.jpromptmanager.exceptions;

/**
 * The root exception class for JPromptManager. All exception thrown will
 * extend this class.
 */
public class JPromptManagerException extends RuntimeException {
	private static final long serialVersionUID = 6822580904706984536L;

	public JPromptManagerException(Throwable e) {
		super(e);
	}
	
	public JPromptManagerException(String msg) {
		super(msg);
	}
	
	public JPromptManagerException(String msg, Throwable e) {
		super(msg, e);
	}
}
