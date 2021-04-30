package nl.kiipdevelopment.lance;

import nl.kiipdevelopment.lance.client.LanceClient;
import nl.kiipdevelopment.lance.configuration.ConfigurationBuilder;

public class ThreadLeakTest {
	public static void main(String[] args) {
		LanceClient client = new LanceClient(
				"localhost", 6639,
				new ConfigurationBuilder().setPassword("qyvuboirgeybvuogire").build()
		);
		client.connect();

		while (client.listenerManager == null) {
			Thread.onSpinWait();
		}
		System.out.println(client.listFilenames());

		client.close();
	}
}
