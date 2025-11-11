package codeit.sb06.imagepost.service;

import codeit.sb06.imagepost.dto.FileMetaData;
import codeit.sb06.imagepost.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Profile("dev") // 'dev' 프로필일 때만 이 빈을 등록
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

    // AWS SDK v2 (io.awspring.cloud)
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // application-dev.yml에서 주입
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public List<FileMetaData> storeFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        List<FileMetaData> storedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String s3Key = "images/" + UUID.randomUUID() + extension; // S3 Key (경로)

            try {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(s3Key)
                        .contentType(file.getContentType())
                        .build();

                RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
                s3Client.putObject(putObjectRequest, requestBody);

                storedFiles.add(new FileMetaData(s3Key, originalFileName));

            } catch (IOException e) {
                log.error("S3 파일 업로드 실패: {}", originalFileName, e);
                throw new FileUploadException("S3 파일 업로드에 실패했습니다: " + originalFileName, e);
            }
        }
        return storedFiles;
    }

    @Override
    public void deleteFiles(List<String> storageKeys) { // (★수정) URL이 아닌 S3 Key 리스트를 받음
        if (storageKeys == null || storageKeys.isEmpty()) {
            return;
        }

        for (String key : storageKeys) { // (★수정) 변수명 변경 (url -> key)
            try {
                // (★수정) URL 파싱 로직 제거, key를 직접 사용
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);

            } catch (Exception e) {
                log.error("S3 파일 삭제 실패: {}", key, e);
            }
        }
    }

    // S3 Key를 기반으로 Presigned GET URL을 생성하는 메서드
    public String getRetrievalUrl(String s3Key) {
        if (s3Key == null || s3Key.isEmpty()) {
            return null; // 또는 기본 이미지 URL
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();

            // 15분 동안 유효한 임시 URL 생성
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("S3 Presigned GET URL 생성 실패: {}", s3Key, e);
            return null; // 예외 발생 시 null 반환
        }
    }
}