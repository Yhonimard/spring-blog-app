package yhoni.blog.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import yhoni.blog.entity.Post;
import yhoni.blog.entity.PostImage;
import yhoni.blog.model.PostRequest;
import yhoni.blog.model.PostResponse;
import yhoni.blog.repository.PostImageRepository;
import yhoni.blog.repository.PostRepository;
import yhoni.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PostImageRepository postImageRepository;

    @Override
    public PostResponse createPost(PostRequest request, MultipartFile file) throws IOException {

        Post postEntity = toPostEntity(request);
        Post post = postRepository.save(postEntity);

        PostImage postImage = new PostImage();
        postImage.setImage(file.getBytes());
        postImage.setImageName(file.getOriginalFilename());
        postImage.setId(UUID.randomUUID().toString());
        postImage.setPost(post);
        postImageRepository.save(postImage);

        PostResponse postResponse = toPostResponse(post);

        return postResponse;
    }

    @Override
    public PageImpl<PostResponse> getAllPost(int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postRepository.findAll(pageable);

        if (posts.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "posts not found");

        List<PostResponse> responses = posts.getContent().stream().map(this::toPostResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

    @Override
    public PostResponse getPostById(String postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found by id " + postId));

        PostResponse postResponse = toPostResponse(post);

        return postResponse;
    }

    @Override
    public PostResponse updatePostById(String postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found by id " + postId));

        post.setTitle(postRequest.getTitle().isBlank() ? post.getTitle() : postRequest.getTitle());
        post.setContent(postRequest.getContent().isBlank() ? post.getContent() : postRequest.getContent());

        Post postUpdated = postRepository.save(post);

        return toPostResponse(postUpdated);
    }

    @Override
    public void deletePostById(String postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found by id " + postId));

        postRepository.deleteById(postId);
    }

    private PostResponse toPostResponse(Post post) {
        return modelMapper.map(post, PostResponse.class);
    }

    private Post toPostEntity(PostRequest request) {
        return modelMapper.map(request, Post.class);
    }
}
