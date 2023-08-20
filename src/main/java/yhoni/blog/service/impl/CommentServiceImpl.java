package yhoni.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import yhoni.blog.entity.Comment;
import yhoni.blog.entity.Post;
import yhoni.blog.entity.User;
import yhoni.blog.repository.CommentRepository;
import yhoni.blog.repository.PostRepository;
import yhoni.blog.repository.UserRepository;
import yhoni.blog.request.CommentRequest;
import yhoni.blog.response.CommentResponse;
import yhoni.blog.service.CommentService;

@Service
@Slf4j
/**
 * Implementation of the {@link yhoni.blog.service.CommentService} interface for
 * managing comments
 * on posts.
 */
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    /**
     * Creates a new comment for a specified post.
     *
     * @param postId         The ID of the post to which the comment is added.
     * @param authentication The authentication object representing the current
     *                       user.
     * @param request        The request containing comment details.
     * @return A CommentResponse object representing the created comment.
     * @throws ResponseStatusException If the specified post or user is not found.
     * 
     */
    public CommentResponse createComment(
            String postId,
            Authentication authentication,
            CommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this post"));

        User user = userRepository.findById(authentication.getName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cannot find this user by this username"));

        Comment comment = toCommentEntity(request);
        comment.setPost(post);
        comment.setUser(user);

        Comment commentSaved = commentRepository.save(comment);

        return toCommentResponse(commentSaved);
    }

    @Override
    /**
     * Get all post
     *
     * @param postId The ID of the post to which the comment is added.
     * @return A list of CommentResponse object representing the created comment.
     * @throws ResponseStatusException If the specified post not found.
     * 
     * 
     */

    public List<CommentResponse> getAll(String postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this post"));

        List<Comment> comments = commentRepository.findAll();

        return comments.stream().map(this::toCommentResponse).collect(Collectors.toList());
    }

    @Override
    /**
     * Retrieves a specific comment for a given post by their respective IDs.
     *
     * @param postId    The ID of the post to which the comment belongs.
     * @param commentId The ID of the comment to retrieve.
     * @return A CommentResponse object representing the requested comment.
     * @throws ResponseStatusException If the specified post or comment is not
     *                                 found, or if the comment does not belong to
     *                                 the post.
     */
    public CommentResponse getByPostIdAndCommentId(String postId, String commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this post"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this comment"));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "comment doesnt belong to post");
        }

        return toCommentResponse(comment);
    }

    @Override
    /**
     * Updates a specific comment for a given post by their respective IDs.
     *
     * @param postId         The ID of the post to which the comment belongs.
     * @param commentId      The ID of the comment to update.
     * @param authentication The authentication object representing the current
     *                       user.
     * @param request        The request containing object CommentRequest.
     * @return A CommentResponse object representing the updated comment.
     * @throws ResponseStatusException If the specified post or comment is not
     *                                 found, if the comment doesn't belong to the
     *                                 post,
     *                                 or if the authenticated user is not
     *                                 authorized to update the comment.
     */
    public CommentResponse updateById(String postId, String commentId, Authentication authentication,
            CommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this post"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this comment"));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "comment doesnt belong to post");
        }

        if (!comment.getUser().getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "youre not allowed to update this comment");
        }

        comment.setTitle(request.getTitle().isEmpty() ? comment.getTitle() : request.getTitle());

        return toCommentResponse(commentRepository.save(comment));
    }

    @Override
    /**
     * Deletes a specific comment for a given post by their respective IDs.
     *
     * @param postId         The ID of the post to which the comment belongs.
     * @param commentId      The ID of the comment to delete.
     * @param authentication The authentication object representing the current
     *                       user.
     * @throws ResponseStatusException If the specified post or comment is not
     *                                 found, if the comment doesn't belong to the
     *                                 post,
     *                                 or if the authenticated user is not
     *                                 authorized to delete the comment.
     */
    public void deleteById(String postId,
            String commentId,
            Authentication authentication) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this post"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this comment"));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "comment doesnt belong to post");
        }

        if (!comment.getUser().getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "youre not allowed to delete this comment");
        }

        commentRepository.deleteById(comment.getId());
    }

    /**
     * Converts a Comment entity into a CommentResponse object.
     *
     * @param comment The Comment entity to be converted.
     * @return A CommentResponse object representing the comment.
     */
    private CommentResponse toCommentResponse(Comment comment) {
        return modelMapper.map(comment, CommentResponse.class);
    }

    /**
     * Converts a CommentRequest object into a Comment entity.
     *
     * @param request The CommentRequest object containing comment details.
     * @return A Comment entity representing the comment.
     */
    private Comment toCommentEntity(CommentRequest request) {
        return modelMapper.map(request, Comment.class);
    }
}
