[![Java CI with Gradle](https://github.com/kiipy/Lance/actions/workflows/gradle.yml/badge.svg)](https://github.com/kiipy/Lance/actions/workflows/gradle.yml)

# Lance
A simple database made in Java.

## Installation
Download the latest release and put it in your dependencies.

## Examples
Incremental counter. Every time you run this program the counter increments by 1.
```java
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
```
