package codeit.sb06.imagepost.dto.response;

import codeit.sb06.imagepost.entity.PostImage;
import lombok.Builder;

@Builder
public record PostImageResponse(
        Long id,
        String imageUrl
) {
    public static PostImageResponse from(PostImage image) {
        return PostImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getStorageUrl())
                .build();
    }
}