package com.qwest.backend.service.impl;

import com.qwest.backend.domain.Post;
import com.qwest.backend.DTO.PostDTO;
import com.qwest.backend.mapper.PostMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.PostRepository;
import com.qwest.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AuthorRepository authorRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, AuthorRepository authorRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.authorRepository = authorRepository;
        this.postMapper = postMapper;
    }

    @Override
    public List<PostDTO> findAll() {
        return postRepository.findAll().stream().map(postMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<PostDTO> findById(Long id) {
        return postRepository.findById(id).map(postMapper::toDto);
    }

    @Override
    public PostDTO save(PostDTO postDTO) {
        Post post = postMapper.toEntity(postDTO);
        // Set author and categories manually...
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}
