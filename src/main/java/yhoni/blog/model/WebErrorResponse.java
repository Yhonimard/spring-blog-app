package yhoni.blog.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WebErrorResponse<T> {
    private String errorMessage;
    private String errorStatus;
    private T errorDetails;
}
