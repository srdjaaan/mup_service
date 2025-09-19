import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import ZahtevCard from '../components/ZahtevCard';
import PolicajacPanel from '../components/PolicajacPanel';
import { useAuth } from '../context/AuthContext';
import { mupApi } from '../api/mupApi';
import './Home.css';

const Home = () => {
    const { user, loading } = useAuth();
    const [zahtevi, setZahtevi] = useState([]);
    const [loadingZahtevi, setLoadingZahtevi] = useState(false);

    useEffect(() => {
        if (user?.role === 'GRADJANIN') {
            fetchZahtevi();
        }
    }, [user]);

    const fetchZahtevi = async () => {
        setLoadingZahtevi(true);
        try {
            const response = await mupApi.getMojiZahtevi();
            setZahtevi(response.data);
        } catch (error) {
            console.error('Greška pri dohvatanju zahteva:', error);
        } finally {
            setLoadingZahtevi(false);
        }
    };

    const handleZahtevCreated = (noviZahtev) => {
        setZahtevi(prev => [noviZahtev, ...prev]);
    };

    if (loading) {
        return (
            <div className="loading-container">
                <div className="loading-spinner"></div>
                <p>Učitavanje...</p>
            </div>
        );
    }

    return (
        <div className="home-container">
            <Navbar />
            <div className="home-content">
                <div className="welcome-section">
                    <h1>Dobrodošli na sajt za eUpravu</h1>
                    <p>Ovde možete upravljati svojim dokumentima i slati zahteve</p>
                </div>

                {user?.role === 'GRADJANIN' && (
                    <div className="gradjanin-section">
                        <ZahtevCard user={user} onZahtevCreated={handleZahtevCreated} />
                        
                        <div className="zahtevi-section">
                            <h2>Moji zahtevi</h2>
                            {loadingZahtevi ? (
                                <div className="loading-container">
                                    <div className="loading-spinner"></div>
                                    <p>Učitavanje zahteva...</p>
                                </div>
                            ) : zahtevi.length > 0 ? (
                                <div className="zahtevi-list">
                                    {zahtevi.map(zahtev => (
                                        <div key={zahtev.id} className="zahtev-item">
                                            <div className="zahtev-header">
                                                <h3>Zahtev #{zahtev.id}</h3>
                                                <span className={`status status-${zahtev.status?.toLowerCase()}`}>
                                                    {zahtev.status}
                                                </span>
                                            </div>
                                            <div className="zahtev-details">
                                                <p><strong>Tip dokumenta:</strong> {zahtev.tipDokumenta}</p>
                                                <p><strong>Razlog:</strong> {zahtev.razlog}</p>
                                                <p><strong>Datum kreiranja:</strong> {new Date(zahtev.datumKreiranja).toLocaleDateString('sr-RS')}</p>
                                                {zahtev.komentar && (
                                                    <p><strong>Komentar:</strong> {zahtev.komentar}</p>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="no-zahtevi">Nemate poslatih zahteva</p>
                            )}
                        </div>
                    </div>
                )}

                {user?.role === 'POLICAJAC' && (
                    <PolicajacPanel user={user} />
                )}
            </div>
        </div>
    );
};

export default Home;
