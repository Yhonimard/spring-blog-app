package yhoni.blog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yhoni.blog.entity.Role;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String username;

    private String firstName;

    private String lastName;

    private List<CommentResponse> comments;

}
