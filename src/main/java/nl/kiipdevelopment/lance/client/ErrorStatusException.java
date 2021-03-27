package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.StatusCode;

public class ErrorStatusException extends Exception {
	
	private final StatusCode statusCode;
	
	public ErrorStatusException(StatusCode statusCode) {
		this.statusCode = statusCode;
	}
	
	public StatusCode getStatusCode() {
		return statusCode;
	}
}
