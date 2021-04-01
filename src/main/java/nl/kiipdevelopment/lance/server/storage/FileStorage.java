package nl.kiipdevelopment.lance.server.storage;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorage implements Storage<byte[]> {
	private final File location;

	public FileStorage(File location) {
		this.location = location;

		location.mkdirs();
	}
	
	@Override
	public byte[] get(@NotNull String key) throws Exception {
		location.mkdirs();

		if (location.isDirectory()) {
			File[] files = location.listFiles();

			if (files == null)
				return null;

			for (File file : files)
				if (file.getName().equals(key)) {
					return Files.readAllBytes(file.toPath());
				}
		}

		return null;
	}
	
	public JsonArray list() {
		location.mkdirs();
		
		JsonArray array = new JsonArray();
		
		if (location.isDirectory()) {
			String[] files = location.list();
			
			if (files == null)
				return array;
			
			for (String file : files)
				array.add(file);
		}
		
		return array;
	}
	
	@Override
	public boolean exists(String key) throws Exception {
		return get(key) != null;
	}
	
	@Override
	public void set(@NotNull String key, byte[] value) throws Exception {
		location.mkdirs();

		if (location.isDirectory()) {
			File target = new File(location, key);

			target.createNewFile();

			Files.write(target.toPath(), value);
		}
	}

	@Override
	public void save() {}
}
