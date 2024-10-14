package veci.veciback.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Proveedor {
    private String id;
    private String name;

    public Proveedor() {
    }

    public Proveedor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y Setters
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
