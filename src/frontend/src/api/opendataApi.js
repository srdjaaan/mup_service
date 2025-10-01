import axios from "axios";

const opendataInstance = axios.create({
  baseURL: "http://localhost:8082/api/opendata",
  timeout: 10000,
});

opendataInstance.interceptors.request.use(
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

opendataInstance.interceptors.response.use(
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

export const opendataApi = {
    getAllUsers: () => opendataInstance.get('/users'),
    exportUsersExcel: () => opendataInstance.get('/users/excel', { responseType: 'blob' }),

    getUsersByPlace: () => opendataInstance.get('/users/place'),
    exportUsersByPlaceExcel: () => opendataInstance.get('/users/place/excel', { responseType: 'blob' }),

    getUsersByBirthday: () => opendataInstance.get('/users/birthday'),
    exportUsersByBirthdayExcel: () => opendataInstance.get('/users/birthday/excel', { responseType: 'blob' }),

    getUsersByGender: () => opendataInstance.get('/users/gender'),
    exportUsersByGenderExcel: () => opendataInstance.get('/users/gender/excel', { responseType: 'blob' }),

    getUsersByRole: () => opendataInstance.get('/users/role'),
    exportUsersByRoleExcel: () => opendataInstance.get('/users/role/excel', { responseType: 'blob' }),

    getUsersByGenderRole: () => opendataInstance.get('/users/gender/role'),
    exportUsersByGenderRoleExcel: () => opendataInstance.get('/users/gender/role/excel', { responseType: 'blob' }),

    getUsersByGenderPlace: () => opendataInstance.get('/users/gender/place'),
    exportUsersByGenderPlaceExcel: () => opendataInstance.get('/users/gender/place/excel', { responseType: 'blob' }),

    getUsersByGenderBirthday: () => opendataInstance.get('/users/gender/birthday'),
    exportUsersByGenderBirthdayExcel: () => opendataInstance.get('/users/gender/birthday/excel', { responseType: 'blob' }),

    getUsersByBirthdayRole: () => opendataInstance.get('/users/birthday/role'),
    exportUsersByBirthdayRoleExcel: () => opendataInstance.get('/users/birthday/role/excel', { responseType: 'blob' }),

    getUsersByBirthdayPlace: () => opendataInstance.get('/users/birthday/place'),
    exportUsersByBirthdayPlaceExcel: () => opendataInstance.get('/users/birthday/place/excel', { responseType: 'blob' }),

    getUsersByPlaceRole: () => opendataInstance.get('/users/place/role'),
    exportUsersByPlaceRoleExcel: () => opendataInstance.get('/users/place/role/excel', { responseType: 'blob' }),

    getUsersByGenderBirthdayRole: () => opendataInstance.get('/users/gender/birthday/role'),
    exportUsersByGenderBirthdayRoleExcel: () => opendataInstance.get('/users/gender/birthday/role/excel', { responseType: 'blob' }),

    getUsersByGenderBirthdayPlace: () => opendataInstance.get('/users/gender/birthday/place'),
    exportUsersByGenderBirthdayPlaceExcel: () => opendataInstance.get('/users/gender/birthday/place/excel', { responseType: 'blob' }),

    getUsersByGenderRolePlace: () => opendataInstance.get('/users/gender/role/place'),
    exportUsersByGenderRolePlaceExcel: () => opendataInstance.get('/users/gender/role/place/excel', { responseType: 'blob' }),

    getUsersByBirthdayRolePlace: () => opendataInstance.get('/users/birthday/role/place'),
    exportUsersByBirthdayRolePlaceExcel: () => opendataInstance.get('/users/birthday/role/place/excel', { responseType: 'blob' }),

    getAllDocuments: () => opendataInstance.get('/documents'),
    exportAllDocuments: () => opendataInstance.get('/documents/excel', { responseType: 'blob' }),
};

export default opendataApi;
