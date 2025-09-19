import React, { useState, useEffect } from 'react';
import { authApi } from '../api/axios';
import { mupApi } from '../api/mupApi';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import './Profile.css';

const Profile = () => {
  const { user } = useAuth();
  const [profileData, setProfileData] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [documentStatuses, setDocumentStatuses] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user?.jmbg) fetchProfileData();
  }, [user]);

  const fetchProfileData = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await authApi.getUserWithDocuments(user.jmbg);
      const data = response.data;
      setProfileData(data);
      setDocuments(data.documents || []);
      
      // Validiraj dokumente
      await validateDocuments(data.documents || []);
    } catch (err) {
      setError('Greška pri dohvatanju podataka profila');
      console.error('Error fetching profile:', err);
    } finally {
      setLoading(false);
    }
  };

  const validateDocuments = async (docs) => {
  const statuses = {};

  for (const [index, doc] of (docs || []).entries()) {
    const docKey = doc.id || `doc_${index}`;

    try {
      if (doc.tipDokumenta === 'LICNA_KARTA') {
        // Novi endpoint vraća poruku u telu
        const res = await mupApi.validirajLicnuKartu(user.jmbg);

        // Pokušaj da izvučeš poruku bez obzira na oblik
        const raw =
          res?.data?.poruka ??
          res?.data?.message ??
          (typeof res?.data === 'string' ? res.data : '');

        // Ako nema poruke, tretiraj kao isteklo
        const message =
          raw && String(raw).trim().length > 0
            ? String(raw).trim()
            : 'Dokument je istekao';

        // Validnost izvodimo iz teksta poruke
        const isValid = /lična?\s+karta\s+je\s+validna/i.test(message) || /licna?\s+karta\s+je\s+validna/i.test(message);

        statuses[docKey] = { isValid, message };
      } else {
        // Ostali dokumenti: datum isteka
        const isExpired = new Date(doc.expiresAt) < new Date();
        statuses[docKey] = {
          isValid: !isExpired,
          message: isExpired ? 'Dokument je istekao' : 'Dokument je validan',
        };
      }
    } catch (err) {
      console.error('Error validating document:', err);
      // Fallback: ako validacija padne, koristi datum isteka
      const isExpired = new Date(doc.expiresAt) < new Date();
      statuses[docKey] = {
        isValid: !isExpired,
        message: isExpired ? 'Dokument je istekao' : 'Dokument je validan',
      };
    }
  }

  setDocumentStatuses(statuses);
};


  const formatDate = (dateString) => (dateString ? new Date(dateString).toLocaleDateString('sr-RS') : 'N/A');
  const getRoleDisplayName = (role) => (role === 'GRADJANIN' ? 'Građanin' : role === 'POLICAJAC' ? 'Policajac' : role);
  const getGenderDisplayName = (gender) => (gender === 'MUSKI' ? 'Muški' : gender === 'ZENSKI' ? 'Ženski' : gender);
  const getDocumentTypeDisplayName = (type) =>
    type === 'LICNA_KARTA' ? 'Lična karta' :
    type === 'PASOS' ? 'Pasoš' :
    type === 'SAOBRACAJNA_DOZVOLA' ? 'Saobraćajna dozvola' :
    type === 'VOZACKA_DOZVOLA' ? 'Vozačka dozvola' : type;

  return (
    <div className="home-container">
      <Navbar />
      <div className="home-content">
        {loading ? (
          <div className="profile-container">
            <div className="loading-container">
              <div className="loading-spinner"></div>
              <p>Učitavanje profila...</p>
            </div>
          </div>
        ) : error ? (
          <div className="profile-container">
            <div className="error-container">
              <h2>Greška</h2>
              <p>{error}</p>
              <button onClick={fetchProfileData} className="retry-btn">Pokušaj ponovo</button>
            </div>
          </div>
        ) : !profileData ? (
          <div className="profile-container">
            <div className="no-data">
              <h2>Nema podataka</h2>
              <p>Nije moguće učitati podatke profila</p>
            </div>
          </div>
        ) : (
          <div className="profile-container">
            <div className="profile-header">
              <div className="profile-avatar">
                <div className="avatar-circle">
                  {profileData.name?.charAt(0)}{profileData.lastname?.charAt(0)}
                </div>
              </div>
              <div className="profile-info">
                <h1>{profileData.name} {profileData.lastname}</h1>
                <p className="profile-role">{getRoleDisplayName(profileData.role)}</p>
                <p className="profile-username">@{profileData.username}</p>
              </div>
            </div>

            <div className="profile-content">
              <div className="profile-section">
                <h2>Lični podaci</h2>
                <div className="info-grid">
                  <div className="info-item"><label>JMBG:</label><span>{profileData.jmbg}</span></div>
                  <div className="info-item"><label>Datum rođenja:</label><span>{formatDate(profileData.birthday)}</span></div>
                  <div className="info-item"><label>Mesto rođenja:</label><span>{profileData.placeOfBirth || 'N/A'}</span></div>
                  <div className="info-item"><label>Pol:</label><span>{getGenderDisplayName(profileData.gender)}</span></div>
                </div>
              </div>

              {documents.length > 0 ? (
                <div className="profile-section">
                  <h2>Dokumenti</h2>
                  <div className="documents-grid">
                     {documents.map((doc, index) => {
                       const docKey = doc.id || `doc_${index}`;
                       const status = documentStatuses[docKey] || { 
                         isValid: false, 
                         message: 'Dokument je istekao' 
                       };
                       return (
                         <div key={index} className="document-card">
                           <div className="document-header">
                             <h3>{getDocumentTypeDisplayName(doc.tipDokumenta)}</h3>
                             <span className={`document-status ${status.isValid ? 'valid' : 'expired'}`}>
                               {status.isValid ? 'Validan' : 'Nije validan'}
                             </span>
                           </div>
                        <div className="document-details">
                          <div className="detail-row"><label>Broj dokumenta:</label><span>{doc.brojDokumenta || 'N/A'}</span></div>
                          <div className="detail-row"><label>Ime:</label><span>{doc.name}</span></div>
                          <div className="detail-row"><label>Prezime:</label><span>{doc.lastname}</span></div>
                          <div className="detail-row"><label>Datum kreiranja:</label><span>{formatDate(doc.createdAt)}</span></div>
                          <div className="detail-row"><label>Datum isteka:</label><span>{formatDate(doc.expiresAt)}</span></div>
                          {doc.kategorije && (
                            <div className="detail-row"><label>Kategorije:</label><span>{doc.kategorije}</span></div>
                          )}
                          <div className="detail-row"><label>Status:</label><span className={`status-text ${status.isValid ? 'valid' : 'expired'}`}>{status.message}</span></div>
                        </div>
                      </div>
                    );
                    })}
                  </div>
                </div>
              ) : (
                <div className="profile-section">
                  <h2>Dokumenti</h2>
                  <div className="no-documents"><p>Nemate registrovanih dokumenata</p></div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

  const getDocumentTypeDisplayName = (tipDokumenta) => {
    switch (tipDokumenta) {
      case 'LICNA_KARTA':
        return 'Lična karta';
      case 'PRODUZENJE_LICNE_KARTE':
        return 'Produženje lične karte';
      case 'PASOS':
        return 'Pasoš';
      case 'VOZACKA_DOZVOLA':
        return 'Vozacka dozvola';
      default:
        return tipDokumenta;
    }
  };

  const getRoleDisplayName = (role) => {
    switch (role) {
      case 'GRADJANIN':
        return 'Građanin';
      case 'POLICAJAC':
        return 'Policajac';
      default:
        return role;
    }
  };

  const getGenderDisplayName = (gender) => {
    switch (gender) {
      case 'MUSKI':
        return 'Muški';
      case 'ZENSKI':
        return 'Ženski';
      default:
        return gender;
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('sr-RS');
    } catch (error) {
      return 'N/A';
    }
  };

export default Profile;
