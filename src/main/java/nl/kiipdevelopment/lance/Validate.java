package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.exception.EnsureFailedException;
import nl.kiipdevelopment.lance.exception.FailedException;
import nl.kiipdevelopment.lance.exception.NeverFailedException;
import org.jetbrains.annotations.Contract;

public class Validate {
	@Contract("false, _ -> fail")
	public static void ensure(boolean condition, String message) {
		if (!condition) {
			throw new EnsureFailedException(message);
		}
	}

	@Contract("false, _ -> fail")
	public static void ensure(boolean condition, Runnable fail) {
		if (!condition) {
			fail.run();
		}
	}

	@SuppressWarnings("Contract")
	@Contract("false -> fail")
	public static void assume(@SuppressWarnings("unused") boolean condition) {}

	@Contract("true, _ -> fail")
	public static void never(boolean condition, String message) {
		if (condition) {
			throw new NeverFailedException(message);
		}
	}

	@Contract("true, _ -> fail")
	public static void never(boolean condition, Runnable fail) {
		if (condition) {
			fail.run();
		}
	}

	@Contract("_ -> fail")
	public static void fail(String message) {
		throw new FailedException(message);
	}
}
