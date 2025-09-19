import React, { useState } from 'react';
import axios from '../api/axios';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './AuthForms.css';

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('/api/auth/login', { username, password });
            localStorage.setItem('token', response.data.token);
            
            // Dekodiraj token da dobijemo podatke o korisniku
            const payload = JSON.parse(atob(response.data.token.split('.')[1]));
            const userData = {
                jmbg: payload.jmbg,
                username: payload.sub,
                role: payload.role,
                name: payload.name,
                lastname: payload.lastname
            };
            
            login(userData);
            navigate('/home');
        } catch (err) {
            setError('Pogrešna lozinka ili korisničko ime');
        }
    };

    return (
        <div className="auth-container">
            <form className="auth-form" onSubmit={handleSubmit}>
                <h2>Login</h2>
                {error && <p className="error">{error}</p>}
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit">Login</button>
                <p className="redirect-text">
                    Nemate profil? <Link to="/register">Registrujte se ovde</Link>
                </p>
            </form>
        </div>
    );
};

export default LoginForm;
