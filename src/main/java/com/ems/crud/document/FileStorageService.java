package com.ems.crud.document;

import com.ems.crud.config.AppProperties;
import com.ems.crud.exception.BadRequestException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path uploadRoot;

	public FileStorageService(AppProperties appProperties) {
		this.uploadRoot = Paths.get(appProperties.upload().directory()).toAbsolutePath().normalize();
	}

	public String storeFile(MultipartFile file, String subDirectory) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException("File must not be empty");
		}

		try {
			Path targetDirectory = uploadRoot.resolve(subDirectory);
			Files.createDirectories(targetDirectory);

			String extension = getExtension(file.getOriginalFilename());
			String storedFileName = UUID.randomUUID() + extension;
			Path targetPath = targetDirectory.resolve(storedFileName);
			Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

			return uploadRoot.relativize(targetPath).toString().replace('\\', '/');
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to store file", exception);
		}
	}

	private String getExtension(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf('.'));
	}
}
