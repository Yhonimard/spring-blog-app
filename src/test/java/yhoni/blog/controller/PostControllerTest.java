package yhoni.blog.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import yhoni.blog.entity.Post;
import yhoni.blog.model.WebErrorResponse;
import yhoni.blog.model.WebResponse;
import yhoni.blog.model.PostRequest;
import yhoni.blog.model.PostResponse;
import yhoni.blog.repository.CommentRepository;
import yhoni.blog.repository.PostRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

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
    void testCreatePostSuccess() throws Exception {

        PostRequest request = PostRequest
                .builder()
                .title("test")
                .content("testing")
                .build();


        mockMvc.perform(post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                            WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                            });

                            assertNotNull(response.getData());
                            assertEquals(request.getContent(), response.getData().getContent());
                            assertEquals(request.getTitle(), response.getData().getTitle());
                        }
                );

    }

    @Test
    void testGetNotFound() throws Exception {

        mockMvc.perform(get("/api/post")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebErrorResponse<?>>() {
                    });
                    assertNotNull(response.getErrorMessage());
                    assertNotNull(response.getErrorDetails());
                    assertNotNull(response.getErrorStatus());
                    assertEquals("posts not found", response.getErrorMessage());
                    assertNotNull(response.getErrorStatus());
                });
    }

    @Test
    void testPostGetSuccess() throws Exception {
        Post request = Post
                .builder()
                .title("test")
                .content("testing")
                .build();

        postRepository.save(request);

        mockMvc.perform(get("/api/post")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<WebResponse<?>>() {
                            });
                });
    }

    @Test
    void testPostGetByIdSuccess() throws Exception {
        Post request = Post
                .builder()
                .title("test")
                .content("testing")
                .build();

        Post post = postRepository.save(request);


        mockMvc.perform(get("/api/post/" + post.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    assertEquals(post.getContent(), response.getData().getContent());
                    assertEquals(post.getId(), response.getData().getId());
                    assertEquals(post.getTitle(), response.getData().getTitle());
                    assertEquals("success get post by id " + post.getId(), response.getMessage());
                });
    }

    @Test
    void testPostGetByIdFailedNotFound() throws Exception {


        mockMvc.perform(get("/api/post/test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    assertNotNull(response.getErrorDetails());
                    assertEquals("post not found by id test", response.getErrorMessage());
                });
    }

    @Test
    void testUpdatePostById_Success() throws Exception {
        Post post = Post
                .builder()
                .title("test")
                .content("testing")
                .build();

        Post postSave = postRepository.save(post);


        Post request = new Post();
        request.setTitle("test title");
        request.setContent("test content");

        mockMvc.perform(put("/api/post/" + postSave.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    assertEquals(request.getContent(), response.getData().getContent());
                    assertEquals(request.getTitle(), response.getData().getTitle());
                    assertEquals("success update post by id " + post.getId(), response.getMessage());
                });
    }


    @Test
    void testUpdatePostById_failedNotFound() throws Exception {

        Post request = new Post();
        request.setTitle("test title");
        request.setContent("test content");

        mockMvc.perform(put("/api/post/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    assertEquals("post not found by id test", response.getErrorMessage());
                    assertNotNull(response.getErrorDetails());
                    assertNotNull(response.getErrorStatus());

                });

    }


    @Test
    void testDeletePostById_failedNotFound() throws Exception {

//        Post request = new Post();
//        request.setTitle("test title");
//        request.setContent("test content");

        mockMvc.perform(delete("/api/post/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(status().isNotFound())
                .andDo(result -> {
                    WebErrorResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    assertEquals("post not found by id test", response.getErrorMessage());
                    assertNotNull(response.getErrorStatus());
                });

    }


    @Test
    void testDeletePostById_success() throws Exception {

        Post request = new Post();
        request.setTitle("test title");
        request.setContent("test content");

        Post post = postRepository.save(request);


        mockMvc.perform(delete("/api/post/" + post.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    assertEquals("success delete post by id " + post.getId(), response.getMessage());
                });

    }


}