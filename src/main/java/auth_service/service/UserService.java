package auth_service.service;

import auth_service.DTO.UserDTO;
import auth_service.model.User;
import auth_service.repository.UserRepository;
import auth_service.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User registerUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> { throw new RuntimeException("Username već postoji!"); });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Pogrešna lozinka");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserByJmbg(String jmbg) {
        return userRepository.findById(jmbg)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Korisnik sa ovim ID ne postoji"));
    }

    // Privatna metoda za mapiranje User -> UserDTO
    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getJmbg(),
                user.getName(),
                user.getLastname(),
                user.getUsername(),
                user.getBirthday(),
                user.getPlaceOfBirth(),
                user.getRole(),
                user.getGender()
        );
    }
}
