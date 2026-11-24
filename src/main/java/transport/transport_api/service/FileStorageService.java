
package transport.transport_api.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import transport.transport_api.exception.ResourceNotFoundException;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE =
            10L * 1024 * 1024;

    private final Path storageDirectory;

    public FileStorageService(
            @Value("${app.storage.location:uploads}")
            String storageLocation) {

        this.storageDirectory = Path.of(storageLocation)
                .toAbsolutePath()
                .normalize();
    }

    public String store(MultipartFile file, String type) {

        String extension = validate(file, type);

        String fileName = UUID.randomUUID() + extension;
        Path destination = storageDirectory.resolve(fileName);

        try {
            Files.createDirectories(storageDirectory);

            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination);
            }

            return fileName;

        } catch (IOException ex) {

            try {
                Files.deleteIfExists(destination);
            } catch (IOException cleanupException) {
                ex.addSuppressed(cleanupException);
            }

            throw new IllegalStateException(
                    "Could not store file", ex
            );
        }
    }

    public String validate(MultipartFile file, String type) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File cannot be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File exceeds the maximum size of 10 MB"
            );
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null
                || !originalName.contains(".")) {
            throw new IllegalArgumentException(
                    "File extension is required"
            );
        }

        String extension = originalName.substring(
                originalName.lastIndexOf(".")
        ).toLowerCase(Locale.ROOT);

        boolean validExtension = switch (type) {

            case "document" -> extension.equals(".pdf");

            case "image" -> extension.equals(".jpg")
                    || extension.equals(".jpeg")
                    || extension.equals(".png");

            default -> false;
        };

        if (!validExtension) {
            throw new IllegalArgumentException(
                    "Invalid file extension"
            );
        }

        validateSignature(file, extension);

        return extension;
    }

    private void validateSignature(
            MultipartFile file,
            String extension) {

        try (InputStream input = file.getInputStream()) {

            byte[] header = input.readNBytes(8);

            boolean valid = switch (extension) {

                case ".pdf" -> startsWith(header,
                        new byte[]{
                            0x25, 0x50, 0x44, 0x46, 0x2D
                        });

                case ".png" -> startsWith(header,
                        new byte[]{
                            (byte) 0x89, 0x50, 0x4E, 0x47,
                            0x0D, 0x0A, 0x1A, 0x0A
                        });

                case ".jpg", ".jpeg" -> startsWith(header,
                        new byte[]{
                            (byte) 0xFF,
                            (byte) 0xD8,
                            (byte) 0xFF
                        });

                default -> false;
            };

            if (!valid) {
                throw new IllegalArgumentException(
                        "File content does not match its extension"
                );
            }

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not read file", ex
            );
        }
    }

    private boolean startsWith(
            byte[] content,
            byte[] signature) {

        return content.length >= signature.length
                && Arrays.equals(
                    Arrays.copyOf(content, signature.length),
                    signature
                );
    }

    public Path load(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new ResourceNotFoundException(
                    "Attachment not found"
            );
        }

        Path filePath = storageDirectory
                .resolve(fileName)
                .normalize();

        if (!storageDirectory.equals(filePath.getParent())) {
            throw new IllegalArgumentException(
                    "Invalid file path"
            );
        }

        if (!Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException(
                    "Attachment not found"
            );
        }

        return filePath;
    }

    public void delete(String fileName) {

        Path filePath = load(fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not delete file", ex
            );
        }
    }
}