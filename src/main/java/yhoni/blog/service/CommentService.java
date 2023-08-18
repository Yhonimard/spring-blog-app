package yhoni.blog.service;

import yhoni.blog.model.CommentRequest;
import yhoni.blog.model.CommentResponse;

import java.util.List;

import org.springframework.security.core.Authentication;

public interface CommentService {
    CommentResponse createComment(String postId, String userId, Authentication authentication, CommentRequest request);

    List<CommentResponse> getAll(String postId);

    CommentResponse getByPostIdAndCommentId(String postId, String commentId);

    CommentResponse updateById(String postId, String commentId, String userId, Authentication authentication, CommentRequest request);

    void deleteById(String postId, String commentId, String userId, Authentication authentication);
}
