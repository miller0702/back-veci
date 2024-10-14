package veci.veciback.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import veci.veciback.controller.AuthRequest;
import veci.veciback.controller.AuthResponse;
import veci.veciback.controller.RecargaRequest;
import veci.veciback.model.Proveedor;
import veci.veciback.model.Transaccion;
import veci.veciback.repository.TransaccionRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@Service
public class PuntoredService {

    @Value("${puntored.api.base-url}")
    private String baseUrl;

    @Value("${puntored.x.api.key}")
    private String apiKey;

    private String token;

    @Autowired
    private TransaccionRepository transaccionRepository;

    public AuthResponse authenticate(String user, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "/auth";
        AuthRequest authRequest = new AuthRequest(user, password);
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<AuthRequest> requestEntity = new HttpEntity<>(authRequest, headers);

        try {
            String response = restTemplate.postForObject(url, requestEntity, String.class);
            token = extractToken(response);
            return new AuthResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("Error al autenticar: " + e.getMessage());
        }
    }

    private List<Proveedor> proveedoresCache = new ArrayList<>(); // Cache para los proveedores

    public List<Proveedor> getSuppliers() {
        validateToken();

        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "/getSuppliers";

        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            String responseString = responseEntity.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            proveedoresCache = objectMapper.readValue(responseString, new TypeReference<List<Proveedor>>() {});
            return proveedoresCache; // Devuelve la lista de proveedores
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener proveedores: " + e.getMessage());
        }
    }

    public Proveedor getProveedorById(String supplierId) {
        validateToken();

        for (Proveedor proveedor : proveedoresCache) {
            if (proveedor.getId().equals(supplierId)) {
                return proveedor;
            }
        }
        throw new RuntimeException("Proveedor no encontrado con ID: " + supplierId);
    }

    public Transaccion buy(RecargaRequest recargaRequest) {
        validateToken();
        validateCellPhone(recargaRequest.getCellPhone());
        validateValue(recargaRequest.getValue());

        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "/buy";

        try {
            HttpEntity<RecargaRequest> requestEntity = new HttpEntity<>(recargaRequest, createHeaders());
            String response = restTemplate.postForObject(url, requestEntity, String.class);
            return saveTransaction(recargaRequest.getCellPhone(), recargaRequest.getValue(), recargaRequest.getSupplierId());
        } catch (Exception e) {
            throw new RuntimeException("Error al realizar la compra: " + e.getMessage());
        }
    }

    private String extractToken(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al extraer el token: " + e.getMessage());
        }
    }

    private void validateToken() {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token no disponible. Por favor, autentique primero.");
        }
    }


    private void validateCellPhone(String cellPhone) {
        if (!cellPhone.startsWith("3")) {
            throw new IllegalArgumentException("El número de teléfono debe iniciar con '3'.");
        }
        if (cellPhone.length() != 10) {
            throw new IllegalArgumentException("El número de teléfono debe tener una longitud de 10 caracteres.");
        }
        if (!cellPhone.matches("\\d+")) {
            throw new IllegalArgumentException("El número de teléfono solo puede contener caracteres numéricos.");
        }
    }

    private void validateValue(double value) {
        if (value < 1000 || value > 100000) {
            throw new IllegalArgumentException("El valor de la recarga debe estar entre 1000 y 100000.");
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        return headers;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }

    public List<Transaccion> getAllTransacciones() {
        return transaccionRepository.findAll();
    }

    public Transaccion getTicketById(String id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con ID: " + id));
        Transaccion ticket = new Transaccion();
        ticket.setTransactionalID(transaccion.getTransactionalID());
        ticket.setSupplierId(transaccion.getSupplierId());
        ticket.setValue(transaccion.getValue());
        ticket.setCellPhone(transaccion.getCellPhone());

        return ticket;
    }

    private Transaccion saveTransaction(String cellPhone, double value, String supplierId) {
        Transaccion transaccion = new Transaccion();
        transaccion.setCellPhone(cellPhone);
        transaccion.setValue(value);
        transaccion.setSupplierId(supplierId);
        return transaccionRepository.save(transaccion);
    }

}
