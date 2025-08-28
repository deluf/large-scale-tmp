package it.unipi.CellMap;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import it.unipi.CellMap.database.user.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static it.unipi.CellMap.controllers.UserController.FAVORITE_SERVERS_MAX;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTests {

    private static final String TEST_EMAIL = "test@cellmap.it";
    private static final String TEST_PASSWORD = "test";
    private static final String TEST_NAME = "test";

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    
    // POST /users //

    @Test
    @Order(1)
    void createUser_success() {
        // Avoids unwanted email conflicts
        userRepository.findByEmail(TEST_EMAIL).ifPresent(userRepository::delete);

        String request = String.format("""
        {
            "email": "%s",
            "password": "%s",
            "name": "%s"
        }
        """, TEST_EMAIL, TEST_PASSWORD, TEST_NAME);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body(emptyOrNullString());
    }

    @Test
    @Order(2)
    void createUser_invalidJSON() {
        String request = """
        {
            "email": "...", [
            # "password": "...",
            "name": "..." -
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("type", equalTo("HttpMessageNotReadableException"))
            .body("title", equalTo("Request parse error"))
            .body("status", equalTo(400))
            .body("detail", equalTo("The request body is not a valid JSON"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(2)
    // Try to add random fields (not present in the DTO)
    void createUser_randomFields() {
        String request = """
        {
            "email": "...",
            "password": "...",
            "name": "...",
            "role": "ADMIN"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("type", equalTo("HttpMessageNotReadableException"))
            .body("title", equalTo("Request parse error"))
            .body("status", equalTo(400))
            .body("detail", equalTo("Unrecognized field 'role'. Known fields are: [password, email, name]"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(2)
    void createUser_invalidEmail() {
        String request = """
        {
            "email": "invalidEmail#test.com",
            "password": "...",
            "name": "..."
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("type", equalTo("MethodArgumentNotValidException"))
            .body("title", equalTo("Request validation error"))
            .body("status", equalTo(400))
            .body("detail", equalTo("Invalid email"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(2)
    void createUser_blankPassword() {
        String request = """
        {
            "email": "blankPassword@test.com",
            "password": "",
            "name": "..."
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("type", equalTo("MethodArgumentNotValidException"))
            .body("title", equalTo("Request validation error"))
            .body("status", equalTo(400))
            .body("detail", equalTo("The password must not be blank"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(2)
    void createUser_longPassword() {
        String request = """
        {
            "email": "longPassword@test.com",
            "password": "---------------------------------",
            "name": "..."
        }
        """; // 33 characters

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("type", equalTo("MethodArgumentNotValidException"))
            .body("title", equalTo("Request validation error"))
            .body("status", equalTo(400))
            .body("detail", equalTo("The password must not exceed 32 characters"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(2)
    void createUser_blankName() {
        String request = """
        {
            "email": "blankName@test.com",
            "password": "...",
            "name": ""
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("type", equalTo("MethodArgumentNotValidException"))
            .body("title", equalTo("Request validation error"))
            .body("status", equalTo(400))
            .body("detail", equalTo("The name must not be blank"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(2)
    void createUser_longName() {
        String request = """
        {
            "email": "longName@test.com",
            "password": "...",
            "name": "-------------------------"
        }
        """; // 25 characters

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("type", equalTo("MethodArgumentNotValidException"))
            .body("title", equalTo("Request validation error"))
            .body("status", equalTo(400))
            .body("detail", equalTo("The name must not exceed 24 characters"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(2)
    void createUser_emailAlreadyExists() {
        String request = String.format("""
        {
            "email": "%s",
            "password": "...",
            "name": "..."
        }
        """, TEST_EMAIL);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/users")
        .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("type", equalTo("ConflictException"))
            .body("title", equalTo("The request is in conflict with the current state of the data"))
            .body("status", equalTo(409))
            .body("detail", equalTo("Email already in use"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    // GET /users/me/favoriteServers //

    @Test
    @Order(2)
    void getFavoriteServers_success() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/users/me/favoriteServers")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo("[]"));
    }

    @Test
    @Order(2)
    void getFavoriteServers_unauthorized() {
        given()
            .auth().basic("...", "...")
        .when()
            .get("/users/me/favoriteServers")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body(emptyOrNullString());
    }

    // POST /users/me/favoriteServers/{server} //

    @Test
    @Order(3)
    void addFavoriteServer_success() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .post("/users/me/favoriteServers/1")
        .then()
            .statusCode(HttpStatus.CREATED.value())
            .body(emptyOrNullString());
    }

    @Test
    @Order(3)
    void addFavoriteServer_unauthorized() {
        given()
            .auth().basic("...", "...")
        .when()
            .post("/users/me/favoriteServers/1")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body(emptyOrNullString());
    }

    @Test
    @Order(3)
    void addFavoriteServer_serverNotFound() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .post("/users/me/favoriteServers/...")
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("type", equalTo("NotFoundException"))
            .body("title", equalTo("Resource not found"))
            .body("status", equalTo(404))
            .body("detail", equalTo("Server not found"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @Order(4)
    void addFavoriteServer_alreadyInFavorites() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .post("/users/me/favoriteServers/1")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .body(emptyOrNullString());
    }

    @Test
    @Order(5)
    void addFavoriteServer_maximumReached() {
        // Assuming FAVORITE_SERVERS_MAX is a reasonable number
        // Starting from i=2 since server 1 was already added before
        for (int i = 2; i <= FAVORITE_SERVERS_MAX; i++) {
            given()
                .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .when()
                .post("/users/me/favoriteServers/" + i)
            .then()
                .statusCode(equalTo(HttpStatus.CREATED.value()));
        }

        // Try to add one more server beyond the limit
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .post("/users/me/favoriteServers/11")
        .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("type", equalTo("ConflictException"))
            .body("title", equalTo("The request is in conflict with the current state of the data"))
            .body("status", equalTo(409))
            .body("detail", containsString("You can't add more than " + FAVORITE_SERVERS_MAX + " favorite servers"))
            .body("instance", notNullValue())
            .body("timestamp", notNullValue());
    }

    // DELETE /users/me/favoriteServers/{server} //

    @Test
    @Order(6)
    void deleteFavoriteServer_success() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .delete("/users/me/favoriteServers/1")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .body(emptyOrNullString());
    }

    @Test
    @Order(6)
    void deleteFavoriteServer_unauthorized() {
        given()
            .auth().basic("...", "...")
        .when()
            .delete("/users/me/favoriteServers/1")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body(emptyOrNullString());
    }

    // DELETE /users/me //

    @Test
    @Order(7)
    void deleteUser_success() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .delete("/users/me")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .body(emptyOrNullString());
    }

    @Test
    @Order(7)
    void deleteUser_unauthorized() {
        given()
            .auth().basic("...", "...")
        .when()
            .delete("/users/me")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body(emptyOrNullString());
    }

}