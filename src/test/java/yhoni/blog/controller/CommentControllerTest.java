package yhoni.blog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import yhoni.blog.entity.Comment;
import yhoni.blog.entity.Post;
import yhoni.blog.model.CommentResponse;
import yhoni.blog.model.WebErrorResponse;
import yhoni.blog.model.WebResponse;
import yhoni.blog.repository.CommentRepository;
import yhoni.blog.repository.PostRepository;


@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();


    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void createCommentSuccess() throws Exception {
        Post post = new Post();
        post.setContent("content test");
        post.setTitle("title test");
        postRepository.save(post);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setTitle("title");

        mockMvc.perform(post("/api/post/" + post.getId() + "/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment))
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<CommentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<CommentResponse>>() {
            });

            assertEquals("success create comment", response.getMessage());
            assertEquals(comment.getTitle(), response.getData().getTitle());

        });
    }

    @Test
    void createComment_failedNotFound() throws Exception {
        Post post = new Post();
        post.setContent("content test");
        post.setTitle("title test");
        postRepository.save(post);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setTitle("title");

        mockMvc.perform(post("/api/post/test/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment))
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this post", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
            assertNotNull(response.getErrorDetails());
        });
    }


    @Test
    void testGet_NotFound() throws Exception {
        Post post = new Post();
        post.setContent("content test");
        post.setTitle("title test");
        postRepository.save(post);

        mockMvc.perform(get("/api/post/test/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this post", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
            assertNotNull(response.getErrorDetails());
        });
    }

    @Test
    void testGet_success() throws Exception {
        Post post = new Post();
        post.setContent("content test");
        post.setTitle("title test");
        postRepository.save(post);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setTitle("title");
        commentRepository.save(comment);

        mockMvc.perform(get("/api/post/" + post.getId() + "/comment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<CommentResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("success get all comments", response.getMessage());
            assertNotNull(response.getData());
            assertEquals(response.getData().size(), commentRepository.findAll().size());
        });
    }

    @Test
    void testGetCommentByPostIdAndCommentId_success() throws Exception {
        Post post = new Post();
        post.setContent("content test");
        post.setTitle("title test");
        Post postSaved = postRepository.save(post);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setTitle("title");
        Comment reqComment = commentRepository.save(comment);

        mockMvc.perform(get("/api/post/" + post.getId() + "/comment/" + reqComment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<CommentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals("success get comment by id " + reqComment.getId(), response.getMessage());
            assertNotNull(response.getData());
            assertEquals(response.getData().getId(), reqComment.getId());
            assertEquals(response.getData().getTitle(), reqComment.getTitle());
            Optional<Post> byId = postRepository.findById(reqComment.getPost().getId());
            assertEquals(byId.get().getId(), postSaved.getId());

        });
    }

    @Test
    void testGetCommentByPostIdAndCommentId_commentDoesntBelongToPost() throws Exception {
        Post post1 = new Post();
        post1.setContent("content test");
        post1.setTitle("title test");
        Post post1Request = postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("content test 2");
        post2.setTitle("title test 2");
        Post post2Request = postRepository.save(post2);

        Comment comment = new Comment();
        comment.setPost(post1Request);
        comment.setTitle("title");
        Comment reqComment = commentRepository.save(comment);

        mockMvc.perform(get("/api/post/" + post2Request.getId() + "/comment/" + reqComment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("comment doesnt belong to post", response.getErrorMessage());
            assertNotEquals(reqComment.getPost().getId(), post2Request.getId());
            assertEquals(reqComment.getPost().getId(), post1Request.getId());
        });
    }

    @Test
    void testGetCommentByPostIdAndCommentId_notFoundByPostId() throws Exception {
        Post post1 = new Post();
        post1.setContent("content test");
        post1.setTitle("title test");
        Post post1Request = postRepository.save(post1);

        Comment comment = new Comment();
        comment.setPost(post1Request);
        comment.setTitle("title");
        Comment reqComment = commentRepository.save(comment);

        mockMvc.perform(get("/api/post/test/comment/" + comment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this post", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
        });
    }


    @Test
    void testGetCommentByPostIdAndCommentId_notFoundByCommentId() throws Exception {
        Post post1 = new Post();
        post1.setContent("content test");
        post1.setTitle("title test");
        Post post1Request = postRepository.save(post1);

        Comment comment = new Comment();
        comment.setPost(post1Request);
        comment.setTitle("title");
        Comment reqComment = commentRepository.save(comment);

        mockMvc.perform(get("/api/post/" + post1Request.getId() + "/comment/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this comment", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
        });

    }

    @Test
    void testUpdateComment_success() throws Exception {
        Post postRequest1 = new Post();
        postRequest1.setContent("content test");
        postRequest1.setTitle("title test");
        Post postResponse1 = postRepository.save(postRequest1);

        Comment commentRequest = new Comment();
        commentRequest.setPost(postRequest1);
        commentRequest.setTitle("title");
        Comment commentResponse = commentRepository.save(commentRequest);


        Comment commentRequestUpdate = new Comment();
        commentRequestUpdate.setTitle("title");


        mockMvc.perform(put("/api/post/" + postResponse1.getId() + "/comment/" + commentResponse.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestUpdate))
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<CommentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("success update comment", response.getMessage());
            assertEquals(commentRequestUpdate.getTitle(), response.getData().getTitle());
        });
    }

    @Test
    void testUpdateComment_commentDoesntBelongToPost() throws Exception {
        Post post1 = new Post();
        post1.setContent("content test");
        post1.setTitle("title test");
        Post postResponse1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("content test 2");
        post2.setTitle("title test 2");
        Post postResponse2 = postRepository.save(post2);

        Comment comment = new Comment();
        comment.setPost(postResponse1);
        comment.setTitle("title");
        Comment commentResponse = commentRepository.save(comment);

        Comment commentRequestUpdate = new Comment();
        comment.setTitle("title");

        mockMvc.perform(put("/api/post/" + postResponse2.getId() + "/comment/" + commentResponse.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestUpdate))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("comment doesnt belong to post", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
            assertNotEquals(comment.getTitle(), commentRequestUpdate.getTitle());

        });
    }

    @Test
    void testUpdateComment_commentNotFound() throws Exception {
        Post post1 = new Post();
        post1.setContent("content test");
        post1.setTitle("title test");
        Post postResponse1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("content test 2");
        post2.setTitle("title test 2");
        Post postResponse2 = postRepository.save(post2);

        Comment comment = new Comment();
        comment.setPost(postResponse1);
        comment.setTitle("title");
        Comment commentResponse = commentRepository.save(comment);

        Comment commentRequestUpdate = new Comment();
        comment.setTitle("title");

        mockMvc.perform(put("/api/post/" + postResponse2.getId() + "/comment/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestUpdate))
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this comment", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
        });
    }


    @Test
    void testUpdateComment_postNotFound() throws Exception {
        Post post1 = new Post();
        post1.setContent("content test");
        post1.setTitle("title test");
        Post postResponse1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("content test 2");
        post2.setTitle("title test 2");
        Post postResponse2 = postRepository.save(post2);

        Comment comment = new Comment();
        comment.setPost(postResponse1);
        comment.setTitle("title");
        Comment commentResponse = commentRepository.save(comment);

        Comment commentRequestUpdate = new Comment();
        comment.setTitle("title");

        mockMvc.perform(put("/api/post/test/comment/" + commentResponse.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestUpdate))
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this post", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
        });
    }

    @Test
    void testDeleteComment_success() throws Exception {

        Post postRequest = new Post();
        postRequest.setContent("content test");
        postRequest.setTitle("title test");
        Post postResponse = postRepository.save(postRequest);

        Comment commentRequest = new Comment();
        commentRequest.setPost(postRequest);
        commentRequest.setTitle("title");
        Comment commentResponse = commentRepository.save(commentRequest);


        mockMvc.perform(delete("/api/post/" + postResponse.getId() + "/comment/" + commentResponse.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("success delete this comment", response.getMessage());
        });
    }

    @Test
    void testDeleteComment_PostNotFound() throws Exception {

        Post postRequest = new Post();
        postRequest.setContent("content test");
        postRequest.setTitle("title test");
        Post postResponse = postRepository.save(postRequest);

        Comment commentRequest = new Comment();
        commentRequest.setPost(postRequest);
        commentRequest.setTitle("title");
        Comment commentResponse = commentRepository.save(commentRequest);


        mockMvc.perform(delete("/api/post/test/comment/" + commentResponse.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this post", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
            assertNotNull(response.getErrorDetails());
        });
    }

    @Test
    void testDeleteComment_CommentNotFound() throws Exception {

        Post postRequest = new Post();
        postRequest.setContent("content test");
        postRequest.setTitle("title test");
        Post postResponse = postRepository.save(postRequest);

        Comment commentRequest = new Comment();
        commentRequest.setPost(postRequest);
        commentRequest.setTitle("title");
        Comment commentResponse = commentRepository.save(commentRequest);


        mockMvc.perform(delete("/api/post/" + postResponse.getId() + "/comment/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("cant find this comment", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
            assertNotNull(response.getErrorDetails());
        });
    }


    @Test
    void testDeleteComment_commentDoesntBelongToPost() throws Exception {
        Post post1 = new Post();
        post1.setContent("content test");
        post1.setTitle("title test");
        Post postResponse1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("content test 2");
        post2.setTitle("title test 2");
        Post postResponse2 = postRepository.save(post2);

        Comment comment = new Comment();
        comment.setPost(postResponse1);
        comment.setTitle("title");
        Comment commentResponse = commentRepository.save(comment);

        Comment commentRequestUpdate = new Comment();
        comment.setTitle("title");

        mockMvc.perform(delete("/api/post/" + postResponse2.getId() + "/comment/" + commentResponse.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("comment doesnt belong to post", response.getErrorMessage());
            assertNotNull(response.getErrorStatus());
            assertNotEquals(comment.getTitle(), commentRequestUpdate.getTitle());

        });
    }

}