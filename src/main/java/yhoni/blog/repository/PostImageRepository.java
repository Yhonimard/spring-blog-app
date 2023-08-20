package yhoni.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import yhoni.blog.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, String> {
}
