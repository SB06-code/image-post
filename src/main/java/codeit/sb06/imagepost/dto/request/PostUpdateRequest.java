package codeit.sb06.imagepost.dto.request;

import codeit.sb06.imagepost.dto.request.validator.ValidContent;
import codeit.sb06.imagepost.dto.request.validator.ValidTitle;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record PostUpdateRequest(
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @ValidTitle
        String title,

        @ValidContent
        String content,

        List<@NotBlank(message = "태그는 공백일 수 없습니다.") String> tags
) {
}