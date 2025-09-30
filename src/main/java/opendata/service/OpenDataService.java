package opendata.service;

import opendata.contract.*;
import opendata.model.Document;
import opendata.model.User;
import opendata.util.ExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
public class OpenDataService {

    private final RestTemplate restTemplate;

    @Value("${services.mup-url}")
    private String mupServiceUrl;

    @Value("${services.auth-url}")
    private String authServiceUrl;

    private final String username = "opendata";

    private final String password = "opendata123";


    @Autowired
    public OpenDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<UserDTO> getAllUsers() {
        return fetchAllUsers().stream()
                .map(user -> new UserDTO(
                        user.getName(),
                        user.getLastname(),
                        user.getBirthday(),
                        user.getPlaceOfBirth(),
                        user.getRole(),
                        user.getGender()
                ))
                .toList();
    }

    public byte[] exportUsersToExcel() {
        List<UserDTO> users = getAllUsers();

        String[] headers = {"Name", "Last Name", "Birthday",  "Place of Birth", "Role", "Gender"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getBirthday().toString(),
                        user.getPlaceOfBirth(),
                        user.getRole().toString(),
                        user.getGender().toString()
                )
        );
    }

    public List<UserByBirthday> getAllUsersByBirthday() {
        return fetchAllUsers().stream()
                .map(user -> new UserByBirthday(
                        user.getName(),
                        user.getLastname(),
                        user.getBirthday()
                ))
                .toList();

    }

    public byte[] exportUsersByBirthdayToExcel() {
        List<UserByBirthday> users = getAllUsersByBirthday();

        String[] headers = {"Name", "Last Name", "Birthday"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getBirthday().toString()
                )
        );
    }

    public List<UserByGender> getAllUsersByGender() {
        return fetchAllUsers().stream()
                .map(user -> new UserByGender(
                        user.getName(),
                        user.getLastname(),
                        user.getGender()
                ))
                .toList();

    }

    public byte[] exportUsersByGenderToExcel() {
        List<UserByGender> users = getAllUsersByGender();

        String[] headers = {"Name", "Last Name", "Gender"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getGender().toString()
                )
        );
    }

    public List<UserByPlace> getAllUsersByPlace() {
        return fetchAllUsers().stream()
                .map(user -> new UserByPlace(
                        user.getName(),
                        user.getLastname(),
                        user.getPlaceOfBirth()
                ))
                .toList();

    }

    public byte[] exportUsersByPlaceToExcel() {
        List<UserByPlace> users = getAllUsersByPlace();

        String[] headers = {"Name", "Last Name", "Place of Birth"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getPlaceOfBirth()
                )
        );
    }

    public List<UserByRole> getAllUsersByRole() {
        return fetchAllUsers().stream()
                .map(user -> new UserByRole(
                        user.getName(),
                        user.getLastname(),
                        user.getRole()
                ))
                .toList();
    }

    public byte[] exportUsersByRoleToExcel() {
        List<UserByRole> users = getAllUsersByRole();

        String[] headers = {"Name", "Last Name", "Role"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getRole().toString()
                )
        );
    }


    public List<UserByGenderRole> getAllUsersByGenderRole() {
        return fetchAllUsers().stream()
                .map(user -> new UserByGenderRole(
                        user.getName(),
                        user.getLastname(),
                        user.getGender(),
                        user.getRole()
                ))
                .toList();
    }

    public byte[] exportUsersByGenderRoleToExcel() {
        List<UserByGenderRole> users = getAllUsersByGenderRole();

        String[] headers = {"Name", "Last Name","Gender", "Role"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getGender().toString(),
                        user.getRole().toString()
                )
        );
    }

    public List<UserByGenderPlace> getAllUsersByGenderPlace() {
        return fetchAllUsers().stream()
                .map(user -> new UserByGenderPlace(
                        user.getName(),
                        user.getLastname(),
                        user.getGender(),
                        user.getPlaceOfBirth()
                ))
                .toList();
    }

    public byte[] exportUsersByGenderPlaceToExcel() {
        List<UserByGenderPlace> users = getAllUsersByGenderPlace();

        String[] headers = {"Name", "Last Name","Gender", "Place of Birth"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getGender().toString(),
                        user.getPlaceOfBirth()
                )
        );
    }

    public List<UserByGenderBirthday> getAllUsersByGenderBirthday() {
        return fetchAllUsers().stream()
                .map(user -> new UserByGenderBirthday(
                        user.getName(),
                        user.getLastname(),
                        user.getGender(),
                        user.getBirthday()
                ))
                .toList();
    }

    public byte[] exportUsersByGenderBirthdayToExcel() {
        List<UserByGenderBirthday> users = getAllUsersByGenderBirthday();

        String[] headers = {"Name", "Last Name","Gender", "Birthday"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getGender().toString(),
                        user.getBirthday().toString()
                )
        );
    }

    public List<UserByBirthdayRole> getAllUsersByBirthdayRole() {
        return fetchAllUsers().stream()
                .map(user -> new UserByBirthdayRole(
                        user.getName(),
                        user.getLastname(),
                        user.getRole(),
                        user.getBirthday()
                ))
                .toList();
    }

    public byte[] exportUsersByBirthdayRoleToExcel() {
        List<UserByBirthdayRole> users = getAllUsersByBirthdayRole();

        String[] headers = {"Name", "Last Name", "Role", "Birthday"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getRole().toString(),
                        user.getBirthday().toString()
                )
        );
    }

    public List<UserByBirthdayPlace> getAllUsersByBirthdayPlace() {
        return fetchAllUsers().stream()
                .map(user -> new UserByBirthdayPlace(
                        user.getName(),
                        user.getLastname(),
                        user.getPlaceOfBirth(),
                        user.getBirthday()
                ))
                .toList();
    }

    public byte[] exportUsersByBirthdayPlaceToExcel() {
        List<UserByBirthdayPlace> users = getAllUsersByBirthdayPlace();

        String[] headers = {"Name", "Last Name", "Place of Birth", "Birthday"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getPlaceOfBirth(),
                        user.getBirthday().toString()
                )
        );
    }

    public List<UserByPlaceRole> getAllUsersByPlaceRole() {
        return fetchAllUsers().stream()
                .map(user -> new UserByPlaceRole(
                        user.getName(),
                        user.getLastname(),
                        user.getPlaceOfBirth(),
                        user.getRole()
                ))
                .toList();
    }

    public byte[] exportUsersByPlaceRoleToExcel() {
        List<UserByPlaceRole> users = getAllUsersByPlaceRole();

        String[] headers = {"Name", "Last Name", "Place of Birth", "Role"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getPlaceOfBirth(),
                        user.getRole().toString()
                )
        );
    }

    public List<UserByGenderBirthdayRole> getAllUsersByGenderBirthdayRole() {
        return fetchAllUsers().stream()
                .map(user -> new UserByGenderBirthdayRole(
                        user.getName(),
                        user.getLastname(),
                        user.getGender(),
                        user.getBirthday(),
                        user.getRole()
                ))
                .toList();
    }

    public byte[] exportUsersByGenderBirthdayRoleToExcel() {
        List<UserByGenderBirthdayRole> users = getAllUsersByGenderBirthdayRole();

        String[] headers = {"Name", "Last Name", "Gender","Birthday", "Role"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getGender().toString(),
                        user.getBirthday().toString(),
                        user.getRole().toString()
                )
        );
    }

    public List<UserByGenderBirthdayPlace> getAllUsersByGenderBirthdayPlace() {
        return fetchAllUsers().stream()
                .map(user -> new UserByGenderBirthdayPlace(
                        user.getName(),
                        user.getLastname(),
                        user.getGender(),
                        user.getBirthday(),
                        user.getPlaceOfBirth()
                ))
                .toList();
    }

    public byte[] exportUsersByGenderBirthdayPlaceToExcel() {
        List<UserByGenderBirthdayPlace> users = getAllUsersByGenderBirthdayPlace();

        String[] headers = {"Name", "Last Name", "Gender","Birthday", "Place of Birth"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getGender().toString(),
                        user.getBirthday().toString(),
                        user.getPlaceOfBirth()
                )
        );
    }

    public List<UserByGenderRolePlace> getAllUsersByGenderRolePlace() {
        return fetchAllUsers().stream()
                .map(user -> new UserByGenderRolePlace(
                        user.getName(),
                        user.getLastname(),
                        user.getGender(),
                        user.getRole(),
                        user.getPlaceOfBirth()
                ))
                .toList();
    }

    public byte[] exportUsersByGenderRolePlaceToExcel() {
        List<UserByGenderRolePlace> users = getAllUsersByGenderRolePlace();

        String[] headers = {"Name", "Last Name", "Gender","Role", "Place of Birth"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getGender().toString(),
                        user.getRole().toString(),
                        user.getPlaceOfBirth()
                )
        );
    }

    public List<UserByBirthdayRolePlace> getAllUsersByBirthdayRolePlace() {
        return fetchAllUsers().stream()
                .map(user -> new UserByBirthdayRolePlace(
                        user.getName(),
                        user.getLastname(),
                        user.getBirthday(),
                        user.getRole(),
                        user.getPlaceOfBirth()
                ))
                .toList();
    }

    public byte[] exportUsersByBirthdayRolePlaceToExcel() {
        List<UserByBirthdayRolePlace> users = getAllUsersByBirthdayRolePlace();

        String[] headers = {"Name", "Last Name", "Birthday", "Role", "Place of Birth"};

        return ExcelExporter.exportToExcel(
                "Users",
                headers,
                users,
                user -> List.of(
                        user.getName(),
                        user.getLastname(),
                        user.getBirthday().toString(),
                        user.getRole().toString(),
                        user.getPlaceOfBirth()
                )
        );
    }


    public List<DocumentDTO> getAllDocuments() {
        return fetchAllDocuments().stream()
                .map(document -> new DocumentDTO(
                        document.getName(),
                        document.getLastname(),
                        document.getBirthday(),
                        document.getPlaceOfBirth(),
                        document.getCreatedAt(),
                        document.getExpiresAt(),
                        document.getTipDokumenta(),
                        document.getKategorije()
                ))
                .toList();
    }

    private List<User> fetchAllUsers() {
        String url = authServiceUrl + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    User[].class
            );
            User[] users = response.getBody();
            return (users != null) ? Arrays.asList(users) : List.of();

        } catch (Exception e) {
            System.err.println("Greška prilikom dohvatanja korisnika: " + e.getMessage());
            throw new RuntimeException("Neuspešno dohvaćeni korisnici", e);
        }
    }

    private List<Document> fetchAllDocuments() {
        String url = mupServiceUrl + "/document/all";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Document[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Document[].class
            );
            Document[] documents = response.getBody();
            return (documents != null) ? Arrays.asList(documents) : List.of();

        } catch (Exception e) {
            System.err.println("Greška prilikom dohvatanja dokumenta: " + e.getMessage());
            throw new RuntimeException("Neuspešno dohvaćeni dokumenti", e);
        }
    }

}
