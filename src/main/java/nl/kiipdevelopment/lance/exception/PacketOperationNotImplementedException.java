package nl.kiipdevelopment.lance.exception;

public class PacketOperationNotImplementedException extends RuntimeException {
	public PacketOperationNotImplementedException() {
		super("This packet operation is not implemented.");
	}
}
