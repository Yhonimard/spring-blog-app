package yhoni.blog.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtApiAuthResponse<T> {
    private String message;
    private String token;
    private T data;
}
