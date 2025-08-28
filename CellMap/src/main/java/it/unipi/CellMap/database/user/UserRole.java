
package it.unipi.CellMap.database.user;

public enum UserRole {
    ADMIN("ADMIN"),
    HOST("HOST"),
    REGULAR("REGULAR");

    private final String value;

    UserRole(String value) { this.value = value; }

    @Override
    public String toString() { return value; }
}
