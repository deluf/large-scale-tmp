package it.unipi.CellMap.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.CellMap.database.server.Server;
import it.unipi.CellMap.database.server.ServerService;
import it.unipi.CellMap.database.user.User;
import it.unipi.CellMap.database.user.UserRepository;
import it.unipi.CellMap.database.user.UserRole;
import it.unipi.CellMap.dtos.CreateUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import it.unipi.CellMap.GlobalExceptionHandler.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management")
public class UserController {

    /** FIXME:
     * speedtests potrebbe essere implementato come array negli user piuttosto
     * last_updated_by potrebbe esserci l'username pure
     */

    public static final int FAVORITE_SERVERS_MAX = 10;

    private final ServerService serverService;
    public UserController(ServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create a new user
    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<Void> createUser(
            @Valid @RequestBody CreateUserDTO createUserDTO
    ) {
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setEmail(createUserDTO.getEmail());
        user.setName(createUserDTO.getName());
        user.setRole(UserRole.REGULAR);
        user.setFavoriteServers(List.of());
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Read the favorite servers of the authenticated user
    @GetMapping("me/favoriteServers")
    @Operation(summary = "Get authenticated user's favorite servers")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite servers retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content /* Empty */ )
    })
    public ResponseEntity<List<Server>> getFavoriteServers(
            Authentication auth
    ) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(() ->
                new RuntimeException("Authenticated user " + auth.getName() + " not found in the database"));

        List<String> favoriteServers = user.getFavoriteServers();
        if (favoriteServers.isEmpty()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }

        List<Server> servers = serverService.getServersByIds(favoriteServers);
        return new ResponseEntity<>(servers, HttpStatus.OK);
    }

    // Add a favorite server to the authenticated user
    @PostMapping("me/favoriteServers/{server}")
    @Operation(summary = "Add a favorite server to the authenticated user")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Server added to favorites successfully"),
            @ApiResponse(responseCode = "204", description = "Server already in favorites"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Server not found"),
            @ApiResponse(responseCode = "409", description = "Maximum number of favorite servers reached")
    })
    public ResponseEntity<Void> addFavoriteServer(
            Authentication auth,
            @PathVariable("server") String server
    ) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(()
                -> new RuntimeException("Authenticated user not found in the database"));

        if (user.getFavoriteServers().contains(server)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if (user.getFavoriteServers().size() >= FAVORITE_SERVERS_MAX) {
            throw new ConflictException(
                    "You can't add more than " + FAVORITE_SERVERS_MAX + " favorite servers");
        }

        if (!serverService.existsById(server)) {
            throw new NotFoundException("Server not found");
        }

        user.getFavoriteServers().add(server);
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Remove a favorite server from the authenticated user
    @DeleteMapping("me/favoriteServers/{server}")
    @Operation(summary = "Remove a favorite server from the authenticated user")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Server removed from favorites successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
    })
    public ResponseEntity<Void> removeFavoriteServer(
            Authentication auth,
            @PathVariable("server") String server
    ) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow(()
                -> new RuntimeException("Authenticated user not found in the database"));

        if (user.getFavoriteServers().contains(server)) {
            user.getFavoriteServers().remove(server);
            userRepository.save(user);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Delete the profile of the authenticated user
    @DeleteMapping("/me")
    @Operation(summary = "Delete the authenticated user's profile")
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User profile deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
    })
    public ResponseEntity<Void> deleteUser(
            Authentication auth
    ) {
        if (!userRepository.existsByEmail(auth.getName())) {
            throw new RuntimeException("Authenticated user not found in the database");
        }
        userRepository.deleteByEmail(auth.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
