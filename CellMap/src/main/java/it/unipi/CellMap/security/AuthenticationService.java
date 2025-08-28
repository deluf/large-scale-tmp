package it.unipi.CellMap.security;

import it.unipi.CellMap.database.user.UserRepository;
import it.unipi.CellMap.database.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
// When Spring security receives a request with basic authentication
//  (userId, password headers), it must be able to check if the password is correct
//  - this class tells Spring security how to do that
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Authentication error: user " + email + " not found"));
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Sprin security expects the roles prefixed by "ROLE_"
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

}
