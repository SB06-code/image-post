package codeit.sb06.imagepost.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

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
}