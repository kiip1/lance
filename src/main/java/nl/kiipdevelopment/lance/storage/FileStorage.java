package nl.kiipdevelopment.lance.storage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileStorage implements Storage {
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
	
	@Override
	public boolean set(@NotNull String key, byte[] value) throws Exception {
		location.mkdirs();

		if (location.isDirectory()) {
			File target = new File(location, key);

			target.createNewFile();

			Files.write(target.toPath(), value);
		}

		return true;
	}

	@Override
	public boolean exists(String key) throws Exception {
		return get(key) != null;
	}

	@Override
	public List<String> list() {
		location.mkdirs();

		List<String> list = new ArrayList<>();

		if (location.isDirectory()) {
			String[] files = location.list();

			if (files == null) {
				return list;
			}

			list.addAll(Arrays.asList(files));
		}

		return list;
	}

	@Override
	public void save() {}
}
