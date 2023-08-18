package yhoni.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_images")
public class PostImage {

    @Id
    private String id;

    @Column(name = "image_name")
    private String imageName;

    @JsonIgnore
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;
}
