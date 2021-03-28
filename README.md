[![Java CI with Gradle](https://github.com/kiipy/Lance/actions/workflows/gradle.yml/badge.svg)](https://github.com/kiipy/Lance/actions/workflows/gradle.yml)

# Lance
A simple database made in Java.

## Installation
Download the latest release and put it in your dependencies.

## Examples
Incremental counter. Every time you run this program the counter increments by 1.
```java
new LanceServer().start();

LanceClient lanceClient = new LanceClient();

lanceClient.start();

int amount = 0;

if (lanceClient.exists("amount"))
    amount = lanceClient.getInteger("amount");

lanceClient.setInteger("amount", ++amount);

System.out.println(amount);
```
