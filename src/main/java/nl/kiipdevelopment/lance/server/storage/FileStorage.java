package nl.kiipdevelopment.lance.server.storage;

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
	
	@Override
	public boolean exists(String key) throws Exception {
		return get(key) != null;
	}
	
	@Override
	public void set(@NotNull String key, byte[] value) throws Exception {
		File dir = location.toFile();

		dir.mkdirs();

		if (dir.isDirectory()) {
			try {
				Path target = location.resolve(key);

				target.toFile().createNewFile();

				Files.write(target, value);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() {}
}
