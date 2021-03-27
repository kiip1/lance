package nl.kiipdevelopment.lance.server.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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

	private Path location;
	private Map<String, JsonElement> data = new HashMap<>();
	
	public JsonStorage(Path location) {
		try {
			this.location = location;

			if (location.toFile().createNewFile())
				Files.writeString(location, "{}");

			Type type = new TypeToken<Map<String, JsonElement>>() {}.getType();
			data = gson.fromJson(Files.readString(location), type);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public JsonElement get(@NotNull String key) {
		return data.get(key);
	}
	
	@Override
	public boolean exists(String key) {
		return get(key) != null;
	}
	
	@Override
	public void set(@NotNull String key, @NotNull JsonElement value) {
		data.put(key, value);
	}
	
	@Override
	public void close() throws IOException {
		Files.writeString(location, gson.toJson(data));
	}
}
