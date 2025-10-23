package codeit.sb06.imagepost.service;

import codeit.sb06.imagepost.dto.FileMetaData;
import codeit.sb06.imagepost.dto.request.PostCreateRequest;
import codeit.sb06.imagepost.dto.request.PostUpdateRequest;
import codeit.sb06.imagepost.dto.response.PostResponse;
import codeit.sb06.imagepost.entity.Post;
import codeit.sb06.imagepost.entity.PostImage;
import codeit.sb06.imagepost.exception.ErrorCode;
import codeit.sb06.imagepost.exception.FileUploadException;
import codeit.sb06.imagepost.exception.InvalidPasswordException;
import codeit.sb06.imagepost.exception.PostNotFoundException;
import codeit.sb06.imagepost.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService; // 인터페이스에 의존
    private static final int MAX_IMAGE_COUNT = 5;

    @Transactional
    public PostResponse savePost(PostCreateRequest request, List<MultipartFile> images) {
        validateImageCount(images);

        // 1. 파일 스토리지에 저장 (local 또는 dev 프로필 구현체가 실행됨)
        List<FileMetaData> storedFiles = fileStorageService.storeFiles(images);

        // 2. Post 엔티티 생성
        Post post = request.toEntity();

        // 3. PostImage 엔티티 리스트 생성
        List<PostImage> postImages = storedFiles.stream()
                .map(meta -> PostImage.builder()
                        .storageUrl(meta.storageUrl())
                        .originalFileName(meta.originalFileName())
                        .build())
                .collect(Collectors.toList());

        // 4. Post 엔티티에 연관관계 설정
        post.setImages(postImages);

        // 5. Post 저장 (Cascade 설정으로 PostImage도 함께 DB에 저장됨)
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostUpdateRequest request, List<MultipartFile> images) {
        validateImageCount(images);

        Post post = findPostById(id);
        verifyPassword(post, request.password());

        // 1. 기존 스토리지 파일 삭제
        List<String> oldStorageUrls = post.getImages().stream()
                .map(PostImage::getStorageUrl)
                .collect(Collectors.toList());
        fileStorageService.deleteFiles(oldStorageUrls);

        // (이후 post.setImages() 호출 시 orphanRemoval=true에 의해 DB의 PostImage 레코드도 삭제됨)

        // 2. 새 파일 스토리지에 저장
        List<FileMetaData> newStoredFiles = fileStorageService.storeFiles(images);

        // 3. 새 PostImage 엔티티 리스트 생성
        List<PostImage> newPostImages = newStoredFiles.stream()
                .map(meta -> PostImage.builder()
                        .storageUrl(meta.storageUrl())
                        .originalFileName(meta.originalFileName())
                        .build())
                .collect(Collectors.toList());

        // 4. Post 엔티티 업데이트 (텍스트 정보)
        post.update(request.title(), request.content(), request.tags());
        // 5. Post 엔티티에 새 이미지 리스트 설정 (연관관계 교체)
        post.setImages(newPostImages);

        // 메서드 종료 시 @Transactional에 의해 변경 감지(Dirty Checking)되어 DB 업데이트
        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long id, String password) {
        Post post = findPostById(id);
        verifyPassword(post, password);

        // 1. 스토리지의 실제 파일 먼저 삭제
        List<String> storageUrls = post.getImages().stream()
                .map(PostImage::getStorageUrl)
                .collect(Collectors.toList());
        fileStorageService.deleteFiles(storageUrls);

        // 2. Post 엔티티 삭제
        // (CascadeType.ALL + orphanRemoval=true로 연관된 PostImage 레코드도 DB에서 함께 삭제)
        postRepository.delete(post);
    }

    public PostResponse getPostById(Long id) {
        Post post = findPostById(id);
        return PostResponse.from(post);
    }

    public List<PostResponse> findAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. ID: " + id));
    }


    private void verifyPassword(Post post, String providedPassword) {
        if (!post.getPassword().equals(providedPassword)) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 이미지 개수 검증
    private void validateImageCount(List<MultipartFile> images) {
        if (images != null && images.size() > MAX_IMAGE_COUNT) {
            // ErrorCode에 정의된 메시지 사용
            throw new FileUploadException(ErrorCode.INVALID_FILE_COUNT.getMessage());
        }
    }
}