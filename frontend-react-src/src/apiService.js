import axios from 'axios';

// create-react-app의 proxy 설정을 사용하므로 상대 경로로 API 호출
// (빌드 후에는 Spring Boot 서버의 /api/posts로 요청됨)
const API_URL = '/api/posts';

const apiClient = axios.create({
    baseURL: API_URL,
});

// 모든 게시글 조회
export const getAllPosts = () => apiClient.get('');

// 특정 게시글 조회
export const getPostById = (id) => apiClient.get(`/${id}`);

// 게시글 생성 (FormData 사용)
export const createPost = (postData) => {
    const formData = new FormData();

    // 1. JSON DTO를 'request' 파트로 추가 (Blob 사용)
    const postRequestDto = {
        author: postData.author,
        password: postData.password,
        title: postData.title,
        content: postData.content,
        tags: postData.tags.split(',').map(tag => tag.trim()).filter(tag => tag),
    };
    formData.append('request', new Blob([JSON.stringify(postRequestDto)], {
        type: "application/json"
    }));

    // 2. 이미지 파일(FileList)을 'images' 파트로 추가
    if (postData.images) { // postData.images는 FileList 객체
        for (let i = 0; i < postData.images.length; i++) {
            formData.append('images', postData.images[i]);
        }
    }

    return apiClient.post('', formData, {
        headers: {
            // Content-Type은 axios가 FormData를 감지하여 자동으로 'multipart/form-data'로 설정함
        },
    });
};

// 게시글 수정 (createPost와 동일한 FormData 방식)
export const updatePost = (id, postData) => {
    const formData = new FormData();

    // 1. JSON DTO ('request' 파트)
    const postUpdateDto = {
        password: postData.password, // 수정 시 비밀번호는 필수
        title: postData.title,
        content: postData.content,
        tags: postData.tags.split(',').map(tag => tag.trim()).filter(tag => tag),
    };
    formData.append('request', new Blob([JSON.stringify(postUpdateDto)], {
        type: "application/json"
    }));

    // 2. 이미지 파일 ('images' 파트)
    if (postData.images) {
        for (let i = 0; i < postData.images.length; i++) {
            formData.append('images', postData.images[i]);
        }
    }

    return apiClient.put(`/${id}`, formData, {
        headers: {
            // 'Content-Type': 'multipart/form-data' (자동 설정)
        },
    });
};

// 게시글 삭제 (PostDeleteRequest DTO 전송)
export const deletePost = (id, password) => {
    return apiClient.delete(`/${id}`, {
        // Spring의 @RequestBody로 받기 위해 'data' 필드에 DTO를 넣어 전송
        data: { password: password }
    });
};