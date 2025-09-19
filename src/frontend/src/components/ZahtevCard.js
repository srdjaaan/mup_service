import React, { useState, useEffect } from 'react';
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
    const [validacijaLoading, setValidacijaLoading] = useState(false);
    const [validacijaPoruka, setValidacijaPoruka] = useState('');
    const [starostLoading, setStarostLoading] = useState(false);
    const [starostPoruka, setStarostPoruka] = useState('');

    // Validacija lične karte kada se bira pasoš
    const validirajLicnuKartu = async () => {
        if (formData.tipDokumenta !== 'PASOS') {
            setValidacijaPoruka('');
            return;
        }

        setValidacijaLoading(true);
        setValidacijaPoruka('');
        
        try {
            const response = await mupApi.validirajLicnuKartu(user.jmbg);
            if (response.data.status === 'VALIDNA') {
                setValidacijaPoruka('Lična karta je validna. Možete kreirati zahtev za pasoš.');
            } else {
                setValidacijaPoruka(response.data.poruka);
            }
        } catch (err) {
            setValidacijaPoruka('Greška pri validaciji lične karte');
        } finally {
            setValidacijaLoading(false);
        }
    };

    // Validacija starosti
    const validirajStarost = async () => {
        setStarostLoading(true);
        setStarostPoruka('');
        
        try {
            const response = await mupApi.validirajStarost(user.jmbg, formData.tipDokumenta);
            if (response.data.status === 'VALIDNA') {
                setStarostPoruka('Starost je validna za kreiranje dokumenta');
            } else {
                setStarostPoruka(response.data.poruka);
            }
        } catch (err) {
            setStarostPoruka('Greška pri validaciji starosti');
        } finally {
            setStarostLoading(false);
        }
    };

    // Pozovi validaciju kada se promeni tip dokumenta
    useEffect(() => {
        // Validacija starosti za sve tipove dokumenata
        validirajStarost();
        
        // Validacija lične karte samo za pasoš
        if (formData.tipDokumenta === 'PASOS') {
            validirajLicnuKartu();
        } else {
            setValidacijaPoruka('');
        }
    }, [formData.tipDokumenta]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Proveri validaciju starosti
        if (starostPoruka && !starostPoruka.includes('validna')) {
            setError(starostPoruka);
            return;
        }
        
        // Proveri validaciju za pasoš
        if (formData.tipDokumenta === 'PASOS' && validacijaPoruka && !validacijaPoruka.includes('validna')) {
            setError('Morate imati validnu ličnu kartu da bi kreirali pasoš');
            return;
        }
        
        setLoading(true);
        setError('');
        setMessage('');

        try {
            const response = await mupApi.kreirajZahtev(formData);
            setMessage('Zahtev je uspešno poslat!');
            setFormData({ tipDokumenta: 'LICNA_KARTA', razlog: '' });
            setValidacijaPoruka('');
            setStarostPoruka('');
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
                <h3>Zahtev za Dokument</h3>
                <p>Pošaljite zahtev za kreiranje ili produženje dokumenata</p>
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
                        <option value="PASOS">Kreiranje pasoša</option>
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

                {/* Validacija starosti */}
                <div className="form-group">
                    <div className="validation-section">
                        <label>Validacija starosti:</label>
                        {starostLoading ? (
                            <div className="loading-indicator">
                                <span>Proverava se starost...</span>
                            </div>
                        ) : starostPoruka ? (
                            <div className={`validation-message ${starostPoruka.includes('validna') ? 'success' : 'error'}`}>
                                {starostPoruka}
                            </div>
                        ) : null}
                    </div>
                </div>

                {/* Validacija za pasoš */}
                {formData.tipDokumenta === 'PASOS' && (
                    <div className="form-group">
                        <div className="validation-section">
                            <label></label>
                            {validacijaLoading ? (
                                <div className="loading-indicator">
                                    <span>Proverava se lična karta...</span>
                                </div>
                            ) : validacijaPoruka ? (
                                <div className={`validation-message ${validacijaPoruka.includes('validna') ? 'success' : 'error'}`}>
                                    {validacijaPoruka}
                                </div>
                            ) : null}
                        </div>
                    </div>
                )}

                {error && <div className="error-message">{error}</div>}
                {message && <div className="success-message">{message}</div>}

                <button 
                    type="submit" 
                    className="submit-btn"
                    disabled={loading || 
                             (starostPoruka && !starostPoruka.includes('validna')) ||
                             (formData.tipDokumenta === 'PASOS' && validacijaPoruka && !validacijaPoruka.includes('validna'))}
                >
                    {loading ? 'Šalje se...' : 'Pošalji zahtev'}
                </button>
            </form>
        </div>
    );
};

export default ZahtevCard;
