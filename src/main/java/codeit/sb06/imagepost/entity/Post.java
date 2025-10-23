package codeit.sb06.imagepost.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    // PostImage와 일대다(OneToMany) 관계
    // cascade = ALL: Post 저장/삭제 시 PostImage도 함께 저장/삭제
    // orphanRemoval = true: Post의 images 리스트에서 PostImage가 제거되면 DB에서도 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Post(String author, String title, String content, String password, List<String> tags) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.password = password;
        this.tags = tags;
    }

    public void update(String title, String content, List<String> tags) {
        this.title = title;
        this.content = content;
        this.tags = tags;
    }

    // 연관관계 편의 메서드 (PostImage 리스트를 설정)
    public void setImages(List<PostImage> images) {
        this.images.clear(); // 기존 이미지 연결 끊기 (orphanRemoval=true로 DB 삭제됨)
        if (images != null) {
            this.images.addAll(images);
            // PostImage 객체에 Post(this)를 설정
            images.forEach(image -> image.setPost(this));
        }
    }
}