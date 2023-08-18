package yhoni.blog.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostImageResponse {
    private String id;
    private String imageName;
}
