package yhoni.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yhoni.blog.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}
