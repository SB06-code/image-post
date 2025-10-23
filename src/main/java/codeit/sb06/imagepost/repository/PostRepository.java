package codeit.sb06.imagepost.repository;

import codeit.sb06.imagepost.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}