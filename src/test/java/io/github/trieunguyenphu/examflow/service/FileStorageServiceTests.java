package io.github.trieunguyenphu.examflow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTests {

    @TempDir
    Path uploadDirectory;

    @Test
    void storesAValidatedImageAndRemovesMetadataByReencoding() throws Exception {
        BufferedImage source = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(source, "png", bytes);
        MockMultipartFile upload = new MockMultipartFile("file", "avatar.png", "image/png", bytes.toByteArray());
        FileStorageService storage = new FileStorageService(uploadDirectory.toString());

        String path = storage.saveProfileImage(upload);

        assertThat(path).startsWith("/uploads/").endsWith(".png");
        assertThat(Files.exists(uploadDirectory.resolve(path.substring("/uploads/".length())))).isTrue();
        storage.delete(path);
        assertThat(Files.list(uploadDirectory)).isEmpty();
    }

    @Test
    void rejectsNonImageContent() {
        FileStorageService storage = new FileStorageService(uploadDirectory.toString());
        MockMultipartFile upload = new MockMultipartFile("file", "avatar.png", "image/png", "not an image".getBytes());

        assertThatThrownBy(() -> storage.saveProfileImage(upload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valid image");
    }
}
