package yhoni.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import yhoni.blog.model.CommentRequest;
import yhoni.blog.model.CommentResponse;
import yhoni.blog.model.WebErrorResponse;
import yhoni.blog.model.WebResponse;
import yhoni.blog.service.CommentService;

@RestController
@RequestMapping(value = "/api/post/{postId}/comment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Comment", description = "Comment API")
@Slf4j
public class CommentController {
	@Autowired
	private CommentService commentService;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	@Operation(description = "this api requires postId if postId not found will be throw 404 error", summary = "create comment in post", responses = {
			@ApiResponse(responseCode = "201", description = "success"),
			@ApiResponse(responseCode = "401", description = "Unathorized / invalid token", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "post not found", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
	})
	@Parameters({
			@Parameter(name = "postId", description = "post id"),
	})
	@SecurityRequirement(name = "bearerAuth")
	public WebResponse<CommentResponse> create(
			@PathVariable("postId") String postId,
			Authentication authentication,
			@RequestBody CommentRequest request) {

		// log.info("AUTHENTICATION.GETNAME = " + authentication.getName());
		// log.info("AUTHENTICATION.getAuthorities = " +
		// authentication.getAuthorities());
		// log.info("AUTHENTICATION.getCredentials = " +
		// authentication.getCredentials());
		// log.info("AUTHENTICATION.getDetails = " + authentication.getDetails());
		// log.info("AUTHENTICATION.getPrincipal = " + authentication.getPrincipal());

		CommentResponse comment = commentService.createComment(postId, authentication, request);

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

	@PutMapping("/{commentId}")
	@Operation(description = "this api requires postId if postId not found will be throw 404 error", summary = "update comment in post", responses = {
			@ApiResponse(responseCode = "200", description = "success"),
			@ApiResponse(responseCode = "401", description = "not allowed to update this comment / Unathorized", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "post not found / comment not found", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "comment doesnt belong to post / something went wrong", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
	})
	@Parameters({
			@Parameter(name = "postId", description = "post id"),
			@Parameter(name = "commentId", description = "comment id"),
	})
	@SecurityRequirement(name = "bearerAuth")
	public WebResponse<CommentResponse> updateCommentById(
			@PathVariable("postId") String postId,
			@PathVariable("commentId") String commentId,
			Authentication authentication,
			@RequestBody CommentRequest request) {

		CommentResponse response = commentService.updateById(postId, commentId, authentication, request);

		return WebResponse.<CommentResponse>builder()
				.message("success update comment")
				.data(response)
				.build();
	}

	@DeleteMapping("/{commentId}")
	@Operation(description = "this api requires postId if postId not found will be throw 404 error", summary = "update comment in post", responses = {
			@ApiResponse(responseCode = "200", description = "success"),
			@ApiResponse(responseCode = "401", description = "not allowed to delete this comment / Unathorized", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "post not found / comment not found", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "comment doesnt belong to post / something went wrong", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
	})
	@Parameters({
			@Parameter(name = "postId", description = "post id"),
			@Parameter(name = "commentId", description = "comment id"),
	})
	@SecurityRequirement(name = "bearerAuth")
	public WebResponse<?> deleteCommentById(@PathVariable("postId") String postId,
			@PathVariable("commentId") String commentId,
			Authentication authentication) {
		commentService.deleteById(postId, commentId, authentication);
		return WebResponse.builder()
				.message("success delete this comment")
				.build();
	}
}
