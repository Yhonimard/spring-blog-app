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
import yhoni.blog.model.CommentRequest;
import yhoni.blog.model.CommentResponse;
import yhoni.blog.repository.CommentRepository;
import yhoni.blog.repository.PostRepository;
import yhoni.blog.repository.UserRepository;
import yhoni.blog.service.CommentService;

@Service
@Slf4j
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
    public List<CommentResponse> getAll(String postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this post"));

        List<Comment> comments = commentRepository.findAll();

        return comments.stream().map(this::toCommentResponse).collect(Collectors.toList());
    }

    @Override
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

    private CommentResponse toCommentResponse(Comment comment) {
        return modelMapper.map(comment, CommentResponse.class);
    }

    private Comment toCommentEntity(CommentRequest request) {
        return modelMapper.map(request, Comment.class);
    }
}
