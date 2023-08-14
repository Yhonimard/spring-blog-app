package yhoni.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yhoni.blog.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
