# Lance

[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)

A simple database made in Java.

## Table of Contents

- [Installation](#installation)
- [Examples](#examples)

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
