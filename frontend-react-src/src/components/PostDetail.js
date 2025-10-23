import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getPostById, deletePost } from '../apiService';

function PostDetail() {
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { id } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPost = async () => {
            try {
                setLoading(true);
                setError(null);
                const response = await getPostById(id);
                setPost(response.data);
            } catch (err) {
                setError('게시글을 불러오지 못했습니다. (존재하지 않거나 삭제된 글)');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchPost();
    }, [id]);

    const handleDelete = async () => {
        const password = prompt("삭제하려면 비밀번호(6자리 숫자)를 입력하세요.");
        if (!password) {
            return;
        }

        try {
            await deletePost(id, password);
            alert('게시글이 삭제되었습니다.');
            navigate('/'); // 목록으로 이동
        } catch (err) {
            // 백엔드 ErrorResponse 메시지 (예: "비밀번호가 일치하지 않습니다.")
            const errorMsg = err.response?.data?.message || '삭제 중 오류가 발생했습니다.';
            alert(errorMsg); // alert로 에러 표시
            console.error(err);
        }
    };

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div className="error-msg">{error}</div>;
    if (!post) return <div>게시글을 찾을 수 없습니다.</div>;

    return (
        <div className="post-detail">
            <h2>{post.title}</h2>
            <p>
                <strong>작성자:</strong> {post.author} |
                <strong> 작성일:</strong> {new Date(post.createdAt).toLocaleString()}
            </p>

            <div className="post-tags" style={{ margin: '15px 0' }}>
                {post.tags.map((tag, index) => (
                    <span key={index} className="tag">#{tag}</span>
                ))}
            </div>

            <div className="post-content" style={{ whiteSpace: 'pre-wrap', borderTop: '1px solid #eee', paddingTop: '20px' }}>
                {/* whiteSpace: 'pre-wrap' : 엔터(개행)를 HTML에 반영 */}
                {post.content}
            </div>

            <div className="post-images">
                {post.images && post.images.map(image => (
                    <img key={image.id} src={image.imageUrl} alt="post content" />
                ))}
            </div>

            <div className="post-actions">
                <Link to={`/edit/${id}`}>
                    <button>수정</button>
                </Link>
                <button onClick={handleDelete} className="delete-btn">삭제</button>
            </div>
        </div>
    );
}

export default PostDetail;