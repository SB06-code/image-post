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
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

                // 저장된 객체의 S3 URL 가져오기
                String storageUrl = s3Client.utilities()
                        .getUrl(GetUrlRequest.builder().bucket(bucket).key(s3Key).build())
                        .toString();

                storedFiles.add(new FileMetaData(storageUrl, originalFileName));

            } catch (IOException e) {
                log.error("S3 파일 업로드 실패: {}", originalFileName, e);
                throw new FileUploadException("S3 파일 업로드에 실패했습니다: " + originalFileName, e);
            }
        }
        return storedFiles;
    }

    @Override
    public void deleteFiles(List<String> storageUrls) {
        if (storageUrls == null || storageUrls.isEmpty()) {
            return;
        }

        for (String url : storageUrls) {
            try {
                // S3 URL에서 객체 키(Key) 추출
                String key = extractKeyFromUrl(url);

                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);

            } catch (Exception e) {
                log.error("S3 파일 삭제 실패: {}", url, e);
            }
        }
    }

    // S3 URL에서 S3 Key(파일 경로)를 추출하는 헬퍼 메서드
    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            // URL 디코딩(한글 등) 후, 맨 앞의 '/' 제거
            return URLDecoder.decode(path.substring(1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("S3 URL에서 키 추출 실패: {}", fileUrl, e);
            throw new IllegalArgumentException("S3 URL 형식이 잘못되었습니다.", e);
        }
    }
}