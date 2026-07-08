package com.example.demo.service;

import com.example.demo.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageServiceTest {

    @TempDir
    Path temporaryDirectory;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService =
                new FileStorageService(
                        temporaryDirectory.toString()
                );
    }

    @Test
    void storeImageShouldSaveValidImage()
            throws IOException {

        byte[] imageContent =
                "fake-image-content"
                        .getBytes(StandardCharsets.UTF_8);

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "student-photo.jpg",
                        "image/jpeg",
                        imageContent
                );

        String relativePath =
                fileStorageService.storeImage(
                        image,
                        "students"
                );

        Path storedFile =
                temporaryDirectory.resolve(
                        relativePath
                );

        assertTrue(
                relativePath.startsWith(
                        "students/"
                )
        );

        assertTrue(
                relativePath.endsWith(".jpg")
        );

        assertTrue(
                Files.exists(storedFile)
        );

        assertArrayEquals(
                imageContent,
                Files.readAllBytes(storedFile)
        );
    }

    @Test
    void storeImageShouldRejectInvalidFileType() {

        MockMultipartFile textFile =
                new MockMultipartFile(
                        "file",
                        "notes.txt",
                        "text/plain",
                        "This is not an image."
                                .getBytes(StandardCharsets.UTF_8)
                );

        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileStorageService.storeImage(
                                textFile,
                                "students"
                        )
                );

        assertEquals(
                "Only JPG, PNG and WEBP images are allowed.",
                exception.getMessage()
        );
    }

    @Test
    void storeImageShouldRejectInvalidFolder() {

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "image.png",
                        "image/png",
                        new byte[]{1, 2, 3}
                );

        InvalidFileException exception =
                assertThrows(
                        InvalidFileException.class,
                        () -> fileStorageService.storeImage(
                                image,
                                "private"
                        )
                );

        assertEquals(
                "Invalid upload folder.",
                exception.getMessage()
        );
    }

    @Test

    void loadImageShouldReturnStoredImage()
            throws IOException {

        byte[] imageContent =
                new byte[]{10, 20, 30, 40};

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "project-image.png",
                        "image/png",
                        imageContent
                );

        String relativePath =
                fileStorageService.storeImage(
                        image,
                        "projects"
                );

        String filename =
                Path.of(relativePath)
                        .getFileName()
                        .toString();

        Resource resource =
                fileStorageService.loadImage(
                        "projects",
                        filename
                );

        assertTrue(resource.exists());
        assertTrue(resource.isReadable());

        /*
         * try-with-resources sayesinde InputStream
         * test bitmeden kapatılır.
         */
        try (InputStream inputStream =
                     resource.getInputStream()) {

            assertArrayEquals(
                    imageContent,
                    inputStream.readAllBytes()
            );
        }
    }

    @Test
    void deleteImageByUrlShouldDeleteStoredImage()
            throws IOException {

        MockMultipartFile image =
                new MockMultipartFile(
                        "file",
                        "news-image.webp",
                        "image/webp",
                        new byte[]{5, 10, 15}
                );

        String relativePath =
                fileStorageService.storeImage(
                        image,
                        "news"
                );

        Path storedFile =
                temporaryDirectory.resolve(
                        relativePath
                );

        assertTrue(
                Files.exists(storedFile)
        );

        String imageUrl =
                "/api/media/files/"
                        + relativePath;

        fileStorageService.deleteImageByUrl(
                imageUrl
        );

        assertFalse(
                Files.exists(storedFile)
        );
    }

    @Test
    void deleteImageByUrlShouldIgnoreExternalUrl() {

        fileStorageService.deleteImageByUrl(
                "https://example.com/image.jpg"
        );

        /*
         * Metot dış bağlantıları silmeye çalışmadan
         * tamamlanmalı ve exception atmamalı.
         */
        assertTrue(
                Files.exists(temporaryDirectory)
        );
    }

    @Test
    void initializeStorageShouldCreateAllowedFolders() {

        assertTrue(
                Files.isDirectory(
                        temporaryDirectory.resolve(
                                "students"
                        )
                )
        );

        assertTrue(
                Files.isDirectory(
                        temporaryDirectory.resolve(
                                "projects"
                        )
                )
        );

        assertTrue(
                Files.isDirectory(
                        temporaryDirectory.resolve(
                                "news"
                        )
                )
        );
    }
}