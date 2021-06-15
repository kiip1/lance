package nl.kiipdevelopment.lance.storage;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class JsonStorage implements Storage {
	private final Gson gson = new Gson();

	private final File location;
	private final JsonObject data;
	
	public JsonStorage(File location) throws IOException {
		this.location = location;

		if (location.createNewFile()) {
			Files.writeString(location.toPath(), "{}");
		}

		JsonElement element = new JsonParser().parse(Files.newBufferedReader(location.toPath()));

		if (!element.isJsonObject()) {
			element = new JsonObject();
		}

		this.data = element.getAsJsonObject();
	}

	@Override
	public byte[] get(String key) {
		return gson.toJson(get0(key)).getBytes();
	}

	@Override
	public boolean set(String key, byte[] value) {
		return set0(key, gson.fromJson(new String(value), JsonElement.class));
	}

	public synchronized JsonElement get0(@NotNull String key) {
		String[] path = key.split("\\.");

		if (path.length == 0) {
			return null;
		}

		if (path.length == 1) {
			return data.get(path[0]);
		}
		
		JsonElement element = data.get(path[0]);

		if (element == null || !element.isJsonObject()) {
			return null;
		}
		
		JsonObject currentObject = element.getAsJsonObject();
		for (int i = 1; i < path.length; i++) {
			element = currentObject.get(path[i]);

			if (element == null) {
				return null;
			}

			if (i < path.length - 1) {
				if (!element.isJsonObject()) {
					return null;
				}

				currentObject = element.getAsJsonObject();
			}
		}
		
		return element;
	}

	public synchronized boolean set0(@NotNull String key, JsonElement value) {
		String[] path = key.split("\\.");

		if (path.length == 0) {
			return false;
		}
		
		if (path.length == 1) {
			if (value == null) {
				data.remove(path[0]);
			} else {
				data.add(path[0], value);
			}

			return true;
		}
		
		JsonElement element;
		if (data.has(path[0])) {
			element = data.get(path[0]);
			if (!element.isJsonObject()) {
				if (value == null) {
					return false;
				}
				
				element = new JsonObject();
				data.add(path[0], element);
			}
		} else {
			if (value == null) {
				return false;
			}
			
			element = new JsonObject();
			data.add(path[0], element);
		}
		
		JsonObject currentObject = element.getAsJsonObject();
		for (int i = 1; i < path.length; i++) {
			if (i < path.length - 1) {
				if (currentObject.has(path[i])) {
					element = currentObject.get(path[i]);
					if (!element.isJsonObject()) {
						if (value == null) {
							return true;
						}
						
						element = new JsonObject();
						currentObject.add(path[i], element);
					}
				} else {
					if (value == null) {
						return true;
					}
					
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

		return false;
	}

	@Override
	public List<String> list() {
		return data.entrySet()
			.stream()
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());
	}

	@Override
	public synchronized boolean exists(String key) {
		return get(key) != null;
	}

	@Override
	public synchronized void save() throws IOException {
		Files.writeString(location.toPath(), gson.toJson(data));
	}

	@Override
	public boolean isJson() {
		return true;
	}
}
