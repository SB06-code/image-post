package codeit.sb06.imagepost.repository;

import codeit.sb06.imagepost.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}