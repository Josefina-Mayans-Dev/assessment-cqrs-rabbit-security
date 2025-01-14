package ec.com.sofka.data;

public class AuthResponse {

    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse(){}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Builder
    public static class Builder {
        private String token;

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        // Cambié builder() a static y se utiliza este método para crear el builder
        public static Builder builder() {
            return new Builder();
        }

        public AuthResponse build() {
            return new AuthResponse(token);
        }
    }
}
