package com.dicka.app.jasper_vaadin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JasperReportService {

    private DataSource createDataSource(){
        DriverManagerDataSource sourceData = new DriverManagerDataSource();
        sourceData.setDriverClassName("org.postgresql.Driver");
        sourceData.setUrl("jdbc:postgresql://localhost:5432/db_rnd");
        sourceData.setUsername("rnd");
        sourceData.setPassword("rnd");
        return sourceData;
    }

    public byte[] generatePdfReport() throws JRException, SQLException {
        try(Connection connection = createDataSource().getConnection()){
            InputStream inputStream = new ClassPathResource("reports/generated_report_users.jrxml").getInputStream();

            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            Map<String,Object> paramters = new HashMap<>();
            paramters.put("REPORT_TITLE", "Report User Manulife");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, paramters, connection);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (IOException e) {
            throw new RuntimeException("Error while generating report : "+e.getMessage());
        }
    }
}
