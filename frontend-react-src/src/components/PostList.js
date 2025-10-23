import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllPosts } from '../apiService';

function PostList() {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchPosts();
    }, []);

    const fetchPosts = async () => {
        try {
            setLoading(true);
            const response = await getAllPosts();
            setPosts(response.data); // API로부터 받은 데이터
            setError(null);
        } catch (err) {
            setError('게시글을 불러오는 데 실패했습니다.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div className="error-msg">{error}</div>;

    return (
        <div className="post-list">
            {posts.length === 0 ? (
                <p>게시글이 없습니다. 새 글을 작성해보세요.</p>
            ) : (
                posts.map(post => (
                    <div key={post.id} className="post-item">
                        <Link to={`/post/${post.id}`}>
                            <h3>{post.title}</h3>
                        </Link>
                        <p>작성자: {post.author} | 작성일: {new Date(post.createdAt).toLocaleDateString()}</p>
                        {/* 첫 번째 이미지를 썸네일로 표시 */}
                        {post.images && post.images.length > 0 && (
                            <img src={post.images[0].imageUrl} alt="thumbnail" className="thumbnail" />
                        )}
                    </div>
                ))
            )}
        </div>
    );
}

export default PostList;