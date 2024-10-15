package veci.veciback.controller;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.http.*;
import veci.veciback.model.Proveedor;
import veci.veciback.model.Transaccion;
import veci.veciback.service.PuntoredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;

@RestController
@RequestMapping("/api")
public class RecargaController {

    @Autowired
    private PuntoredService puntoredService;

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = puntoredService.authenticate(authRequest.getUser(), authRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/getSuppliers")
    public ResponseEntity<List<Proveedor>> getSuppliers() {
        List<Proveedor> suppliers = puntoredService.getSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @PostMapping("/buy")
    public ResponseEntity<Transaccion> buy(@Valid @RequestBody RecargaRequest recargaRequest) {
        Transaccion transaccion = puntoredService.buy(recargaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccion);
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<Transaccion>> getAllTransacciones() {
        List<Transaccion> transacciones = puntoredService.getAllTransacciones();
        return ResponseEntity.ok(transacciones);
    }

    private static final Logger logger = LoggerFactory.getLogger(TransaccionController.class);

    @GetMapping("/transaccion/{id}/ticket")
    public ResponseEntity<byte[]> getTicket(@PathVariable String id) {
        try {
            logger.info("Iniciando generación del ticket para transacción con id: {}", id);

            // Obtención de la transacción y proveedor
            Transaccion transaccion = puntoredService.getTicketById(id);
            if (transaccion == null) {
                logger.error("Transacción no encontrada para el id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Proveedor proveedor = puntoredService.getProveedorById(transaccion.getSupplierId());
            if (proveedor == null) {
                logger.error("Proveedor no encontrado para el supplierId: {}", transaccion.getSupplierId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            logger.info("Transacción y proveedor encontrados. Iniciando creación de PDF...");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, new PageSize(80, 200));

            document.setMargins(5, 5, 5, 5);

            String logoPath = "src/main/resources/assets/logo.png";
            Image logo;
            try {
                logo = new Image(ImageDataFactory.create(logoPath));
                logo.scaleToFit(40, 40);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(logo);
            } catch (MalformedURLException e) {
                logger.error("Error al cargar el logo desde la ruta: {}", logoPath, e);
                document.add(new Paragraph("LOGO NO DISPONIBLE")
                        .setFontSize(4)
                        .setTextAlignment(TextAlignment.CENTER));
            }

        Paragraph title = new Paragraph("TICKET DE TRANSACCIÓN")
                .setBold()
                .setFontSize(4)
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        title.setMarginTop(5);
        title.setMarginBottom(3);
        document.add(title);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Paragraph dateTimeParagraph = new Paragraph( formattedDateTime)
                .setFontSize(2)
                .setTextAlignment(TextAlignment.CENTER);
        dateTimeParagraph.setMarginTop(1);
        dateTimeParagraph.setMarginBottom(1);
        document.add(dateTimeParagraph);

        Paragraph transactionId = new Paragraph("Cod. Transacción: " + transaccion.getTransactionalID())
                .setFontSize(3)
                .setTextAlignment(TextAlignment.LEFT);
        transactionId.setMarginTop(1);
        transactionId.setMarginBottom(1);
        document.add(transactionId);

        Paragraph supplierName = new Paragraph("Operador: " + proveedor.getName())
                .setFontSize(3)
                .setTextAlignment(TextAlignment.LEFT);
        supplierName.setMarginTop(1);
        supplierName.setMarginBottom(1);
        document.add(supplierName);

        Paragraph value = new Paragraph("Valor: $" + String.format("%.2f", transaccion.getValue()))
                .setFontSize(3)
                .setTextAlignment(TextAlignment.LEFT);
        value.setMarginTop(1);
        value.setMarginBottom(1);
        document.add(value);

        String formattedPhone = formatPhoneNumber(transaccion.getCellPhone());
        Paragraph phone = new Paragraph("Teléfono: " + formattedPhone)
                .setFontSize(3)
                .setTextAlignment(TextAlignment.LEFT);
        phone.setMarginTop(1);
        phone.setMarginBottom(5);
        document.add(phone);

        document.add(new LineSeparator(new SolidLine(0.5f))
                .setMarginTop(2)
                .setMarginBottom(2));

        Paragraph policyTitle = new Paragraph("POLÍTICA DE RECARGAS:")
                .setBold()
                .setFontSize(2)
                .setTextAlignment(TextAlignment.CENTER);
        policyTitle.setMarginTop(3);
        policyTitle.setMarginBottom(1);
        document.add(policyTitle);

        String[] policies = {
                "1. Las recargas son procesadas inmediatamente después de la confirmación de pago.",
                "2. Existen límites diarios y mensuales para las recargas.",
                "3. Las recargas son finales y no se permiten cancelaciones ni reembolsos.",
                "4. Es responsabilidad del cliente asegurarse de que la información proporcionada sea correcta.",
                "5. Para consultas, contactar al servicio de atención al cliente."
        };

        for (String policy : policies) {
            Paragraph policyParagraph = new Paragraph(policy)
                    .setFontSize(2)
                    .setTextAlignment(TextAlignment.LEFT);
            policyParagraph.setMarginTop(1);
            policyParagraph.setMarginBottom(1);
            document.add(policyParagraph);
        }

        document.close();
        byte[] pdfBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("ticket_" + id + ".pdf").build());

        logger.info("Ticket PDF generado correctamente para la transacción con id: {}", id);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

    } catch (Exception e) {
        logger.error("Error al generar el ticket para la transacción con id: {}", id, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    }

    private String formatPhoneNumber(String cellPhone) {
        if (cellPhone.length() == 10) {
            return String.format("(%s) %s - %s",
                    cellPhone.substring(0, 3),
                    cellPhone.substring(3, 6),
                    cellPhone.substring(6));
        }
        return cellPhone;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
