import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        {/* Spring Boot의 /app URL과 맞추기 위해 basename 설정 */}
        <BrowserRouter basename="/app">
            <App />
        </BrowserRouter>
    </React.StrictMode>
);