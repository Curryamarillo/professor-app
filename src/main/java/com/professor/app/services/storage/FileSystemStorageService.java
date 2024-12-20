package com.professor.app.services.storage;

import com.professor.app.config.StorageProperties;
import com.professor.app.exceptions.StorageException;
import com.professor.app.exceptions.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements  StorageService {


    private final Path rootLocation;

    private final StorageProperties storageProperties;



    @Autowired
    public FileSystemStorageService(StorageProperties properties, StorageProperties storageProperties) {
        this.storageProperties = storageProperties;

        if(properties.getLocation().trim().isEmpty()){
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            String username = getUsername();
            Path userDirectory = this.rootLocation.resolve(username);

            Files.createDirectories(userDirectory);

            Path destinationFile = userDirectory.resolve(
                            Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(userDirectory.toAbsolutePath())) {
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            String mimeType = file.getContentType();

            if (!storageProperties.getSUPPORTED_MIME_TYPES().contains(mimeType)) {
                throw new UnsupportedOperationException("File type not supported: " + mimeType);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            String username =getUsername();
            Path userDirectory = this.rootLocation.resolve(username);
            try (Stream<Path> stream = Files.walk(userDirectory, 1)) {
                return stream
                        .filter(path -> !path.equals(userDirectory))
                        .map(userDirectory::relativize);
        }

        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }


    @Override
    public Path load(String filename) {
        String username = getUsername();
        return rootLocation.resolve(username).resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        try (Stream<Path> stream = Files.walk(this.rootLocation)) {
            stream.map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            throw new StorageException("Failed to delete file: " + file.getAbsolutePath());
                        }
                    });
        } catch (IOException e) {
            throw new StorageException("Could not delete files", e);
        }
    }



    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    // Method to get username from Security Context
    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new StorageException("User is not authenticated");
        }
        return authentication.getName();
    }
}
