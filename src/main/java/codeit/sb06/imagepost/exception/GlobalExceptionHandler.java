package codeit.sb06.imagepost.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException e) {
        log.error("handlePostNotFoundException", e);
        final ErrorCode errorCode = ErrorCode.POST_NOT_FOUND;
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException e) {
        log.error("handleInvalidPasswordException", e);
        final ErrorCode errorCode = ErrorCode.INVALID_PASSWORD;
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // FileUploadException 핸들러
    @ExceptionHandler(FileUploadException.class)
    protected ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException e) {
        log.error("handleFileUploadException", e);

        // 에러 메시지를 기반으로 INVALID_FILE_COUNT 구분
        final ErrorCode errorCode = e.getMessage().equals(ErrorCode.INVALID_FILE_COUNT.getMessage())
                ? ErrorCode.INVALID_FILE_COUNT
                : ErrorCode.FILE_UPLOAD_FAILED;

        final ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}