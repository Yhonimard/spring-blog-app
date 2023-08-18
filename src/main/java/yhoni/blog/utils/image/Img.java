package yhoni.blog.utils.image;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Img {
    @Id
    private Integer id;

    private String name;
    @Lob
    private byte[] image;

}
