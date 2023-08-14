package yhoni.blog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String content;
    @CreationTimestamp()
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();
}