package codeit.sb06.imagepost.controller;

import codeit.sb06.imagepost.dto.request.PostCreateRequest;
import codeit.sb06.imagepost.dto.request.PostDeleteRequest;
import codeit.sb06.imagepost.dto.request.PostUpdateRequest;
import codeit.sb06.imagepost.dto.response.PostResponse;
import codeit.sb06.imagepost.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 'consumes'를 통해 JSON(@RequestPart("request"))과 FormData(@RequestPart("images"))를 함께 받도록 설정
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestPart("request") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        PostResponse response = postService.savePost(request, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestPart("request") PostUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        PostResponse response = postService.updatePost(id, request, images);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @Valid @RequestBody PostDeleteRequest request
    ) {
        postService.deletePost(id, request.password());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> responses = postService.findAllPosts();
        return ResponseEntity.ok(responses);
    }
}