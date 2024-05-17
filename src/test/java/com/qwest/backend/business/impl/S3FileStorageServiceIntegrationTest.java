package com.qwest.backend.business.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class S3FileStorageServiceIntegrationTest {

    @Autowired
    private S3FileStorageServiceImpl fileStorageService;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${s3.bucket-name}")
    private String bucketName;

    private String uploadedFileKey;

    @Test
    void testUploadFile() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "testfile.txt",
                "testfile.txt",
                "text/plain",
                "Hello, world!".getBytes());

        // Upload the file
        String fileUrl = fileStorageService.uploadFile(file);
        assertNotNull(fileUrl);
        System.out.println("Uploaded file URL: " + fileUrl);

        // Extract file key from URL
        URL url = new URL(fileUrl);
        uploadedFileKey = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);

        // Directly check file presence
        ObjectMetadata metadata = amazonS3.getObjectMetadata(bucketName, uploadedFileKey);
        assertNotNull(metadata, "The file metadata should not be null, indicates file exists.");

        assertTrue(amazonS3.doesObjectExist(bucketName, uploadedFileKey), "Check directly if file exists in S3.");
    }


    @AfterEach
    public void tearDown() {
        if (uploadedFileKey != null && amazonS3.doesObjectExist(bucketName, uploadedFileKey)) {
            amazonS3.deleteObject(bucketName, uploadedFileKey); // Clean up the file from S3
        }
    }
}
