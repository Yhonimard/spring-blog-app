package yhoni.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yhoni.blog.entity.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, String> {
}
