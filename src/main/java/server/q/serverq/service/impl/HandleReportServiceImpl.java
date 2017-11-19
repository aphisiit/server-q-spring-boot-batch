package server.q.serverq.service.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import server.q.serverq.entity.Customer;
import server.q.serverq.entity.ReportQ;
import server.q.serverq.entity.ReportQList;
import server.q.serverq.repository.CustomerRepository;
import server.q.serverq.repository.ReportQListRepository;
import server.q.serverq.repository.ReportQRepository;
import server.q.serverq.service.EmailService;
import server.q.serverq.service.HandleReportService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service("HandleReportService")
public class HandleReportServiceImpl implements HandleReportService {

    private static Logger LOGGER = LoggerFactory.getLogger(HandleReportServiceImpl.class);
    private static String SOURCE_FOLDER = "C:\\Users\\aphisit\\Desktop\\";

    @Autowired
    private ReportQRepository reportQRepository;

    @Autowired
    private ReportQListRepository reportQListRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;


    @Override
    public void generateReport() {
        Long size = reportQRepository.findSize();
        LOGGER.info("ReportQ size : {}",size);
//        if(size > 0){
        List<ReportQ> reportQList = reportQRepository.findAll();
        ReportQList data;

        LOGGER.info("DOING REPORT QUEUES");
        for(ReportQ reportQ : reportQList){

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Data type in Java");

            List<Customer> customerList = getCustomerByCountry(reportQ.getCriteriaString());
            LOGGER.info("reportQ name : {} ",reportQ.getReportName());

            int rowNum = 0;
            for(Customer customer : customerList){
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                Cell cell = row.createCell(colNum++);
                cell.setCellValue(customer.getId());
                cell = row.createCell(colNum++);
                cell.setCellValue(customer.getFirstName());
                cell = row.createCell(colNum++);
                cell.setCellValue(customer.getLastName());
                cell = row.createCell(colNum++);
                cell.setCellValue(customer.getCity());
                cell = row.createCell(colNum++);
                cell.setCellValue(customer.getCountry());
                cell = row.createCell(colNum++);
                cell.setCellValue(customer.getPhone());
            }


            try {

//                FileOutputStream fileOutputStream = new FileOutputStream(SOURCE_FOLDER + reportQ.getId() + "-" + reportQ.getReportName());
                FileOutputStream fileOutputStream = new FileOutputStream(reportQ.getId() + "-" + reportQ.getReportName());
                workbook.write(fileOutputStream);
                workbook.close();

                data = reportQListRepository.findOne(reportQ.getId());
                data.setReportName(reportQ.getId() + "-" + reportQ.getReportName());
                data.setStatus("Completed");
                reportQListRepository.saveAndFlush(data);

                emailService.sendMessageWithAttachment("aphisiit086757@hotmail.com",
                        reportQ.getId() + "-" + reportQ.getReportName(),
                        "this is file report " + reportQ.getId() + "-" + reportQ.getReportName(),
                        reportQ.getId() + "-" + reportQ.getReportName());

                reportQRepository.delete(reportQ.getId());

            } catch (FileNotFoundException e) {
                LOGGER.error("GET ERROR : {}",e.getMessage());
            } catch (IOException e) {
                LOGGER.error("GET ERROR : {}",e.getMessage());
            }
        }
        LOGGER.info("FINISHED QUEUES");
//                size = reportQRepository.findSize();

//                if(size > 0){
//                    generateReport(checkProcess);
//                }else {
//                    checkProcess = false;
//                }
//            }
//        }

    }

    private List<Customer> getCustomerByCountry(@RequestParam("country") String country){
        return customerRepository.findByCountryIgnoreCaseContaining(country);
    }
}
