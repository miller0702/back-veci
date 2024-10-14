package veci.veciback.controller;

import jakarta.validation.constraints.*;

public class RecargaRequest {

    @NotNull(message = "El número de teléfono no puede ser nulo")
    @Pattern(regexp = "^3\\d{9}$", message = "El número de teléfono debe iniciar con '3' y tener 10 dígitos")
    private String cellPhone;

    @NotNull(message = "El valor no puede ser nulo")
    @DecimalMin(value = "1000", message = "El valor de la recarga debe ser al menos 1000")
    @DecimalMax(value = "100000", message = "El valor de la recarga no puede ser mayor a 100000")
    private double value;

    @NotNull(message = "El ID del proveedor no puede ser nulo")
    private String supplierId;

    public RecargaRequest() {}

    public RecargaRequest(String cellPhone, double value, String supplierId) {
        this.cellPhone = cellPhone;
        this.value = value;
        this.supplierId = supplierId;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}
