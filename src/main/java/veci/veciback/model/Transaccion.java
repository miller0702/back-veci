package veci.veciback.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transaccion")
public class Transaccion {
    @Id
    private String transactionalID;
    private double value;
    private String cellPhone;
    private String supplierId;

    public Transaccion() {}

    public Transaccion(String supplierId, double value, String cellPhone) {
        this.supplierId = supplierId;
        this.value = value;
        this.cellPhone = cellPhone;
    }

    public String getTransactionalID() {
        return transactionalID;
    }

    public void setTransactionalID(String transactionalID) {
        this.transactionalID = transactionalID;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}
