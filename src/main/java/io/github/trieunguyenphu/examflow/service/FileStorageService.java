package io.github.trieunguyenphu.examflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_FORMATS = Set.of("jpeg", "png");
    private static final long MAX_PIXELS = 20_000_000L;
    private final Path storageDirectory;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        storageDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create the upload directory.", exception);
        }
    }

    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Choose a JPEG or PNG image to upload.");
        }

        try (InputStream input = file.getInputStream(); ImageInputStream imageInput = ImageIO.createImageInputStream(input)) {
            if (imageInput == null) {
                throw new IllegalArgumentException("The uploaded file is not a valid image.");
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw new IllegalArgumentException("The uploaded file is not a valid image.");
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(imageInput, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (!ALLOWED_FORMATS.contains(format) || width <= 0 || height <= 0 || (long) width * height > MAX_PIXELS) {
                    throw new IllegalArgumentException("Use a JPEG or PNG image no larger than 20 megapixels.");
                }

                BufferedImage image = reader.read(0);
                String outputFormat = format.equals("png") ? "png" : "jpg";
                String filename = UUID.randomUUID() + "." + outputFormat;
                Path target = storageDirectory.resolve(filename).normalize();
                if (!target.getParent().equals(storageDirectory) || !ImageIO.write(image, outputFormat, target.toFile())) {
                    throw new IOException("Could not encode the profile image.");
                }
                return "/uploads/" + filename;
            } finally {
                reader.dispose();
            }
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not store the profile image.", exception);
        }
    }

    public void delete(String webPath) {
        if (webPath == null || !webPath.startsWith("/uploads/")) return;
        String filename = webPath.substring("/uploads/".length());
        if (!filename.matches("[0-9a-f-]{36}\\.(jpg|png)")) return;
        try {
            Files.deleteIfExists(storageDirectory.resolve(filename).normalize());
        } catch (IOException ignored) {
            // A stale profile image should not prevent the account update.
        }
    }
}
