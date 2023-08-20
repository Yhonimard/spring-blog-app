package yhoni.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import yhoni.blog.model.PagingResponse;
import yhoni.blog.model.PostRequest;
import yhoni.blog.model.PostResponse;
import yhoni.blog.model.WebErrorResponse;
import yhoni.blog.model.WebResponse;
import yhoni.blog.service.PostService;

@RestController
@RequestMapping("/api/post")
@Tag(name = "Post", description = "Post API")
@Slf4j
public class PostController {

	@Autowired
	private PostService postService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	@Operation(description = "this api requires admin role in your account to create post", summary = "create post", responses = {
			@ApiResponse(responseCode = "201", description = "success"),
			@ApiResponse(responseCode = "401", description = "Unathorized / invalid token", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "media exceeded is because file image too large / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))

	})
	@Parameters({
			@Parameter(name = "title", description = "post title"),
			@Parameter(name = "content", description = "post content"),
			@Parameter(name = "image", description = "upload an image")
	})
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	public WebResponse<PostResponse> create(
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "content", required = true) String content,
			@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {

		PostRequest request = PostRequest
				.builder()
				.title(title)
				.content(content)
				.build();

		PostResponse response = postService.createPost(request, file);

		return WebResponse.<PostResponse>builder()
				.message("success create post")
				.data(response)
				.build();
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "this api for get all post and you can filter with soryBy or you can search post by title/content", summary = "get all posts", responses = {
			@ApiResponse(responseCode = "200", description = "success"),
			@ApiResponse(responseCode = "404", description = "posts not found", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "something went wrong/ an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
	})
	public WebResponse<PagingResponse<List<PostResponse>>> getAll(
			@RequestParam(value = "pageNo", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "sortBy", required = false, defaultValue = "title") String sortBy,
			@RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
			@RequestParam(value = "search", required = false) String search) {
		Page<PostResponse> postResponse = postService.getAllPost(page, size, sortBy, sortDir, search);

		return WebResponse.<PagingResponse<List<PostResponse>>>builder()
				.data(PagingResponse.<List<PostResponse>>builder()
						.data(postResponse.getContent())
						.currentPage(postResponse.getNumber())
						.currentPageSize(postResponse.getSize())
						.totalAllPage(postResponse.getTotalPages())
						.totalAllData(postResponse.getTotalElements())
						.isLast(postResponse.isLast())
						.build())
				.build();

	}

	@GetMapping(value = "/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(description = "this api for get post by id and can be error 404 if the post not found", summary = "get post by id", responses = {
			@ApiResponse(responseCode = "200", description = "success"),
			@ApiResponse(responseCode = "404", description = "post not found", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))

	})
	@Parameters({
			@Parameter(name = "postId", description = "post id"),
	})
	public WebResponse<PostResponse> getPostById(@PathVariable("postId") String postId) {
		PostResponse postById = postService.getPostById(postId);

		return WebResponse.<PostResponse>builder()
				.data(postById)
				.message("success get post by id " + postId)
				.build();
	}

	@PutMapping(value = "/{postId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(description = "this api requires admin role in your account to update post by postId and can be error 404 if the post not found", summary = "update post by postId", responses = {
			@ApiResponse(responseCode = "200", description = "success"),
			@ApiResponse(responseCode = "404", description = "post not found", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
	})
	@Parameters({
			@Parameter(name = "postId", description = "post id"),
	})
	@SecurityRequirement(name = "bearerAuth")
	public WebResponse<PostResponse> updatePostById(@PathVariable("postId") String postId,
			@Valid @RequestBody PostRequest postRequest) {
		PostResponse postResponse = postService.updatePostById(postId, postRequest);

		return WebResponse.<PostResponse>builder()
				.message("success update post by id " + postResponse.getId())
				.data(postResponse)
				.build();
	}

	@DeleteMapping("/{postId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(description = "this api requires admin role in your account to delete post by postId and can be error 404 if the post not found", summary = "delete post by postId", responses = {
			@ApiResponse(responseCode = "200", description = "success"),
			@ApiResponse(responseCode = "404", description = "post not found", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
	})
	@Parameters({
			@Parameter(name = "postId", description = "post id"),
	})
	@SecurityRequirement(name = "bearerAuth")
	public WebResponse<?> deletePostById(@PathVariable("postId") String id) {
		postService.deletePostById(id);
		return WebResponse.builder()
				.message("success delete post by id " + id)
				.build();
	}
}
