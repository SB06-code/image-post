package codeit.sb06.imagepost.dto.response;

import codeit.sb06.imagepost.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record PostResponse(
        Long id,
        String author,
        String title,
        String content,
        List<String> tags,
        List<PostImageResponse> images, // <-- 이미지 리스트
        LocalDateTime createdAt
) {
    public static PostResponse from(Post post) {
        // PostImage 엔티티 리스트를 PostImageResponse DTO 리스트로 변환
        List<PostImageResponse> imageResponses = post.getImages().stream()
                .map(PostImageResponse::from)
                .collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags())
                .images(imageResponses) // <-- 빌더에 추가
                .createdAt(post.getCreatedAt())
                .build();
    }
}