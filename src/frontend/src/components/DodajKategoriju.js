import React, { useState, useEffect } from 'react';
import { mupApi } from '../api/mupApi';
import './DodajKategoriju.css';

const DodajKategoriju = ({ user, onKategorijaDodana }) => {
    const [formData, setFormData] = useState({
        kategorija: '',
        razlog: ''
    });
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [vozackaDozvola, setVozackaDozvola] = useState(null);
    const [loadingVozacka, setLoadingVozacka] = useState(false);

    // Dohvati informacije o vozackoj dozvoli
    const fetchVozackaDozvola = async () => {
        setLoadingVozacka(true);
        try {
            const response = await mupApi.validirajVozackuDozvolu(user.jmbg);
            setVozackaDozvola(response.data);
        } catch (err) {
            setError('Nemate vozacku dozvolu ili je istekla');
        } finally {
            setLoadingVozacka(false);
        }
    };

    useEffect(() => {
        if (user?.role === 'GRADJANIN') {
            fetchVozackaDozvola();
        }
    }, [user]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.kategorija) {
            setError('Morate odabrati kategoriju');
            return;
        }

        if (!formData.razlog) {
            setError('Morate uneti razlog');
            return;
        }

        setLoading(true);
        setError('');
        setMessage('');

        try {
            // Kreiraj zahtev za dodavanje kategorije umesto direktnog dodavanja
            const zahtevData = {
                tipDokumenta: 'VOZACKA_DOZVOLA',
                kategorija: formData.kategorija,
                razlog: `Dodavanje kategorije ${formData.kategorija}: ${formData.razlog}`
            };
            
            await mupApi.kreirajZahtev(zahtevData);
            setMessage('Zahtev za dodavanje kategorije je uspešno poslat!');
            setFormData({ kategorija: '', razlog: '' });
            if (onKategorijaDodana) {
                onKategorijaDodana();
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Greška pri slanju zahteva za dodavanje kategorije');
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

    if (loadingVozacka) {
        return (
            <div className="dodaj-kategoriju-card">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Proverava se vozacka dozvola...</p>
                </div>
            </div>
        );
    }

    if (!vozackaDozvola) {
        return (
            <div className="dodaj-kategoriju-card">
                <div className="no-license">
                    <h3>Dodavanje kategorije na vozacku dozvolu</h3>
                    <p>Nemate vozacku dozvolu ili je istekla. Prvo morate kreirati vozacku dozvolu.</p>
                </div>
            </div>
        );
    }

    return (
        <div className="dodaj-kategoriju-card">
            <div className="dodaj-kategoriju-header">
                <h3>Zahtev za dodavanje kategorije</h3>
                <p>Pošaljite zahtev za dodavanje nove kategorije na vašu postojeću vozacku dozvolu</p>
            </div>

            <div className="current-categories">
                <h4>Trenutne kategorije:</h4>
                <div className="categories-list">
                    {vozackaDozvola.kategorije?.map((kategorija, index) => (
                        <span key={index} className="category-badge">
                            {kategorija}
                        </span>
                    ))}
                </div>
            </div>
            
            <form onSubmit={handleSubmit} className="dodaj-kategoriju-form">
                <div className="form-group">
                    <label htmlFor="kategorija">Nova kategorija:</label>
                    <select
                        id="kategorija"
                        name="kategorija"
                        value={formData.kategorija}
                        onChange={handleInputChange}
                        required
                    >
                        <option value="">Odaberite kategoriju</option>
                        {!vozackaDozvola.kategorije?.includes('A') && (
                            <option value="A">A - Motor (16+ godina)</option>
                        )}
                        {!vozackaDozvola.kategorije?.includes('B') && (
                            <option value="B">B - Automobil (18+ godina)</option>
                        )}
                        {!vozackaDozvola.kategorije?.includes('C') && (
                            <option value="C">C - Kamion (21+ godina)</option>
                        )}
                        {!vozackaDozvola.kategorije?.includes('D') && (
                            <option value="D">D - Autobus (21+ godina)</option>
                        )}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="razlog">Razlog:</label>
                    <textarea
                        id="razlog"
                        name="razlog"
                        value={formData.razlog}
                        onChange={handleInputChange}
                        placeholder="Opišite razlog za dodavanje kategorije..."
                        rows="3"
                        required
                    />
                </div>

                {error && <div className="error-message">{error}</div>}
                {message && <div className="success-message">{message}</div>}

                <button 
                    type="submit" 
                    className="submit-btn"
                    disabled={loading || !formData.kategorija || !formData.razlog}
                >
                    {loading ? 'Šalje se...' : 'Pošalji zahtev'}
                </button>
            </form>
        </div>
    );
};

export default DodajKategoriju;
