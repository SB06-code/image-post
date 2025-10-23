package codeit.sb06.imagepost.dto.request;

import codeit.sb06.imagepost.dto.request.validator.ValidContent;
import codeit.sb06.imagepost.dto.request.validator.ValidTitle;
import codeit.sb06.imagepost.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
public record PostCreateRequest(
        @NotBlank(message = "작성자 이름은 필수입니다.")
        String author,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "\\d{6}", message = "비밀번호는 6자리 숫자여야 합니다.")
        String password,

        @ValidTitle
        String title,

        @ValidContent
        String content,

        List<@NotBlank(message = "태그는 공백일 수 없습니다.") String> tags
) {
    public Post toEntity() {
        return Post.builder()
                .author(author)
                .password(password)
                .title(title)
                .content(content)
                .tags(tags)
                .build();
    }
}