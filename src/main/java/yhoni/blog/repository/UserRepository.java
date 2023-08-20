package yhoni.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import yhoni.blog.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}
