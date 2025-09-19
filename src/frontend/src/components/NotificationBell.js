import React, { useState, useEffect } from 'react';
import { mupApi } from '../api/mupApi';
import './NotificationBell.css';

const NotificationBell = ({ user, onNotificationClick }) => {
    const [brojNeprocitanih, setBrojNeprocitanih] = useState(0);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (user?.jmbg) {
            fetchBrojNeprocitanih();
        }
    }, [user]);

    const fetchBrojNeprocitanih = async () => {
        setLoading(true);
        try {
            const response = await mupApi.getBrojNeprocitanihObavestenja(user.jmbg);
            setBrojNeprocitanih(response.data.brojNeprocitanih);
        } catch (error) {
            console.error('Greška pri dohvatanju broja obaveštenja:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleClick = () => {
        if (onNotificationClick) {
            onNotificationClick();
        }
    };

    if (user?.role !== 'GRADJANIN') {
        return null;
    }

    return (
        <div className="notification-bell" onClick={handleClick}>
            <div className="bell-icon">
                🔔
            </div>
            {brojNeprocitanih > 0 && (
                <div className="notification-badge">
                    {brojNeprocitanih > 99 ? '99+' : brojNeprocitanih}
                </div>
            )}
        </div>
    );
};

export default NotificationBell;
