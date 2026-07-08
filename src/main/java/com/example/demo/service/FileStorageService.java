package com.example.demo.service;

import com.example.demo.exception.FileStorageException;
import com.example.demo.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.exception.ResourceNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.InvalidPathException;

@Service
public class FileStorageService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(FileStorageService.class);

    private static final String FILE_URL_PREFIX =
            "/api/media/files/";


    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(
                    ".jpg",
                    ".jpeg",
                    ".png",
                    ".webp"
            );

    private static final Set<String> ALLOWED_FOLDERS =
            Set.of(
                    "students",
                    "projects",
                    "news"
            );

    private final Path uploadRoot;

    public FileStorageService(
            @Value("${app.upload.dir:uploads}")
            String uploadDirectory
    ) {
        this.uploadRoot = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        initializeStorage();
    }

    private void initializeStorage() {
        try {
            Files.createDirectories(uploadRoot);

            for (String folder : ALLOWED_FOLDERS) {
                Files.createDirectories(
                        uploadRoot.resolve(folder)
                );
            }
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Upload directories could not be created.",
                    exception
            );
        }
    }

    public String storeImage(
            MultipartFile file,
            String folder
    ) {
        validateFolder(folder);
        validateImage(file);

        String originalFilename =
                StringUtils.cleanPath(
                        file.getOriginalFilename() == null
                                ? ""
                                : file.getOriginalFilename()
                );

        String extension =
                getExtension(originalFilename);

        String generatedFilename =
                UUID.randomUUID() + extension;

        Path targetFolder =
                uploadRoot.resolve(folder).normalize();

        Path targetFile =
                targetFolder.resolve(generatedFilename)
                        .normalize();

        if (!targetFile.startsWith(targetFolder)) {
            throw new InvalidFileException(
                    "Invalid file path."
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    targetFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new FileStorageException(
                    "The image could not be saved.",
                    exception
            );
        }

        return folder + "/" + generatedFilename;
    }

    public Resource loadImage(
            String folder,
            String filename
    ) {
        validateFolder(folder);

        if (filename == null
                || filename.isBlank()
                || filename.contains("..")
                || filename.contains("/")
                || filename.contains("\\")) {

            throw new InvalidFileException(
                    "Invalid filename."
            );
        }

        Path folderPath =
                uploadRoot.resolve(folder).normalize();

        Path filePath =
                folderPath.resolve(filename).normalize();

        if (!filePath.startsWith(folderPath)) {
            throw new InvalidFileException(
                    "Invalid file path."
            );
        }

        try {
            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists()
                    || !resource.isReadable()) {

                throw new FileStorageException(
                        "Image could not be found."
                );
            }

            return resource;

        } catch (MalformedURLException exception) {
            throw new FileStorageException(
                    "Image path is invalid.",
                    exception
            );
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(
                    "Please select an image."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(
                contentType.toLowerCase()
        )) {

            throw new InvalidFileException(
                    "Only JPG, PNG and WEBP images are allowed."
            );
        }

        String originalFilename =
                StringUtils.cleanPath(
                        file.getOriginalFilename() == null
                                ? ""
                                : file.getOriginalFilename()
                );

        if (originalFilename.contains("..")) {
            throw new InvalidFileException(
                    "Invalid filename."
            );
        }

        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileException(
                    "Only JPG, PNG and WEBP images are allowed."
            );
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0) {
            throw new InvalidFileException(
                    "The image must have a file extension."
            );
        }

        return filename.substring(dotIndex)
                .toLowerCase();
    }

    private void validateFolder(String folder) {
        if (!ALLOWED_FOLDERS.contains(folder)) {
            throw new InvalidFileException(
                    "Invalid upload folder."
            );
        }
    }
    public void deleteImageByUrl(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        /*
         * Harici URL'leri veya eski manuel yolları silmeye çalışma.
         */
        if (!imageUrl.startsWith(FILE_URL_PREFIX)) {
            return;
        }

        String relativeValue =
                imageUrl.substring(FILE_URL_PREFIX.length());

        try {
            Path relativePath =
                    Path.of(relativeValue).normalize();

            /*
             * Yol şu formatta olmalı:
             * students/filename.jpg
             * projects/filename.jpg
             * news/filename.jpg
             */
            if (relativePath.getNameCount() != 2) {
                LOGGER.warn(
                        "Invalid stored image path was ignored: {}",
                        imageUrl
                );
                return;
            }

            String folder =
                    relativePath.getName(0).toString();

            if (!ALLOWED_FOLDERS.contains(folder)) {
                LOGGER.warn(
                        "Invalid image folder was ignored: {}",
                        folder
                );
                return;
            }

            Path targetFile =
                    uploadRoot.resolve(relativePath).normalize();

            /*
             * uploads klasörünün dışına çıkılmasını engeller.
             */
            if (!targetFile.startsWith(uploadRoot)) {
                LOGGER.warn(
                        "Unsafe image path was ignored: {}",
                        imageUrl
                );
                return;
            }

            if (Files.isDirectory(targetFile)) {
                LOGGER.warn(
                        "Directory deletion attempt was ignored: {}",
                        targetFile
                );
                return;
            }

            boolean deleted =
                    Files.deleteIfExists(targetFile);

            if (deleted) {
                LOGGER.info(
                        "Old image deleted: {}",
                        targetFile
                );
            }

        } catch (InvalidPathException | IOException exception) {
            /*
             * Veritabanı güncellemesi başarılıyken yalnızca eski
             * görsel silinemedi diye bütün isteği başarısız yapmıyoruz.
             */
            LOGGER.warn(
                    "Image cleanup failed for URL: {}",
                    imageUrl,
                    exception
            );
        }
    }
}