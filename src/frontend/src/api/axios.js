import axios from 'axios';

// Postavljanje osnovnog URL-a za sve API pozive
const instance = axios.create({
    baseURL: 'http://localhost:8080', // Osnovni URL za tvoju backend API rutu
    timeout: 1000,  // Postavljanje timeout-a
});

export default instance;
