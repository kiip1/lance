package nl.kiipdevelopment.lance.server.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonStorage implements Storage<JsonElement> {
	private static final Gson GSON = new Gson();
	
	private final JsonObject data;
	
	public JsonStorage(Path location) throws IOException, StorageException {
		JsonElement element = JsonParser.parseReader(Files.newBufferedReader(location));
		if (!element.isJsonObject()) throw new StorageException("Stored json is not in object format");
		this.data = element.getAsJsonObject();
	}
	
	@Override
	public JsonElement get(String key) {
		if (key == null) return null;
		String[] path = key.split("\\.");
		
		if (path.length == 1) return data.get(path[0]);
		
		JsonElement element = data.get(path[0]);
		if (element == null || !element.isJsonObject()) return null;
		
		JsonObject currentObject = element.getAsJsonObject();
		for (int i = 1; i < path.length; i++) {
			element = currentObject.get(path[i]);
			if (element == null || !element.isJsonObject()) return null;
			
			currentObject = element.getAsJsonObject();
		}
		
		return currentObject;
	}
	
	@Override
	public boolean exists(String key) {
		//TODO
		return false;
	}
	
	@Override
	public void set(String key, JsonElement value) {
		//TODO
	}
	
	@Override
	public void close() throws Exception {
		//TODO save file
	}
}
