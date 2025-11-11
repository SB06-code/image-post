package codeit.sb06.imagepost.service;

import codeit.sb06.imagepost.dto.FileMetaData;
import codeit.sb06.imagepost.exception.FileUploadException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Profile(value = {"local", "build"}) // 'local', 'build'  프로필일 때만 이 빈을 등록
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new FileUploadException("로컬 스토리지 디렉토리를 생성할 수 없습니다.", e);
        }
    }

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
            String storedFileName = UUID.randomUUID() + extension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(storedFileName)).normalize().toAbsolutePath();

            try {
                file.transferTo(destinationFile);
                // WebConfig에서 설정한 /uploads/ 경로와 파일명을 조합
                String storageUrl = "/uploads/" + storedFileName;
                storedFiles.add(new FileMetaData(storageUrl, originalFileName));
            } catch (IOException e) {
                log.error("파일 저장 실패: {}", originalFileName, e);
                throw new FileUploadException("파일 저장에 실패했습니다: " + originalFileName, e);
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
                // URL(예: /uploads/uuid.jpg)에서 파일명(uuid.jpg) 추출
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                Path filePath = this.rootLocation.resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                log.error("로컬 파일 삭제 실패: {}", url, e);
            }
        }
    }
}