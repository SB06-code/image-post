package codeit.sb06.imagepost.service;

import codeit.sb06.imagepost.dto.FileMetaData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    /**
     * 여러 개의 파일을 스토리지에 저장합니다.
     */
    List<FileMetaData> storeFiles(List<MultipartFile> files);

    /**
     * 스토리지에서 여러 개의 파일을 삭제합니다.
     */
    void deleteFiles(List<String> storageUrls);
}