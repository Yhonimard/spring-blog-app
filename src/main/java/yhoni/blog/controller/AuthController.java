package yhoni.blog.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import yhoni.blog.entity.Role;
import yhoni.blog.entity.User;
import yhoni.blog.repository.RoleRepository;
import yhoni.blog.repository.UserRepository;
import yhoni.blog.request.AuthRequest;
import yhoni.blog.response.AuthResponse;
import yhoni.blog.response.JwtApiAuthResponse;
import yhoni.blog.response.WebErrorResponse;
import yhoni.blog.response.WebResponse;
import yhoni.blog.security.JwtGenerator;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(description = "this api for registering", summary = "register account", responses = {
            @ApiResponse(responseCode = "201", description = "created"),
            @ApiResponse(responseCode = "409", description = "username conflict", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
    })
    @Tag(name = "Auth", description = "Auth API")
    public WebResponse<AuthResponse> register(@Valid @RequestBody AuthRequest request) {

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username already exist");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role roleUser = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(roleUser));

        User save = userRepository.save(user);

        return WebResponse.<AuthResponse>builder()
                .message("register success")
                .data(toAuthResponse(save))
                .build();
    }

    @PostMapping("/login")
    @Operation(description = "this api for login", summary = "login account", responses = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "username or password wrong", content = @Content(schema = @Schema(implementation = WebErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "something went wrong / an error occured", content = @Content(schema = @Schema(implementation = WebErrorResponse.class)))
    })
    public JwtApiAuthResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Optional<User> existingUser = userRepository.findById(request.getUsername());
        if (existingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cant find this user by that username");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = existingUser.get();
        String token = jwtGenerator.generateToken(authentication);

        return JwtApiAuthResponse.<AuthResponse>builder()
                .message("login success")
                .token(token)
                .data(toAuthResponse(user))
                .build();
    }

    private AuthResponse toAuthResponse(User user) {
        return modelMapper.map(user, AuthResponse.class);
    }

}
