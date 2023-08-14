package yhoni.blog.model;

import lombok.*;
import yhoni.blog.entity.Comment;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private String id;
    private String title;
    private String content;

    private List<CommentResponse> comments;
}
