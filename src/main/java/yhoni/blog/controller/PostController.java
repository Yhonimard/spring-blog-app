package yhoni.blog.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yhoni.blog.model.WebResponse;
import yhoni.blog.model.PagingResponse;
import yhoni.blog.model.PostRequest;
import yhoni.blog.model.PostResponse;
import yhoni.blog.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WebResponse<PostResponse> create(@Valid @RequestBody PostRequest request) {
        PostResponse response = postService.createPost(request);

        return WebResponse.<PostResponse>builder()
                .message("success create post")
                .data(response)
                .build();
    }

    @GetMapping
    public WebResponse<PagingResponse<List<PostResponse>>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "asc") String sortBy) {
        Page<PostResponse> postResponse = postService.getAllPost(page, size, sortBy);

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

    @GetMapping("/{id}")
    public WebResponse<PostResponse> getPostById(@PathVariable("id") String postId) {
        PostResponse postById = postService.getPostById(postId);

        return WebResponse.<PostResponse>builder()
                .data(postById)
                .message("success get post by id " + postId)
                .build();
    }

    @PutMapping("/{id}")
    public WebResponse<PostResponse> updatePostById(@PathVariable("id") String id,
                                                    @Valid @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.updatePostById(id, postRequest);

        return WebResponse.<PostResponse>builder()
                .message("success update post by id " + postResponse.getId())
                .data(postResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<?> deletePostById(@PathVariable("id") String id) {
        postService.deletePostById(id);
        return WebResponse.builder()
                .message("success delete post by id " + id)
                .build();
    }
}
