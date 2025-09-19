import React, { useState } from 'react';
import { mupApi } from '../api/mupApi';
import './ZahtevCard.css';

const ZahtevCard = ({ user, onZahtevCreated }) => {
    const [formData, setFormData] = useState({
        tipDokumenta: 'LICNA_KARTA',
        razlog: ''
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setMessage('');

        try {
            const response = await mupApi.kreirajZahtev(formData);
            setMessage('Zahtev je uspešno poslat!');
            setFormData({ tipDokumenta: 'LICNA_KARTA', razlog: '' });
            if (onZahtevCreated) {
                onZahtevCreated(response.data);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Greška pri slanju zahteva');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    if (user?.role !== 'GRADJANIN') {
        return null;
    }

    return (
        <div className="zahtev-card">
            <div className="zahtev-card-header">
                <h3>Zahtev za Ličnu Kartu</h3>
                <p>Pošaljite zahtev za kreiranje ili produženje lične karte</p>
            </div>
            
            <form onSubmit={handleSubmit} className="zahtev-form">
                <div className="form-group">
                    <label htmlFor="tipDokumenta">Tip dokumenta:</label>
                    <select
                        id="tipDokumenta"
                        name="tipDokumenta"
                        value={formData.tipDokumenta}
                        onChange={handleInputChange}
                        required
                    >
                        <option value="LICNA_KARTA">Kreiranje lične karte</option>
                        <option value="PRODUZENJE_LICNE_KARTE">Produženje lične karte</option>
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="razlog">Razlog:</label>
                    <textarea
                        id="razlog"
                        name="razlog"
                        value={formData.razlog}
                        onChange={handleInputChange}
                        placeholder="Opišite razlog za zahtev..."
                        rows="3"
                        required
                    />
                </div>

                {error && <div className="error-message">{error}</div>}
                {message && <div className="success-message">{message}</div>}

                <button 
                    type="submit" 
                    className="submit-btn"
                    disabled={loading}
                >
                    {loading ? 'Šalje se...' : 'Pošalji zahtev'}
                </button>
            </form>
        </div>
    );
};

export default ZahtevCard;
