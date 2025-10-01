import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import NotificationBell from './NotificationBell';
import NotificationModal from './NotificationModal';
import './Navbar.css';

const Navbar = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const [showUserMenu, setShowUserMenu] = useState(false);
    const [showNotifications, setShowNotifications] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const toggleUserMenu = () => {
        setShowUserMenu(!showUserMenu);
    };

    const handleProfileClick = () => {
        navigate('/profile');
        setShowUserMenu(false);
    };

    const handleNotificationClick = () => {
        setShowNotifications(true);
    };

    const handleNotificationClose = () => {
        setShowNotifications(false);
    };

    const handleNotificationRefresh = () => {
        // Refresh notification bell
        window.location.reload();
    };

    return (
        <nav className="navbar">
            <div className="navbar-logo">
                <Link to="/home">eUprava</Link>
            </div>
            <div className="navbar-links">
                <Link to="/home" className="nav-link">Home</Link>
                {user && (
                    <div className="navbar-right">
                        <Link to="/opendata" className="nav-link">Open Data</Link>
                        <NotificationBell 
                            user={user} 
                            onNotificationClick={handleNotificationClick}
                        />
                        <div className="user-section">
                        <div className="user-info" onClick={toggleUserMenu}>
                            <div className="user-avatar">
                                {user.name?.charAt(0)}{user.lastname?.charAt(0)}
                            </div>
                            <div className="user-details">
                                <span className="user-name">{user.name} {user.lastname}</span>
                                <span className="user-role">{user.role}</span>
                            </div>
                            <div className="dropdown-arrow">▼</div>
                        </div>

                        {showUserMenu && (
                            <div className="user-menu">
                                <div className="menu-item" onClick={handleProfileClick}>
                                    <span className="menu-icon">👤</span>
                                    Profil
                                </div>
                                <div className="menu-divider"></div>
                                <div className="menu-item" onClick={handleLogout}>
                                    <span className="menu-icon">🚪</span>
                                    Logout
                                </div>
                            </div>
                        )}
                        </div>
                    </div>
                )}
            </div>
            
            <NotificationModal 
                user={user}
                isOpen={showNotifications}
                onClose={handleNotificationClose}
                onRefresh={handleNotificationRefresh}
            />
        </nav>
    );
};

export default Navbar;
