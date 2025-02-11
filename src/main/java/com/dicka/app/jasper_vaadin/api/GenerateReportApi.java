package com.dicka.app.jasper_vaadin.api;

import com.dicka.app.jasper_vaadin.service.JasperReportService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping(value = "/api/reports")
@RequiredArgsConstructor
public class GenerateReportApi {

    private final JasperReportService jasperReportService;

    @GetMapping
    public ResponseEntity<byte[]> generatedReport() throws JRException, SQLException {
        byte[] pdfBytes = jasperReportService.generatePdfReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
