package it.unipi.CellMap;

public class APIErrorResponse {
    private String error;

    public APIErrorResponse(String error) {
        this.error = error;
    }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
