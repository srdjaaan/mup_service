package opendata.controller;

import opendata.contract.*;
import opendata.service.OpenDataService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(
        origins = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*"
)
@RequestMapping("/api/opendata")
public class OpenDataController {

    private final OpenDataService openDataService;

    public OpenDataController(OpenDataService openDataService) {
        this.openDataService = openDataService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(openDataService.getAllUsers());
    }

    @GetMapping("/users/excel")
    public ResponseEntity<byte[]> exportUsersExcel() {
        byte[] excelFile = openDataService.exportUsersToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/place")
    public ResponseEntity<List<UserByPlace>> getAllUsersByPlace() {
        return ResponseEntity.ok(openDataService.getAllUsersByPlace());
    }

    @GetMapping("/users/place/excel")
    public ResponseEntity<byte[]> exportUsersByPlaceExcel() {
        byte[] excelFile = openDataService.exportUsersByPlaceToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/birthday")
    public ResponseEntity<List<UserByBirthday>> getAllUsersByBirthday() {
        return ResponseEntity.ok(openDataService.getAllUsersByBirthday());
    }

    @GetMapping("/users/birthday/excel")
    public ResponseEntity<byte[]> exportUsersByBirthdayExcel() {
        byte[] excelFile = openDataService.exportUsersByBirthdayToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/gender")
    public ResponseEntity<List<UserByGender>> getAllUsersByGender() {
        return ResponseEntity.ok(openDataService.getAllUsersByGender());
    }

    @GetMapping("/users/gender/excel")
    public ResponseEntity<byte[]> exportUsersByGenderExcel() {
        byte[] excelFile = openDataService.exportUsersByGenderToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/role")
    public ResponseEntity<List<UserByRole>> getAllUsersByRole() {
        return ResponseEntity.ok(openDataService.getAllUsersByRole());
    }

    @GetMapping("/users/role/excel")
    public ResponseEntity<byte[]> exportUsersByRoleExcel() {
        byte[] excelFile = openDataService.exportUsersByRoleToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/gender/role")
    public ResponseEntity<List<UserByGenderRole>> getAllUsersByGenderRole() {
        return ResponseEntity.ok(openDataService.getAllUsersByGenderRole());
    }

    @GetMapping("/users/gender/role/excel")
    public ResponseEntity<byte[]> exportUsersByGenderRoleExcel() {
        byte[] excelFile = openDataService.exportUsersByGenderRoleToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/gender/place")
    public ResponseEntity<List<UserByGenderPlace>> getAllUsersByGenderPlace() {
        return ResponseEntity.ok(openDataService.getAllUsersByGenderPlace());
    }

    @GetMapping("/users/gender/place/excel")
    public ResponseEntity<byte[]> exportUsersByGenderPlaceExcel() {
        byte[] excelFile = openDataService.exportUsersByGenderPlaceToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/gender/birthday")
    public ResponseEntity<List<UserByGenderBirthday>> getAllUsersByGenderBirthday() {
        return ResponseEntity.ok(openDataService.getAllUsersByGenderBirthday());
    }

    @GetMapping("/users/gender/birthday/excel")
    public ResponseEntity<byte[]> exportUsersByGenderBirthdayExcel() {
        byte[] excelFile = openDataService.exportUsersByGenderBirthdayToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/birthday/role")
    public ResponseEntity<List<UserByBirthdayRole>> getAllUsersByBirthdayRole() {
        return ResponseEntity.ok(openDataService.getAllUsersByBirthdayRole());
    }

    @GetMapping("/users/birthday/role/excel")
    public ResponseEntity<byte[]> exportUsersByBirthdayRoleExcel() {
        byte[] excelFile = openDataService.exportUsersByBirthdayRoleToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/birthday/place")
    public ResponseEntity<List<UserByBirthdayPlace>> getAllUsersByBirthdayPlace() {
        return ResponseEntity.ok(openDataService.getAllUsersByBirthdayPlace());
    }

    @GetMapping("/users/birthday/place/excel")
    public ResponseEntity<byte[]> exportUsersByBirthdayPlaceExcel() {
        byte[] excelFile = openDataService.exportUsersByBirthdayPlaceToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/place/role")
    public ResponseEntity<List<UserByPlaceRole>> getAllUsersByPlaceRole() {
        return ResponseEntity.ok(openDataService.getAllUsersByPlaceRole());
    }

    @GetMapping("/users//place/role/excel")
    public ResponseEntity<byte[]> exportUsersByPlaceRoleExcel() {
        byte[] excelFile = openDataService.exportUsersByPlaceRoleToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/gender/birthday/role")
    public ResponseEntity<List<UserByGenderBirthdayRole>> getAllUsersByGenderBirthdayRole() {
        return ResponseEntity.ok(openDataService.getAllUsersByGenderBirthdayRole());
    }

    @GetMapping("/users/gender/birthday/role/excel")
    public ResponseEntity<byte[]> exportUsersByGenderBirthdayRoleExcel() {
        byte[] excelFile = openDataService.exportUsersByGenderBirthdayRoleToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/gender/birthday/place")
    public ResponseEntity<List<UserByGenderBirthdayPlace>> getAllUsersByGenderBirthdayPlace() {
        return ResponseEntity.ok(openDataService.getAllUsersByGenderBirthdayPlace());
    }

    @GetMapping("/users/gender/birthday/place/excel")
    public ResponseEntity<byte[]> exportUsersByGenderBirthdayPlaceExcel() {
        byte[] excelFile = openDataService.exportUsersByGenderBirthdayPlaceToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/gender/role/place")
    public ResponseEntity<List<UserByGenderRolePlace>> getAllUsersByGenderRolePlace() {
        return ResponseEntity.ok(openDataService.getAllUsersByGenderRolePlace());
    }

    @GetMapping("/users/gender/role/place/excel")
    public ResponseEntity<byte[]> exportUsersByGenderRolePlaceExcel() {
        byte[] excelFile = openDataService.exportUsersByGenderRolePlaceToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/users/birthday/role/place")
    public ResponseEntity<List<UserByBirthdayRolePlace>> getAllUsersByBirthdayRolePlace() {
        return ResponseEntity.ok(openDataService.getAllUsersByBirthdayRolePlace());
    }

    @GetMapping("/users/birthday/role/place/excel")
    public ResponseEntity<byte[]> exportUsersByBirthdayRolePlaceExcel() {
        byte[] excelFile = openDataService.exportUsersByBirthdayRolePlaceToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(excelFile);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        return ResponseEntity.ok(openDataService.getAllDocuments());
    }
}
