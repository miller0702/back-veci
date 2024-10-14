package veci.veciback.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthRequest {
    @NotNull(message = "El usuario no puede ser nulo")
    @Size(min = 1, message = "El usuario no puede estar vacío")
    private String user;

    @NotNull(message = "La contraseña no puede ser nula")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    public AuthRequest() {}

    public AuthRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
