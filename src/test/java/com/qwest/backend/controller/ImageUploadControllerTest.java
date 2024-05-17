package com.qwest.backend.controller;

import com.qwest.backend.business.FileStorageService;
import com.qwest.backend.configuration.exceptionhandler.FileNotFoundException;
import com.qwest.backend.configuration.exceptionhandler.FileStorageException;
import com.qwest.backend.configuration.security.SecurityConfig;
import com.qwest.backend.configuration.security.token.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageUploadController.class)
@Import({SecurityConfig.class, JwtUtil.class})
class ImageUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void uploadImageTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
        String imageUrl = "https://example.com/images/image.jpg";

        when(fileStorageService.uploadFile(any(MultipartFile.class))).thenReturn(imageUrl);

        mockMvc.perform(multipart("/api/images/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(imageUrl));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void uploadImage_FileStorageException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());

        when(fileStorageService.uploadFile(any(MultipartFile.class))).thenThrow(new FileStorageException("Storage error"));

        mockMvc.perform(multipart("/api/images/upload")
                        .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to upload image: Storage error"));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteImageTest() throws Exception {
        String imageUrl = "https://example.com/images/image.jpg";

        doNothing().when(fileStorageService).deleteFile(imageUrl);

        mockMvc.perform(delete("/api/images/delete")
                        .param("imageUrl", imageUrl))
                .andExpect(status().isOk())
                .andExpect(content().string("Image deleted successfully."));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteImage_FileNotFoundException() throws Exception {
        String imageUrl = "https://example.com/images/image.jpg";

        doThrow(new FileNotFoundException("File not found")).when(fileStorageService).deleteFile(imageUrl);

        mockMvc.perform(delete("/api/images/delete")
                        .param("imageUrl", imageUrl))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Image not found: File not found"));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteImage_FileStorageException() throws Exception {
        String imageUrl = "https://example.com/images/image.jpg";

        doThrow(new FileStorageException("Storage error")).when(fileStorageService).deleteFile(imageUrl);

        mockMvc.perform(delete("/api/images/delete")
                        .param("imageUrl", imageUrl))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to delete image: Storage error"));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteImage_InvalidUrl() throws Exception {
        String imageUrl = "invalid_url";

        doThrow(new IllegalArgumentException("Invalid file URL")).when(fileStorageService).deleteFile(imageUrl);

        mockMvc.perform(delete("/api/images/delete")
                        .param("imageUrl", imageUrl))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid file URL: Invalid file URL"));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void uploadMultipleImagesTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "image1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "image2 content".getBytes());
        List<String> imageUrls = List.of("https://example.com/images/image1.jpg", "https://example.com/images/image2.jpg");

        when(fileStorageService.uploadFile(any(MultipartFile.class)))
                .thenReturn(imageUrls.get(0))
                .thenReturn(imageUrls.get(1));

        mockMvc.perform(multipart("/api/images/upload/multiple")
                        .file(file1)
                        .file(file2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is(imageUrls.get(0))))
                .andExpect(jsonPath("$[1]", is(imageUrls.get(1))));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void uploadMultipleImages_FileStorageException() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "image1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "image2 content".getBytes());

        when(fileStorageService.uploadFile(any(MultipartFile.class)))
                .thenReturn("https://example.com/images/image1.jpg")
                .thenThrow(new FileStorageException("Storage error"));

        mockMvc.perform(multipart("/api/images/upload/multiple")
                        .file(file1)
                        .file(file2))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is("Failed to upload one or more images: Storage error")));
    }


    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void uploadMultipleImages_UnexpectedError() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "image1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "image2 content".getBytes());

        when(fileStorageService.uploadFile(any(MultipartFile.class)))
                .thenReturn("https://example.com/images/image1.jpg")
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(multipart("/api/images/upload/multiple")
                        .file(file1)
                        .file(file2))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is("Unexpected error occurred: Unexpected error")));
    }

}
