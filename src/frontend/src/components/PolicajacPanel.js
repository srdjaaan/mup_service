import React, { useState, useEffect } from 'react';
import { mupApi } from '../api/mupApi';
import './PolicajacPanel.css';

const PolicajacPanel = ({ user }) => {
    const [zahtevi, setZahtevi] = useState([]);
    const [loading, setLoading] = useState(false);
    const [selectedZahtev, setSelectedZahtev] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [odobravanjeData, setOdobravanjeData] = useState({
        status: 'ODOBREN',
        komentar: ''
    });
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        if (user?.role === 'POLICAJAC') {
            fetchZahteviNaCekanju();
        }
    }, [user]);

    const fetchZahteviNaCekanju = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await mupApi.getZahteviNaCekanju();
            setZahtevi(response.data);
        } catch (err) {
            setError('Greška pri dohvatanju zahteva');
            console.error('Error fetching zahtevi:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleOdobriZahtev = (zahtev) => {
        setSelectedZahtev(zahtev);
        setOdobravanjeData({
            status: 'ODOBREN',
            komentar: ''
        });
        setShowModal(true);
        setMessage('');
        setError('');
    };

    const handleOdbijZahtev = (zahtev) => {
        setSelectedZahtev(zahtev);
        setOdobravanjeData({
            status: 'ODBIJEN',
            komentar: ''
        });
        setShowModal(true);
        setMessage('');
        setError('');
    };

    const handleSubmitOdobravanje = async () => {
        if (!selectedZahtev) return;

        setSubmitting(true);
        setError('');
        setMessage('');

        try {
            await mupApi.odobriZahtev(selectedZahtev.id, odobravanjeData);
            setMessage(`Zahtev je uspešno ${odobravanjeData.status === 'ODOBREN' ? 'odobren' : 'odbijen'}!`);
            
            // Osveži listu zahteva
            await fetchZahteviNaCekanju();
            
            // Zatvori modal
            setShowModal(false);
            setSelectedZahtev(null);
        } catch (err) {
            setError(err.response?.data?.message || 'Greška pri obrađivanju zahteva');
        } finally {
            setSubmitting(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setOdobravanjeData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const closeModal = () => {
        setShowModal(false);
        setSelectedZahtev(null);
        setOdobravanjeData({
            status: 'ODOBREN',
            komentar: ''
        });
        setMessage('');
        setError('');
    };

    if (user?.role !== 'POLICAJAC') {
        return null;
    }

    return (
        <div className="policajac-panel">
            <div className="panel-header">
                <h2>Policajac Panel</h2>
                <p>Pregled i obrađivanje zahteva građana</p>
                <button 
                    onClick={fetchZahteviNaCekanju} 
                    className="refresh-btn"
                    disabled={loading}
                >
                    {loading ? 'Učitava...' : 'Osveži'}
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}
            {message && <div className="success-message">{message}</div>}

            {loading ? (
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Učitavanje zahteva...</p>
                </div>
            ) : zahtevi.length > 0 ? (
                <div className="zahtevi-grid">
                    {zahtevi.map(zahtev => (
                        <div key={zahtev.id} className="zahtev-card">
                            <div className="zahtev-header">
                                <h3>Zahtev #{zahtev.id}</h3>
                                <span className="status status-na_čekanju">
                                    {zahtev.status}
                                </span>
                            </div>
                            
                            <div className="zahtev-details">
                                <div className="detail-row">
                                    <strong>Građanin:</strong>
                                    <span>{zahtev.gradjaninIme} {zahtev.gradjaninPrezime}</span>
                                </div>
                                <div className="detail-row">
                                    <strong>JMBG:</strong>
                                    <span>{zahtev.gradjaninJmbg}</span>
                                </div>
                                <div className="detail-row">
                                    <strong>Tip dokumenta:</strong>
                                    <span className="document-type">
                                        {zahtev.tipDokumenta === 'LICNA_KARTA' && 'Lična karta'}
                                        {zahtev.tipDokumenta === 'PRODUZENJE_LICNE_KARTE' && 'Produženje lične karte'}
                                        {zahtev.tipDokumenta === 'PASOS' && 'Pasoš'}
                                        {!['LICNA_KARTA', 'PRODUZENJE_LICNE_KARTE', 'PASOS'].includes(zahtev.tipDokumenta) && zahtev.tipDokumenta}
                                    </span>
                                </div>
                                <div className="detail-row">
                                    <strong>Razlog:</strong>
                                    <span>{zahtev.razlog}</span>
                                </div>
                                <div className="detail-row">
                                    <strong>Datum kreiranja:</strong>
                                    <span>{new Date(zahtev.datumKreiranja).toLocaleDateString('sr-RS')}</span>
                                </div>
                            </div>

                            <div className="zahtev-actions">
                                <button 
                                    onClick={() => handleOdobriZahtev(zahtev)}
                                    className="approve-btn"
                                >
                                    Odobri
                                </button>
                                <button 
                                    onClick={() => handleOdbijZahtev(zahtev)}
                                    className="reject-btn"
                                >
                                    Odbij
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="no-zahtevi">
                    <p>Nema zahteva na čekanju</p>
                </div>
            )}

            {/* Modal za odobravanje/odbijanje */}
            {showModal && selectedZahtev && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h3>
                                {odobravanjeData.status === 'ODOBREN' ? 'Odobri zahtev' : 'Odbij zahtev'}
                            </h3>
                            <button onClick={closeModal} className="close-btn">&times;</button>
                        </div>
                        
                        <div className="modal-body">
                            <div className="zahtev-summary">
                                <h4>Detalji zahteva:</h4>
                                <p><strong>Građanin:</strong> {selectedZahtev.gradjaninIme} {selectedZahtev.gradjaninPrezime}</p>
                                <p><strong>Tip dokumenta:</strong> {
                                    selectedZahtev.tipDokumenta === 'LICNA_KARTA' && 'Lična karta'
                                }{selectedZahtev.tipDokumenta === 'PRODUZENJE_LICNE_KARTE' && 'Produženje lične karte'}
                                {selectedZahtev.tipDokumenta === 'PASOS' && 'Pasoš'}
                                {!['LICNA_KARTA', 'PRODUZENJE_LICNE_KARTE', 'PASOS'].includes(selectedZahtev.tipDokumenta) && selectedZahtev.tipDokumenta}</p>
                                <p><strong>Razlog:</strong> {selectedZahtev.razlog}</p>
                            </div>

                            <div className="form-group">
                                <label htmlFor="komentar">Komentar:</label>
                                <textarea
                                    id="komentar"
                                    name="komentar"
                                    value={odobravanjeData.komentar}
                                    onChange={handleInputChange}
                                    placeholder="Unesite komentar..."
                                    rows="4"
                                    required
                                />
                            </div>

                            {error && <div className="error-message">{error}</div>}
                        </div>

                        <div className="modal-footer">
                            <button 
                                onClick={closeModal} 
                                className="cancel-btn"
                                disabled={submitting}
                            >
                                Otkaži
                            </button>
                            <button 
                                onClick={handleSubmitOdobravanje}
                                className={`submit-btn ${odobravanjeData.status === 'ODOBREN' ? 'approve' : 'reject'}`}
                                disabled={submitting}
                            >
                                {submitting ? 'Obrađuje...' : 
                                 odobravanjeData.status === 'ODOBREN' ? 'Odobri' : 'Odbij'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PolicajacPanel;
