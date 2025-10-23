package codeit.sb06.imagepost.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // S3 URL 또는 로컬 웹 경로 (/uploads/...)
    @Column(nullable = false)
    private String storageUrl;

    @Column(nullable = false)
    private String originalFileName; // 원본 파일명

    // 연관관계 편의 메서드 (Post를 설정)
    // Post와 다대일(ManyToOne) 관계
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostImage(String storageUrl, String originalFileName) {
        this.storageUrl = storageUrl;
        this.originalFileName = originalFileName;
    }
}