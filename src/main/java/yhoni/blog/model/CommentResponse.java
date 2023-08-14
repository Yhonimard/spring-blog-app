package yhoni.blog.model;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

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
