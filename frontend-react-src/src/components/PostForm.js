import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { createPost, getPostById, updatePost } from '../apiService';

function PostForm() {
    const [formData, setFormData] = useState({
        author: '',
        password: '',
        title: '',
        content: '',
        tags: '',
    });
    const [files, setFiles] = useState(null); // FileList 객체
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { id } = useParams(); // URL 파라미터 (수정 모드 식별 /edit/:id)
    const isEditMode = Boolean(id);

    useEffect(() => {
        // 수정 모드인 경우, 기존 게시글 데이터를 불러옵니다.
        if (isEditMode) {
            const fetchPost = async () => {
                try {
                    setLoading(true);
                    const response = await getPostById(id);
                    const post = response.data;
                    setFormData({
                        author: post.author, // 작성자는 UI에서 disabled
                        password: '', // 비밀번호는 항상 새로 입력
                        title: post.title,
                        content: post.content,
                        tags: post.tags.join(', '), // 배열을 콤마로 구분된 문자열로
                    });
                } catch (err) {
                    setError('게시글 정보를 불러오지 못했습니다.');
                } finally {
                    setLoading(false);
                }
            };
            fetchPost();
        }
    }, [id, isEditMode]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleFileChange = (e) => {
        // 파일 개수 제한 (최대 5개)
        if (e.target.files.length > 5) {
            alert('이미지는 최대 5개까지 업로드할 수 있습니다.');
            e.target.value = null; // 파일 선택 초기화
            setFiles(null);
        } else {
            setFiles(e.target.files); // FileList 객체 저장
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (loading) return;

        // API로 보낼 데이터 (FileList 포함)
        const postData = { ...formData, images: files };

        try {
            setLoading(true);
            setError(null);

            if (isEditMode) {
                // --- 수정 요청 ---
                await updatePost(id, postData);
                alert('게시글이 수정되었습니다.');
                navigate(`/post/${id}`); // 수정된 글로 이동
            } else {
                // --- 생성 요청 ---
                await createPost(postData);
                alert('게시글이 작성되었습니다.');
                navigate('/'); // 목록으로 이동
            }
        } catch (err) {
            // 백엔드에서 보낸 ErrorResponse의 message 필드 사용
            const errorMsg = err.response?.data?.message || '작업 중 오류가 발생했습니다.';
            setError(errorMsg);
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="post-form">
            <h2>{isEditMode ? '게시글 수정' : '새 글 작성'}</h2>
            {error && <div className="error-msg">{error}</div>}

            <input
                type="text"
                name="author"
                value={formData.author}
                onChange={handleChange}
                placeholder="작성자"
                required
                disabled={isEditMode} // 수정 모드에서는 작성자 변경 불가
            />

            <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder={isEditMode ? "수정/삭제용 비밀번호 (6자리 숫자)" : "비밀번호 (6자리 숫자)"}
                required
                pattern="\d{6}"
                title="비밀번호는 6자리 숫자여야 합니다."
            />

            <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="제목"
                required
            />

            <textarea
                name="content"
                value={formData.content}
                onChange={handleChange}
                placeholder="내용"
                required
            />

            <input
                type="text"
                name="tags"
                value={formData.tags}
                onChange={handleChange}
                placeholder="태그 (쉼표로 구분. 예: java, spring)"
            />

            {/* ===== 수정된 부분 시작 ===== */}
            <div>
                <label>이미지 (최대 5개, 각 10MB)</label>
                <input
                    type="file"
                    name="images"
                    onChange={handleFileChange}
                    multiple
                    accept="image/*"
                    // 접근성 향상: input 필드가 아래 설명(id="image-help-text")과 연관됨을 명시
                    aria-describedby={isEditMode ? "image-help-text" : undefined}
                />
                {isEditMode && (
                    // <p> 태그 대신 <div>를 사용하고 ID 부여
                    <div
                        id="image-help-text"
                        style={{ fontSize: '0.9em', color: '#555', marginTop: '5px' }}
                    >
                        (참고: 이미지를 새로 첨부하면 기존 이미지들은 모두 삭제되고 새 이미지로 교체됩니다.)
                    </div>
                )}
            </div>
            {/* ===== 수정된 부분 끝 ===== */}

            <button type="submit" disabled={loading}>
                {loading ? '전송 중...' : (isEditMode ? '수정하기' : '작성하기')}
            </button>
        </form>
    );
}

export default PostForm;