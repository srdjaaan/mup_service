import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
    const navigate = useNavigate(); // koristi useNavigate u verziji 6

    const handleLogout = () => {
        // Brisanje tokena ili drugih podataka
        localStorage.removeItem('userToken');

        // Preusmeravanje na login stranicu
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-logo">MyApp</div>
            <div className="navbar-links">
                <Link to="/home">Home</Link>
                <button onClick={handleLogout}>Logout</button> {/* Dugme za logout */}
            </div>
        </nav>
    );
};

export default Navbar;
