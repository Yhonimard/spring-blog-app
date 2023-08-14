package yhoni.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yhoni.blog.entity.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
}
