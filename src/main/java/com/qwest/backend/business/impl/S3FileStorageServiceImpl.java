package com.qwest.backend.business.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.qwest.backend.business.FileStorageService;
import com.qwest.backend.configuration.exceptionhandler.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3FileStorageServiceImpl implements FileStorageService {

    private final AmazonS3 s3Client;

    @Value("${s3.bucket-name}")
    String bucketName;

    public S3FileStorageServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        String fileKey = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata));

            URL url = s3Client.getUrl(bucketName, fileKey);
            if (url == null) {
                throw new FileStorageException("Unable to retrieve URL. Check bucket and key names, and AWS configuration.");
            }
            return url.toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to upload file to S3", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        String baseUrl = "https://qwest.s3.eu-north-1.amazonaws.com/";
        if (fileUrl != null && fileUrl.startsWith(baseUrl)) {
            String fileKey = fileUrl.substring(baseUrl.length());
            try {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
            } catch (Exception e) {
                throw new FileStorageException("Failed to delete file from S3", e);
            }
        } else {
            throw new IllegalArgumentException("Invalid file URL");
        }
    }
}
