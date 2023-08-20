package yhoni.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import yhoni.blog.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, String> {
}
