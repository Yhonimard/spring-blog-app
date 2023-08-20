package yhoni.blog.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import yhoni.blog.request.CommentRequest;
import yhoni.blog.response.CommentResponse;

public interface CommentService {

    CommentResponse createComment(String postId, Authentication authentication, CommentRequest request);

    List<CommentResponse> getAll(String postId);

    CommentResponse getByPostIdAndCommentId(String postId, String commentId);

    CommentResponse updateById(String postId, String commentId, Authentication authentication,
            CommentRequest request);

    void deleteById(String postId, String commentId, Authentication authentication);
}
