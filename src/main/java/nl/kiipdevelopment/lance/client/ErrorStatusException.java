package nl.kiipdevelopment.lance.client;

import nl.kiipdevelopment.lance.network.LanceMessage;

public class ErrorStatusException extends Exception {
	
	private final LanceMessage message;
	
	public ErrorStatusException(LanceMessage message) {
		this.message = message;
	}
	
	public LanceMessage getLanceMessage() {
		return message;
	}
}
