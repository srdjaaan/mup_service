import axios from 'axios';

// Postavljanje osnovnog URL-a za sve API pozive
const instance = axios.create({
    baseURL: 'http://localhost:8080', // Osnovni URL za tvoju backend API rutu
    timeout: 10000,  // Postavljanje timeout-a
});

// Interceptor za dodavanje tokena u sve zahteve
instance.interceptors.request.use(
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
instance.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Token je istekao ili nije valjan
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// Auth Service API funkcije
export const authApi = {
    // Dohvatanje korisnika po JMBG
    getUserByJmbg: (jmbg) => {
        return instance.get(`/api/auth/user/${jmbg}`);
    },

    // Dohvatanje korisnika sa dokumentima
    getUserWithDocuments: (jmbg) => {
        return instance.get(`/api/auth/user/${jmbg}/documents`);
    },

    // Dohvatanje svih korisnika (za policajce)
    getAllUsers: () => {
        return instance.get('/api/auth/users');
    }
};

export default instance;
