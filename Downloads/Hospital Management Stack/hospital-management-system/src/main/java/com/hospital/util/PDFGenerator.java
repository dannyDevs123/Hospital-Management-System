package com.hospital.util;

import com.hospital.model.Bill;
import javafx.scene.control.Alert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for generating PDF documents.
 */
public class PDFGenerator {
    private static final Logger LOGGER = Logger.getLogger(PDFGenerator.class.getName());
    
    /**
     * Generate a bill receipt PDF
     */
    public static void generateBillReceipt(Bill bill) {
        String filename = bill.getBillNumber() + ".pdf";
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            float yPosition = 750;
            float margin = 50;
            float pageWidth = page.getMediaBox().getWidth();
            
            // Header
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("MediCare Hospital Management System");
            contentStream.endText();
            
            yPosition -= 30;
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Bill Receipt");
            contentStream.endText();
            
            yPosition -= 40;
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            
            // Bill details
            String[][] details = {
                    {"Bill Number:", bill.getBillNumber()},
                    {"Date:", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))},
                    {"Patient:", bill.getPatientName()},
                    {"", ""},
                    {"Consultation Fee:", String.format("$%.2f", bill.getConsultationFee())},
                    {"Medicine Fee:", String.format("$%.2f", bill.getMedicineFee())},
                    {"Service Charge:", String.format("$%.2f", bill.getServiceCharge())},
                    {"Tax (10%):", String.format("$%.2f", bill.getTaxAmount())},
                    {"", ""},
                    {"TOTAL AMOUNT:", String.format("$%.2f", bill.getTotalAmount())}
            };
            
            float labelX = margin;
            float valueX = margin + 150;
            
            for (String[] row : details) {
                contentStream.beginText();
                contentStream.newLineAtOffset(labelX, yPosition);
                contentStream.setFont(new PDType1Font(
                        row[0].equals("TOTAL AMOUNT:") ? Standard14Fonts.FontName.HELVETICA_BOLD : Standard14Fonts.FontName.HELVETICA), 
                        row[0].equals("TOTAL AMOUNT:") ? 14 : 10);
                contentStream.showText(row[0]);
                contentStream.endText();
                
                if (!row[1].isEmpty()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(valueX, yPosition);
                    contentStream.setFont(new PDType1Font(
                            row[0].equals("TOTAL AMOUNT:") ? Standard14Fonts.FontName.HELVETICA_BOLD : Standard14Fonts.FontName.HELVETICA), 
                            row[0].equals("TOTAL AMOUNT:") ? 14 : 10);
                    contentStream.showText(row[1]);
                    contentStream.endText();
                }
                
                yPosition -= 20;
            }
            
            // Footer
            yPosition -= 40;
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 9);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Thank you for choosing MediCare Hospital.");
            contentStream.endText();
            
            yPosition -= 15;
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("This is a computer-generated receipt and does not require a signature.");
            contentStream.endText();
            
            contentStream.close();
            
            // Save file
            File file = saveToDownloads(filename, document);
            
            LOGGER.info("Bill PDF generated: " + file.getAbsolutePath());
            showInfo("Bill PDF generated successfully!\nSaved to: " + file.getAbsolutePath());
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error generating bill PDF", e);
            showError("Failed to generate PDF: " + e.getMessage());
        }
    }
    
    /**
     * Generate a report PDF
     */
    public static void generateReport(String title, String content, LocalDate startDate, LocalDate endDate) {
        String filename = "Report_" + title.replace(" ", "_") + "_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            float yPosition = 750;
            float margin = 50;
            
            // Header
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("MediCare Hospital Management System");
            contentStream.endText();
            
            yPosition -= 25;
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText(title);
            contentStream.endText();
            
            yPosition -= 20;
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Period: " + startDate + " to " + endDate);
            contentStream.endText();
            
            yPosition -= 10;
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Generated: " + LocalDate.now());
            contentStream.endText();
            
            yPosition -= 20;
            
            // Content - split into lines
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 8);
            String[] lines = content.split("\n");
            
            for (String line : lines) {
                if (yPosition < 50) {
                    // Add new page
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = 750;
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 8);
                }
                
                // Handle long lines
                String remaining = line;
                while (!remaining.isEmpty()) {
                    String chunk;
                    if (remaining.length() > 95) {
                        chunk = remaining.substring(0, 95);
                        remaining = remaining.substring(95);
                    } else {
                        chunk = remaining;
                        remaining = "";
                    }
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(chunk);
                    contentStream.endText();
                    yPosition -= 12;
                }
            }
            
            contentStream.close();
            
            // Save file
            File file = saveToDownloads(filename, document);
            
            LOGGER.info("Report PDF generated: " + file.getAbsolutePath());
            showInfo("Report PDF generated successfully!\nSaved to: " + file.getAbsolutePath());
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error generating report PDF", e);
            showError("Failed to generate PDF: " + e.getMessage());
        }
    }
    
    /**
     * Save PDF to the user's Downloads folder.
     */
    private static File saveToDownloads(String filename, PDDocument document) throws IOException {
        String userHome = System.getProperty("user.home");
        File exportFile = new File(userHome + File.separator + "Downloads" + File.separator + filename);
        File downloadsDir = exportFile.getParentFile();
        if (downloadsDir != null && !downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }
        document.save(exportFile);
        return exportFile;
    }
    
    private static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("PDF Export");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("PDF Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
