import React, { useState, useEffect } from 'react';
import { mupApi } from '../api/mupApi';
import './NotificationModal.css';

const NotificationModal = ({ user, isOpen, onClose, onRefresh }) => {
    const [obavestenja, setObavestenja] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (isOpen && user?.jmbg) {
            fetchObavestenja();
        }
    }, [isOpen, user]);

    const fetchObavestenja = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await mupApi.getObavestenja(user.jmbg);
            setObavestenja(response.data);
        } catch (err) {
            setError('Greška pri dohvatanju obaveštenja');
            console.error('Error fetching notifications:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleMarkAsRead = async (notificationId) => {
        try {
            await mupApi.oznaciKaoProcitanu(notificationId);
            setObavestenja(prev => 
                prev.map(notif => 
                    notif.id === notificationId 
                        ? { ...notif, isRead: true }
                        : notif
                )
            );
            if (onRefresh) {
                onRefresh();
            }
        } catch (err) {
            console.error('Error marking notification as read:', err);
        }
    };

    const handleMarkAllAsRead = async () => {
        try {
            await mupApi.oznaciSveKaoProcitane(user.jmbg);
            setObavestenja(prev => 
                prev.map(notif => ({ ...notif, isRead: true }))
            );
            if (onRefresh) {
                onRefresh();
            }
        } catch (err) {
            console.error('Error marking all notifications as read:', err);
        }
    };

    const getNotificationIcon = (type) => {
        switch (type) {
            case 'ZAHTEV_ODOBREN':
                return '✅';
            case 'ZAHTEV_ODBIJEN':
                return '❌';
            case 'DOKUMENT_KREIRAN':
                return '📄';
            case 'DOKUMENT_ISTEKAO':
                return '⚠️';
            case 'SISTEM':
                return '🔧';
            default:
                return '📢';
        }
    };

    const getNotificationColor = (type) => {
        switch (type) {
            case 'ZAHTEV_ODOBREN':
                return 'success';
            case 'ZAHTEV_ODBIJEN':
                return 'error';
            case 'DOKUMENT_KREIRAN':
                return 'info';
            case 'DOKUMENT_ISTEKAO':
                return 'warning';
            case 'SISTEM':
                return 'system';
            default:
                return 'default';
        }
    };

    if (!isOpen) return null;

    return (
        <div className="notification-modal-overlay" onClick={onClose}>
            <div className="notification-modal" onClick={e => e.stopPropagation()}>
                <div className="notification-modal-header">
                    <h3>Obaveštenja</h3>
                    <div className="notification-actions">
                        <button 
                            onClick={handleMarkAllAsRead}
                            className="mark-all-read-btn"
                            disabled={obavestenja.every(n => n.isRead)}
                        >
                            Označi sve kao pročitano
                        </button>
                        <button onClick={onClose} className="close-btn">&times;</button>
                    </div>
                </div>

                <div className="notification-modal-body">
                    {loading ? (
                        <div className="loading-container">
                            <div className="loading-spinner"></div>
                            <p>Učitavanje obaveštenja...</p>
                        </div>
                    ) : error ? (
                        <div className="error-message">{error}</div>
                    ) : obavestenja.length === 0 ? (
                        <div className="no-notifications">
                            <p>Nemate obaveštenja</p>
                        </div>
                    ) : (
                        <div className="notifications-list">
                            {obavestenja.map(notification => (
                                <div 
                                    key={notification.id} 
                                    className={`notification-item ${!notification.isRead ? 'unread' : ''} ${getNotificationColor(notification.type)}`}
                                    onClick={() => !notification.isRead && handleMarkAsRead(notification.id)}
                                >
                                    <div className="notification-icon">
                                        {getNotificationIcon(notification.type)}
                                    </div>
                                    <div className="notification-content">
                                        <div className="notification-title">
                                            {notification.title}
                                            {!notification.isRead && <span className="unread-dot"></span>}
                                        </div>
                                        <div className="notification-message">
                                            {notification.message}
                                        </div>
                                        <div className="notification-time">
                                            {new Date(notification.createdAt).toLocaleString('sr-RS')}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default NotificationModal;