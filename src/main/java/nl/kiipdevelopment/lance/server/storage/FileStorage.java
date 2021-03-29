package nl.kiipdevelopment.lance.server.storage;

import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStorage implements Storage<byte[]> {
	private Path location;

	public FileStorage(Path location) {
		this.location = location;

		location.toFile().mkdirs();
	}
	
	@Override
	public byte[] get(@NotNull String key) throws Exception {
		File dir = location.toFile();
		dir.mkdirs();

		if (dir.isDirectory()) {
			File[] files = dir.listFiles();

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
		File dir = location.toFile();
		dir.mkdirs();
		
		JsonArray array = new JsonArray();
		
		if (dir.isDirectory()) {
			String[] files = dir.list();
			
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
		File dir = location.toFile();
		dir.mkdirs();

		if (dir.isDirectory()) {
			Path target = location.resolve(key);

			target.toFile().createNewFile();

			Files.write(target, value);
		}
	}

	@Override
	public void save() {}
}
