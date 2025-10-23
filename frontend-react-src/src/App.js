import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import PostList from './components/PostList';
import PostDetail from './components/PostDetail';
import PostForm from './components/PostForm';

function App() {
    return (
        <div className="container">
            <nav>
                <Link to="/"><h1>Image-Post</h1></Link>
                <Link to="/new" className="new-post-btn">새 글 작성</Link>
            </nav>
            <main>
                <Routes>
                    <Route path="/" element={<PostList />} />
                    <Route path="/post/:id" element={<PostDetail />} />
                    <Route path="/new" element={<PostForm />} />
                    <Route path="/edit/:id" element={<PostForm />} />
                </Routes>
            </main>
        </div>
    );
}

export default App;