package nl.kiipdevelopment.lance;

import com.google.gson.JsonPrimitive;

public class IncrementExample {
	public static void main(String[] args) {
		new LanceServer();

		LanceClient lanceClient = new LanceClient();

		int amount = 0;

		if (lanceClient.existsJson("amount"))
			amount = lanceClient.getJson("amount").getAsInt();

		lanceClient.setJson("amount", new JsonPrimitive(++amount));

		System.out.println(amount);
	}
}
