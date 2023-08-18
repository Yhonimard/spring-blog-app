package yhoni.blog.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import yhoni.blog.model.PostRequest;
import yhoni.blog.model.PostResponse;

public interface PostService {
    PostResponse createPost(PostRequest request, MultipartFile file) throws IOException;

    Page<PostResponse> getAllPost(int page, int size, String sort);

    PostResponse getPostById(String postId);

    PostResponse updatePostById(String postId, PostRequest request);

    void deletePostById(String postId);
}
