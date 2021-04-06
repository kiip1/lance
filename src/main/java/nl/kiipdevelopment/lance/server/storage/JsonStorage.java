package nl.kiipdevelopment.lance.server.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class JsonStorage implements Storage<JsonElement> {
	private final Gson gson = new Gson();

	private final File location;
	private final JsonObject data;
	
	public JsonStorage(File location) throws IOException {
		this.location = location;

		if (location.createNewFile()) {
			Files.write(location.toPath(), "{}".getBytes(StandardCharsets.UTF_8));
		}

		JsonElement element = new JsonParser().parse(Files.newBufferedReader(location.toPath()));
		if (!element.isJsonObject()) element = new JsonObject();
		this.data = element.getAsJsonObject();
	}
	
	@Override
	public synchronized JsonElement get(@NotNull String key) {
		String[] path = key.split("\\.");
		if (path.length == 0) return null;
		if (path.length == 1) return data.get(path[0]);
		
		JsonElement element = data.get(path[0]);
		if (element == null || !element.isJsonObject()) return null;
		
		JsonObject currentObject = element.getAsJsonObject();
		for (int i = 1; i < path.length; i++) {
			element = currentObject.get(path[i]);
			if (element == null) return null;
			if (i < path.length - 1) {
				if (!element.isJsonObject()) return null;
				currentObject = element.getAsJsonObject();
			}
		}
		
		return element;
	}
	
	@Override
	public synchronized boolean exists(String key) {
		return get(key) != null;
	}
	
	@Override
	public synchronized void set(@NotNull String key, JsonElement value) {
		String[] path = key.split("\\.");
		if (path.length == 0) return;
		
		if (path.length == 1) {
			if (value == null) {
				data.remove(path[0]);
			} else {
				data.add(path[0], value);
			}
			return;
		}
		
		JsonElement element;
		if (data.has(path[0])) {
			element = data.get(path[0]);
			if (!element.isJsonObject()) {
				if (value == null) return;
				
				element = new JsonObject();
				data.add(path[0], element);
			}
		} else {
			if (value == null) return;
			
			element = new JsonObject();
			data.add(path[0], element);
		}
		
		JsonObject currentObject = element.getAsJsonObject();
		for (int i = 1; i < path.length; i++) {
			if (i < path.length - 1) {
				if (currentObject.has(path[i])) {
					element = currentObject.get(path[i]);
					if (!element.isJsonObject()) {
						if (value == null) return;
						
						element = new JsonObject();
						currentObject.add(path[i], element);
					}
				} else {
					if (value == null) return;
					
					element = new JsonObject();
					currentObject.add(path[i], element);
				}
				
				currentObject = element.getAsJsonObject();
			} else {
				if (value == null) {
					currentObject.remove(path[i]);
				} else {
					currentObject.add(path[i], value);
				}
			}
		}
	}
	
	@Override
	public boolean isJson() {
		return true;
	}

	@Override
	public synchronized void save() throws IOException {
		Files.write(location.toPath(), gson.toJson(data).getBytes(StandardCharsets.UTF_8));
	}
}
