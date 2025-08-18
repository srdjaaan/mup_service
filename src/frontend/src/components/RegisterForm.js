import React, { useState } from 'react';
import axios from '../api/axios';
import { useNavigate } from 'react-router-dom';
import './AuthForms.css';

const RegisterForm = () => {
    const [jmbg, setJmbg] = useState('');
    const [name, setName] = useState('');
    const [lastname, setLastname] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [birthday, setBirthday] = useState('');
    const [placeOfBirth, setPlaceOfBirth] = useState('');
    const [role, setRole] = useState('GRADJANIN');
    const [gender, setGender] = useState('MUSKI');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('/api/auth/register', {
                jmbg,
                name,
                lastname,
                username,
                password,
                birthday,
                placeOfBirth,
                role,
                gender
            },{
            headers: {
                'Content-Type': 'application/json' // Dodaj header za JSON format
            }
        });
        navigate('/login'); // Prebaci na login nakon uspešne registracije
    } catch (err) {
        setError('Greška prilikom registracije');
        console.error(err);
    }
    };

    return (
        <div className="auth-container">
            <form className="auth-form" onSubmit={handleSubmit}>
                <h2>Register</h2>
                {error && <p className="error">{error}</p>}

                <input
                    type="text"
                    placeholder="JMBG"
                    value={jmbg}
                    onChange={(e) => setJmbg(e.target.value)}
                    required
                />
                <input
                    type="text"
                    placeholder="First Name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
                <input
                    type="text"
                    placeholder="Last Name"
                    value={lastname}
                    onChange={(e) => setLastname(e.target.value)}
                    required
                />
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <input
                    type="date"
                    placeholder="Birthday"
                    value={birthday}
                    onChange={(e) => setBirthday(e.target.value)}
                    required
                />
                <input
                    type="text"
                    placeholder="Place of Birth"
                    value={placeOfBirth}
                    onChange={(e) => setPlaceOfBirth(e.target.value)}
                    required
                />

                <div className="select-group">
                    <select value={role} onChange={(e) => setRole(e.target.value)}>
                        <option value="GRADJANIN">GRADJANIN</option>
                        <option value="POLICAJAC">POLICAJAC</option>
                    </select>

                    <select value={gender} onChange={(e) => setGender(e.target.value)}>
                        <option value="MUSKI">MUSKI</option>
                        <option value="ZENSKI">ZENSKI</option>
                    </select>
                </div>

                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default RegisterForm;
