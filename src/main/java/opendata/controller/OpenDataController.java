package opendata.controller;

import opendata.contract.UserDTO;
import opendata.service.OpenDataService;
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
}
