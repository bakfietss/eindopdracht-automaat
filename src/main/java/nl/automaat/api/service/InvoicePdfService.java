package nl.automaat.api.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import nl.automaat.api.model.Car;
import nl.automaat.api.model.Invoice;
import nl.automaat.api.model.Part;
import nl.automaat.api.model.Repair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class InvoicePdfService {

    private final InvoiceService invoiceService;

    public InvoicePdfService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public byte[] generatePdf(Long invoiceId) {
        Invoice invoice = invoiceService.findOrThrow(invoiceId);
        Repair repair = invoice.getRepair();
        Car car = repair.getCar();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Paragraph title = new Paragraph("AutoMaat - Bon #" + invoice.getId(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        document.add(new Paragraph("Datum: " + LocalDate.now(), normal));
        document.add(new Paragraph("Reparatie-id: " + repair.getId(), normal));
        if (car != null) {
            document.add(new Paragraph("Auto: " + car.getBrand() + " " + car.getModel()
                    + " (" + car.getLicensePlate() + ")", normal));
        }
        document.add(new Paragraph(" ", normal));

        document.add(new Paragraph("Onderdelen:", normal));
        for (Part part : repair.getParts()) {
            document.add(new Paragraph("  - " + part.getName() + ": EUR " + part.getPrice(), normal));
        }
        document.add(new Paragraph(" ", normal));

        document.add(new Paragraph("BTW (21%): EUR " + invoice.getVatAmount(), normal));
        document.add(new Paragraph("Totaalbedrag (incl. BTW): EUR " + invoice.getTotalAmount(), normal));
        document.add(new Paragraph("Betaalstatus: " + invoice.getPaymentStatus(), normal));

        document.close();
        return out.toByteArray();
    }
}
