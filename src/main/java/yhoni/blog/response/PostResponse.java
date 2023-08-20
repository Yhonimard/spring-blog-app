package yhoni.blog.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private List<CommentResponse> comments;
    private List<PostImageResponse> images;
}
