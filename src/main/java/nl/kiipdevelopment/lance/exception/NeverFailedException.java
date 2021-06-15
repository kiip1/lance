package nl.kiipdevelopment.lance.exception;

public class NeverFailedException extends FailedException {
	public NeverFailedException(String message) {
		super(message);
	}
}
