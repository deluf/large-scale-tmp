package it.unipi.CellMap.database.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String password;
    private String name;
    private UserRole role;
    private List<String> favorite_servers;
    
    public User() {}
    public User(String email, String password, String name, UserRole role, List<String> favoriteServers) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.favorite_servers = favoriteServers;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }
    public List<String> getFavoriteServers() { return favorite_servers; }
    public void setFavoriteServers(List<String> favoriteServers) {
        this.favorite_servers = favoriteServers;
    }
}
