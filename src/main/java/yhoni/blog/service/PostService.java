package yhoni.blog.service;

import org.springframework.data.domain.Page;
import yhoni.blog.entity.Post;
import yhoni.blog.model.PostRequest;
import yhoni.blog.model.PostResponse;

public interface PostService {
    PostResponse createPost(PostRequest request);

    Page<PostResponse> getAllPost(int page, int size, String sort);

    PostResponse getPostById(String postId);

    PostResponse updatePostById(String postId, PostRequest request);

    void deletePostById(String postId);
}
