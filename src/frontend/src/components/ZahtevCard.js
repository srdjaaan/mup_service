import React, { useState, useEffect } from 'react';
import { mupApi } from '../api/mupApi';
import './ZahtevCard.css';

const ZahtevCard = ({ user, onZahtevCreated }) => {
    const [formData, setFormData] = useState({
        tipDokumenta: 'LICNA_KARTA',
        razlog: '',
        kategorija: ''
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [validacijaLoading, setValidacijaLoading] = useState(false);
    const [validacijaPoruka, setValidacijaPoruka] = useState('');
    const [starostLoading, setStarostLoading] = useState(false);
    const [starostPoruka, setStarostPoruka] = useState('');

    // Validacija lične karte kada se bira pasoš ili vozacka dozvola
    const validirajLicnuKartu = async () => {
        if (formData.tipDokumenta !== 'PASOS' && formData.tipDokumenta !== 'VOZACKA_DOZVOLA') {
            setValidacijaPoruka('');
            return;
        }

        setValidacijaLoading(true);
        setValidacijaPoruka('');
        
        try {
            const response = await mupApi.validirajLicnuKartu(user.jmbg);
            if (response.data.status === 'VALIDNA') {
                setValidacijaPoruka('Lična karta je validna. Možete kreirati zahtev.');
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
        // Validacija lične karte za pasoš i vozacku dozvolu
        if (formData.tipDokumenta === 'PASOS' || formData.tipDokumenta === 'VOZACKA_DOZVOLA') {
            validirajLicnuKartu();
        } else {
            setValidacijaPoruka('');
        }
        
        // Resetuj starost poruku kada se promeni tip dokumenta
        setStarostPoruka('');
    }, [formData.tipDokumenta]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Proveri da li je kategorija odabrana za vozacku dozvolu
        if (formData.tipDokumenta === 'VOZACKA_DOZVOLA' && !formData.kategorija) {
            setError('Morate odabrati kategoriju za vozacku dozvolu');
            return;
        }
        
        // Proveri validaciju za pasoš i vozacku dozvolu
        if ((formData.tipDokumenta === 'PASOS' || formData.tipDokumenta === 'VOZACKA_DOZVOLA') && validacijaPoruka && !validacijaPoruka.includes('validna')) {
            setError('Morate imati validnu ličnu kartu da bi kreirali dokument');
            return;
        }
        
        setLoading(true);
        setError('');
        setMessage('');

        try {
            // Pripremi podatke za slanje - ukloni kategorija ako nije vozacka dozvola
            const zahtevData = { ...formData };
            if (formData.tipDokumenta !== 'VOZACKA_DOZVOLA') {
                delete zahtevData.kategorija;
            }
            
            const response = await mupApi.kreirajZahtev(zahtevData);
            setMessage('Zahtev je uspešno poslat!');
            setFormData({ tipDokumenta: 'LICNA_KARTA', razlog: '', kategorija: '' });
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
                        <option value="VOZACKA_DOZVOLA">Kreiranje vozacke dozvole</option>
                    </select>
                </div>

                {/* Kategorija za vozacku dozvolu */}
                {formData.tipDokumenta === 'VOZACKA_DOZVOLA' && (
                    <div className="form-group">
                        <label htmlFor="kategorija">Kategorija vozacke dozvole:</label>
                        <select
                            id="kategorija"
                            name="kategorija"
                            value={formData.kategorija}
                            onChange={handleInputChange}
                            required
                        >
                            <option value="">Odaberite kategoriju</option>
                            <option value="A">A - Motor (16+ godina)</option>
                            <option value="B">B - Automobil (18+ godina)</option>
                            <option value="C">C - Kamion (21+ godina)</option>
                            <option value="D">D - Autobus (21+ godina)</option>
                        </select>
                    </div>
                )}

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


                {/* Validacija za pasoš i vozacku dozvolu */}
                {(formData.tipDokumenta === 'PASOS' || formData.tipDokumenta === 'VOZACKA_DOZVOLA') && (
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
                             ((formData.tipDokumenta === 'PASOS' || formData.tipDokumenta === 'VOZACKA_DOZVOLA') && validacijaPoruka && !validacijaPoruka.includes('validna')) ||
                             (formData.tipDokumenta === 'VOZACKA_DOZVOLA' && !formData.kategorija)}
                >
                    {loading ? 'Šalje se...' : 'Pošalji zahtev'}
                </button>
            </form>
        </div>
    );
};

export default ZahtevCard;
