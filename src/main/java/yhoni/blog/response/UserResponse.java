package yhoni.blog.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponse {

    private String username;

    private String firstName;

    private String lastName;

    private List<CommentResponse> comments;
}
