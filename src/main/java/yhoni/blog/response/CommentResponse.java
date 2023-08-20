package yhoni.blog.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentResponse {
    private String id;
    private String title;
}
