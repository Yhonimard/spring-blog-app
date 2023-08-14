package yhoni.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {


    @Size(max = 100)
    @NotBlank
    private String username;

    @Size(max = 100)
    @NotBlank
    private String password;
}
