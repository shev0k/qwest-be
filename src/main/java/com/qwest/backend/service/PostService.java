package com.qwest.backend.service;

import com.qwest.backend.DTO.PostDTO;
import java.util.List;
import java.util.Optional;

public interface PostService {
    List<PostDTO> findAll();
    Optional<PostDTO> findById(Long id);
    PostDTO save(PostDTO postDTO);
    void deleteById(Long id);
}
