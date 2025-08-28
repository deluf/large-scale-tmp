package it.unipi.CellMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // CRUD operations

    // [C] - Create a new user
    @PostMapping
    // ? Because we can return both User and APIErrorResponse responses
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                return new ResponseEntity<>(
                        new APIErrorResponse("Email already exists"),
                        HttpStatus.CONFLICT
                );
            }
            User savedUser = userRepository.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new APIErrorResponse("Something went wrong"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // [R] - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // [U] - Update (even partially) a user by ID
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateUser(@PathVariable("id") String id, @RequestBody User userUpdates) {
        Optional<User> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            User existingUser = userData.get();

            if (userUpdates.getEmail() != null) {
                // Check if the email is being changed to an existing one
                if (!existingUser.getEmail().equals(userUpdates.getEmail()) &&
                        userRepository.existsByEmail(userUpdates.getEmail())) {
                    return new ResponseEntity<>(
                            new APIErrorResponse("Email already exists"),
                            HttpStatus.CONFLICT
                    );
                }
                existingUser.setEmail(userUpdates.getEmail());
            }

            if (userUpdates.getPassword() != null) {
                existingUser.setPassword(userUpdates.getPassword());
            }
            if (userUpdates.getName() != null) {
                existingUser.setName(userUpdates.getName());
            }
            if (userUpdates.getRole() != null) {
                existingUser.setRole(userUpdates.getRole());
            }
            if (userUpdates.getFavoriteServers() != null) {
                existingUser.setFavoriteServers(userUpdates.getFavoriteServers());
            }

            return new ResponseEntity<>(userRepository.save(existingUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // [D] - Delete user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                new APIErrorResponse("Something went wrong"),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}