package nl.kiipdevelopment.lance.client;

import com.google.gson.JsonElement;

/**
 * A holder for a data value. A data value can be either a string,
 * if the storage is file based, or a json element, if the storage
 * is json based. You can easily check which one it is with #isFile()
 * and #isJson(). You can get the value using #getAsFile() or #getAsJson().
 */
public class DataValue {
	
	private final String stringValue;
	private final JsonElement jsonValue;
	
	public DataValue(String stringValue) {
		this.stringValue = stringValue;
		this.jsonValue = null;
	}
	
	public DataValue(JsonElement jsonValue) {
		this.stringValue = null;
		this.jsonValue = jsonValue;
	}
	
	public boolean isFile() {
		return stringValue != null;
	}
	
	public String getAsFile() {
		return stringValue;
	}
	
	public boolean isJson() {
		return jsonValue != null;
	}
	
	public JsonElement getAsJson() {
		return jsonValue;
	}
}
