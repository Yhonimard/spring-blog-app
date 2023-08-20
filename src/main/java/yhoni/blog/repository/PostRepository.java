package yhoni.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import yhoni.blog.entity.Post;

public interface PostRepository extends JpaRepository<Post, String>, JpaSpecificationExecutor<Post> {

    // @Query("select p from Post p where " +
    //         "p.title like concat('%', :search, '%') " +
    //         "or p.content like concat('%', :search, '%')")
    // Page<Post> findAllSearchAndSort(String search, Pageable pageable);

    
}
