package it.unipi.CellMap.dtos;

import jakarta.validation.constraints.*;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class CreateUserDTO {
    @Email (message = "Invalid email")
    // The @Email tag also checks that the email isn't null, blank or too long
    private String email;

    @NotBlank (message = "The password must not be blank")
    @Size(max = 32, message = "The password must not exceed 32 characters")
    private String password;

    @NotBlank (message = "The name must not be blank")
    @Size(max = 24, message = "The name must not exceed 24 characters")
    private String name;

    public CreateUserDTO() {}
    public CreateUserDTO(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
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
}
