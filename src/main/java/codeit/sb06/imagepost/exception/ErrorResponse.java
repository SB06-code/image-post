package codeit.sb06.imagepost.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String message;
    private final String code;

    private ErrorResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.code = code.getCode();
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }
}