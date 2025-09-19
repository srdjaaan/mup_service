import axios from 'axios';

// MUP Service API instance
const mupInstance = axios.create({
    baseURL: 'http://localhost:8081',
    timeout: 10000,
});

// Interceptor za dodavanje tokena u sve zahteve
mupInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor za rukovanje odgovorima
mupInstance.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const mupApi = {
    // Kreiranje zahteva za ličnu kartu
    kreirajZahtev: (zahtevData) => {
        return mupInstance.post('/api/zahtevi/kreiraj', zahtevData);
    },

    // Dohvatanje mojih zahteva
    getMojiZahtevi: () => {
        return mupInstance.get('/api/zahtevi/moji-zahtevi');
    },

    // Dohvatanje zahteva na čekanju (za policajce)
    getZahteviNaCekanju: () => {
        return mupInstance.get('/api/zahtevi/na-cekanju');
    },

    // Odobravanje/odbijanje zahteva (za policajce)
    odobriZahtev: (zahtevId, odobravanjeData) => {
        return mupInstance.post(`/api/zahtevi/${zahtevId}/odobri`, odobravanjeData);
    },

    // Dohvatanje zahteva po ID
    getZahtevById: (zahtevId) => {
        return mupInstance.get(`/api/zahtevi/${zahtevId}`);
    },

    // Validacija lične karte
    validirajLicnuKartu: (jmbg) => {
        return mupInstance.get(`/api/zahtevi/korisnik/${jmbg}/validiraj-licnu-kartu`);
    },

    // Validacija pasoša
    validirajPasos: (jmbg) => {
        return mupInstance.get(`/api/zahtevi/korisnik/${jmbg}/validiraj-pasos`);
    },

    // Validacija starosti
    validirajStarost: (jmbg, tipDokumenta) => {
        return mupInstance.get(`/api/zahtevi/korisnik/${jmbg}/validiraj-starost/${tipDokumenta}`);
    },

    // Obaveštenja
    getObavestenja: (jmbg) => {
        return mupInstance.get(`/api/zahtevi/korisnik/${jmbg}/obavestenja`);
    },

    getNeprocitanaObavestenja: (jmbg) => {
        return mupInstance.get(`/api/zahtevi/korisnik/${jmbg}/obavestenja/neprocitana`);
    },

    getBrojNeprocitanihObavestenja: (jmbg) => {
        return mupInstance.get(`/api/zahtevi/korisnik/${jmbg}/obavestenja/broj-neprocitanih`);
    },

    oznaciKaoProcitanu: (notificationId) => {
        return mupInstance.post(`/api/zahtevi/obavestenja/${notificationId}/oznaci-procitanu`);
    },

    oznaciSveKaoProcitane: (jmbg) => {
        return mupInstance.post(`/api/zahtevi/korisnik/${jmbg}/obavestenja/oznaci-sve-procitane`);
    },

    // Dohvatanje dokumenata korisnika
    getDokumentiKorisnika: (jmbg) => {
        return mupInstance.get(`/api/zahtevi/korisnik/${jmbg}/dokumenti`);
    }
};

export default mupApi;
