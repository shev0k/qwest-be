package com.qwest.backend.business.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.qwest.backend.configuration.exceptionhandler.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3FileStorageServiceImplTest {

    private S3FileStorageServiceImpl fileStorageService;
    private AmazonS3 s3Client;
    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Client = mock(AmazonS3.class);
        fileStorageService = new S3FileStorageServiceImpl(s3Client);
        fileStorageService.bucketName = bucketName;
    }

    @Test
    void uploadFile_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image content".getBytes());
        URL expectedUrl = new URL("https://example.com/images/image.jpg");

        when(s3Client.getUrl(anyString(), anyString())).thenReturn(expectedUrl);

        String result = fileStorageService.uploadFile(file);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture());
        PutObjectRequest request = requestCaptor.getValue();

        assertNotNull(result);
        assertEquals(expectedUrl.toString(), result);
        assertEquals(bucketName, request.getBucketName());
        assertTrue(request.getKey().contains("image.jpg"));
    }

    @Test
    void uploadFile_IOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("image.jpg");
        when(file.getInputStream()).thenThrow(new IOException("IO error"));

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.uploadFile(file));
        assertEquals("Failed to upload file to S3", exception.getMessage());
    }

    @Test
    void uploadFile_UrlNull() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image content".getBytes());

        when(s3Client.getUrl(anyString(), anyString())).thenReturn(null);

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.uploadFile(file));
        assertEquals("Unable to retrieve URL. Check bucket and key names, and AWS configuration.", exception.getMessage());
    }

    @Test
    void deleteFile_Success() {
        String fileUrl = "https://qwest.s3.eu-north-1.amazonaws.com/test-file.jpg";
        String fileKey = "test-file.jpg";

        doNothing().when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        fileStorageService.deleteFile(fileUrl);

        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(requestCaptor.capture());
        DeleteObjectRequest request = requestCaptor.getValue();

        assertEquals(bucketName, request.getBucketName());
        assertEquals(fileKey, request.getKey());
    }

    @Test
    void deleteFile_InvalidUrl() {
        String invalidUrl = "invalid_url";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileStorageService.deleteFile(invalidUrl));
        assertEquals("Invalid file URL", exception.getMessage());
    }

    @Test
    void deleteFile_FileStorageException() {
        String fileUrl = "https://qwest.s3.eu-north-1.amazonaws.com/test-file.jpg";

        doThrow(new RuntimeException("Delete error")).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.deleteFile(fileUrl));
        assertEquals("Failed to delete file from S3", exception.getMessage());
    }

    @Test
    void deleteFile_FileUrlNull() {
        String fileUrl = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileStorageService.deleteFile(fileUrl));
        assertEquals("Invalid file URL", exception.getMessage());
    }
    @Test

    void deleteFile_FileUrlNotStartingWithBaseUrl() {
        String fileUrl = "https://someotherurl.com/test-file.jpg";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileStorageService.deleteFile(fileUrl));
        assertEquals("Invalid file URL", exception.getMessage());
    }

}
