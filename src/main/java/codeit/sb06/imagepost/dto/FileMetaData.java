package codeit.sb06.imagepost.dto;

/**
 * 파일 저장 후 서비스 계층에서 반환할 메타데이터
 * @param storageUrl 스토리지에 저장된 URL (S3 URL 또는 로컬 웹 경로)
 * @param originalFileName 원본 파일명
 */
public record FileMetaData(
        String storageUrl,
        String originalFileName
) {
}