package it.unipi.CellMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    // The id can be null, in that case mongoDB will automatically generate one
    private String id;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String name;

    // Default user role set to regular
    private String role = "regular";
    
    @Field("favorite_servers")
    // Initialized to an empty list by default (useful, for example, when creating a new user)
    private List<Integer> favoriteServers = new ArrayList<>();
    
    public User() {}
    
    public User(String email, String password, String name, String role, List<Integer> favoriteServers) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.favoriteServers = favoriteServers;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public List<Integer> getFavoriteServers() {
        return favoriteServers;
    }
    
    public void setFavoriteServers(List<Integer> favoriteServers) {
        this.favoriteServers = favoriteServers;
    }

}
