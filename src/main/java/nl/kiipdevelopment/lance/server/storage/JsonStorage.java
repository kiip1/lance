package nl.kiipdevelopment.lance.server.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonStorage implements Storage<JsonElement> {
	private final Gson gson = new Gson();

	private final Path location;
	private final JsonObject data;
	
	public JsonStorage(Path location) throws IOException, StorageException {
		this.location = location;

		if (location.toFile().createNewFile())
			Files.writeString(location, "{}");

		JsonElement element = JsonParser.parseReader(Files.newBufferedReader(location));
		if (!element.isJsonObject()) throw new StorageException("Stored json is not in object format");
		this.data = element.getAsJsonObject();
	}
	
	@Override
	public JsonElement get(@NotNull String key) {
		String[] path = key.split("\\.");
		if (path.length == 1) return data.get(path[0]);
		
		JsonElement element = data.get(path[0]);
		if (element == null || !element.isJsonObject()) return null;
		
		JsonObject currentObject = element.getAsJsonObject();
		for (int i = 1; i < path.length; i++) {
			element = currentObject.get(path[i]);
			if (element == null) return null;
			if (i < path.length - 1 && !element.isJsonObject()) return null;
			
			currentObject = element.getAsJsonObject();
		}
		
		return currentObject;
	}
	
	@Override
	public boolean exists(String key) {
		return get(key) != null;
	}
	
	@Override
	public void set(@NotNull String key, @NotNull JsonElement value) {
		String[] path = key.split("\\.");
		
		if (path.length == 1) {
			data.add(path[0], value);
			return;
		}
		
		JsonElement element;
		if (data.has(path[0])) {
			element = data.get(path[0]);
			if (!element.isJsonObject()) {
				element = new JsonObject();
				data.add(path[0], element);
			}
		} else {
			element = new JsonObject();
			data.add(path[0], element);
		}
		
		JsonObject currentObject = element.getAsJsonObject();
		for (int i = 1; i < path.length; i++) {
			if (i < path.length - 1) {
				if (currentObject.has(path[i])) {
					element = currentObject.get(path[i]);
					if (!element.isJsonObject()) {
						element = new JsonObject();
						currentObject.add(path[i], element);
					}
				} else {
					element = new JsonObject();
					currentObject.add(path[i], element);
				}
				
				currentObject = element.getAsJsonObject();
			} else {
				currentObject.add(path[i], value);
			}
		}
	}
	
	@Override
	public boolean isJson() {
		return true;
	}
	
	@Override
	public void close() throws IOException {
		Files.writeString(location, gson.toJson(data));
	}
}
