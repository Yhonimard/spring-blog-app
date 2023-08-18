package yhoni.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yhoni.blog.model.CommentRequest;
import yhoni.blog.model.CommentResponse;
import yhoni.blog.model.WebResponse;
import yhoni.blog.service.CommentService;

@RestController
@RequestMapping(value = "/api/post/{postId}/comment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {
	@Autowired
	private CommentService commentService;

	@PostMapping("/user/{userId}")
	public WebResponse<CommentResponse> create(
			@PathVariable("postId") String postId,
			@PathVariable("userId") String userId,
			Authentication authentication,
			@RequestBody CommentRequest request) {

		CommentResponse comment = commentService.createComment(postId, userId, authentication, request);

		return WebResponse.<CommentResponse>builder()
				.message("success create comment")
				.data(comment)
				.build();
	}

	@GetMapping
	public WebResponse<List<CommentResponse>> getAll(@PathVariable("postId") String postId) {
		List<CommentResponse> commentResponses = commentService.getAll(postId);

		return WebResponse.<List<CommentResponse>>builder()
				.data(commentResponses)
				.message("success get all comments")
				.build();
	}

	@GetMapping("/{commentId}")
	public WebResponse<CommentResponse> getCommentByPostIdAndCommentId(@PathVariable("postId") String postId,
			@PathVariable("commentId") String commentId) {
		CommentResponse response = commentService.getByPostIdAndCommentId(postId, commentId);

		return WebResponse.<CommentResponse>builder()
				.message("success get comment by id " + response.getId())
				.data(response)
				.build();
	}

	@PutMapping("/{commentId}/user/{userId}")
	public WebResponse<CommentResponse> updateCommentById(@PathVariable("postId") String postId,
			@PathVariable("commentId") String commentId,
			@PathVariable("userId") String userId,
			Authentication authentication,
			@RequestBody CommentRequest request) {

		CommentResponse response = commentService.updateById(postId, commentId, userId, authentication,
				request);

		return WebResponse.<CommentResponse>builder()
				.message("success update comment")
				.data(response)
				.build();
	}

	@DeleteMapping("/{commentId}/user/{userId}")
	public WebResponse<?> deleteCommentById(@PathVariable("postId") String postId,
			@PathVariable("commentId") String commentId,
			@PathVariable("userId") String userId,
			Authentication authentication) {
		commentService.deleteById(postId, commentId, userId, authentication);
		return WebResponse.builder()
				.message("success delete this comment")
				.build();
	}
}
