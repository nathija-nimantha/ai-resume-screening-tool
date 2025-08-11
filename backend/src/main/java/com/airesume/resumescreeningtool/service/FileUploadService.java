package com.airesume.resumescreeningtool.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Value("${file.upload.max-size}")
    private long maxSize;

    @Value("${file.upload.supported-extensions}")
    private String supportedExtensions;

    private List<String> getSupportedExtensions() {
        return Arrays.asList(supportedExtensions.split(","));
    }

    /**
     * Stores the uploaded file and returns file information
     */
    public FileUploadResult storeFile(MultipartFile file) throws IOException {
        validateFile(file);

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilenameRaw = file.getOriginalFilename();
        if (originalFilenameRaw == null) {
            throw new IllegalArgumentException("File must have a name");
        }
        String originalFilename = StringUtils.cleanPath(originalFilenameRaw);
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(originalFilename);

        // Store file
        Path targetLocation = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Extract text content
        String extractedText = extractTextFromFile(file, fileExtension);

        logger.info("File stored successfully: {}", uniqueFilename);

        return FileUploadResult.builder()
                .originalFilename(originalFilename)
                .storedFilename(uniqueFilename)
                .filePath(targetLocation.toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .extractedText(extractedText)
                .build();
    }

    /**
     * Validates the uploaded file
     */
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + (maxSize / 1024 / 1024) + "MB");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("File must have a name");
        }
        String filename = StringUtils.cleanPath(originalName);
        if (filename.contains("..")) {
            throw new IllegalArgumentException("Invalid filename: " + filename + ". Filenames cannot contain '..'.");
        }

        String contentType = file.getContentType();
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));
        if (!allowedTypeList.contains(contentType)) {
            throw new IllegalArgumentException("File type not supported: " + contentType);
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!getSupportedExtensions().contains(extension)) {
            throw new IllegalArgumentException("Unsupported file extension: " + extension + ". Supported extensions are: " + getSupportedExtensions());
        }
    }

    /**
     * Extracts text content from the uploaded file
     */
    private String extractTextFromFile(MultipartFile file, String extension) {
        try {
            switch (extension.toLowerCase()) {
                case "pdf" -> {
                    return extractTextFromPDF(file.getInputStream());
                }
                case "doc" -> {
                    return extractTextFromDOC(file.getInputStream());
                }
                case "docx" -> {
                    return extractTextFromDOCX(file.getInputStream());
                }
                case "txt" -> {
                    return extractTextFromTXT(file.getInputStream());
                }
                default -> {
                    logger.warn("Unsupported file type for text extraction: {}", extension);
                    return "";
                }
            }
        } catch (IOException e) {
            logger.error("Error extracting text from file: {}", e.getMessage());
            throw new RuntimeException("Failed to extract text from file", e);
        }
    }

    /**
     * Extracts text from PDF file
     */
    private String extractTextFromPDF(InputStream inputStream) throws IOException {
        byte[] fileBytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Extracts text from DOC file
     */
    private String extractTextFromDOC(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }

    /**
     * Extracts text from DOCX file
     */
    private String extractTextFromDOCX(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    /**
     * Extracts text from a TXT file.
     *
     * @param inputStream the input stream of the file
     * @return the extracted text
     * @throws IOException if an I/O error occurs
     */
    private String extractTextFromTXT(InputStream inputStream) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line).append(System.lineSeparator());
            }
        }
        return textBuilder.toString();
    }

    /**
     * Gets file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * Generates unique filename to prevent conflicts
     */
    private String generateUniqueFilename(String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFilename);
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        
        // Clean the base name to remove any special characters
        baseName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        return String.format("%s_%s_%s.%s", baseName, timestamp, uuid, extension);
    }

    /**
     * Deletes a file from storage
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
            logger.info("File deleted successfully: {}", filename);
            return true;
        } catch (IOException e) {
            logger.error("Error deleting file '{}': {}", filename, e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a file exists in storage
     */
    public boolean fileExists(String filename) {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        return Files.exists(filePath);
    }

    /**
     * Gets the upload directory path
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * Result class for file upload operations
     */
    public static class FileUploadResult {
        private String originalFilename;
        private String storedFilename;
        private String filePath;
        private long fileSize;
        private String contentType;
        private String extractedText;

        // Builder pattern
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final FileUploadResult result = new FileUploadResult();

            public Builder originalFilename(String originalFilename) {
                result.originalFilename = originalFilename;
                return this;
            }

            public Builder storedFilename(String storedFilename) {
                result.storedFilename = storedFilename;
                return this;
            }

            public Builder filePath(String filePath) {
                result.filePath = filePath;
                return this;
            }

            public Builder fileSize(long fileSize) {
                result.fileSize = fileSize;
                return this;
            }

            public Builder contentType(String contentType) {
                result.contentType = contentType;
                return this;
            }

            public Builder extractedText(String extractedText) {
                result.extractedText = extractedText;
                return this;
            }

            public FileUploadResult build() {
                return result;
            }
        }

        // Getters
        public String getOriginalFilename() { return originalFilename; }
        public String getStoredFilename() { return storedFilename; }
        public String getFilePath() { return filePath; }
        public long getFileSize() { return fileSize; }
        public String getContentType() { return contentType; }
        public String getExtractedText() { return extractedText; }
    }
}
