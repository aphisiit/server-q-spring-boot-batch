package server.q.serverq.controller;

import flexjson.JSONSerializer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.q.serverq.entity.Customer;
import server.q.serverq.repository.ReportQListRepository;
import server.q.serverq.repository.ReportQRepository;
import server.q.serverq.service.HandleReportService;
import server.q.serverq.service.QueueService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAsync
@Controller
public class PrintQController {

    private static Logger LOGGER = LoggerFactory.getLogger(PrintQController.class);
    private static boolean checkProcess = false;
    private static String SOURCE_FOLDER = "C:\\Users\\aphisit\\Desktop\\";
    private static int index = 0;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ReportQRepository reportQRepository;

    @Autowired
    private HandleReportService handleReportService;

    @Autowired
    private ReportQListRepository reportQListRepository;

    @GetMapping("/")
    public String index(){
        LOGGER.info("INDEX METHOD ===");
        return "index";
    }

    @SuppressWarnings("all")
    @GetMapping(value = "/getCustomer",produces = "text/html;charset=utf-8", headers = "Accept=application/json; charset=utf-8")
    public ResponseEntity<String> query(@Param("country") String country){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Query query = entityManager.createQuery("" +
                "SELECT c FROM Customer c where country like concat('%',:country,'%') ")
                .setParameter("country",country);

        List<Customer> result = query.getResultList();

        Map<String,String> map;
        LOGGER.info("getCustomer size : {}",query.getResultList().size());

        if(result.size() > 10){
            map = new HashMap<>();
            map.put("queue","yes");
            return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("class").serialize(map),headers,HttpStatus.OK);
        }else{
            return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).exclude("class").serialize(result),headers,HttpStatus.OK);
        }
    }

    @SuppressWarnings("all")
    @GetMapping(value = "/getCustomerSize",produces = "text/html;charset=utf-8", headers = "Accept=application/json; charset=utf-8")
    public ResponseEntity<String> getCustomerSize(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Query query = entityManager.createQuery("select count(c) as size from Customer c");

        Long size = Long.valueOf(query.getResultList().get(0).toString());
        LOGGER.info("DATA SIZE : {} ",size);
        Map<String,Long> map = new HashMap<>();
        map.put("size" ,size);


        return new ResponseEntity<String>(new JSONSerializer().serialize(map),headers,HttpStatus.OK);
    }

    @RequestMapping(value = "/gotoQ",produces = "text/html;charset=utf-8", headers = "Accept=application/json; charset=utf-8",method = RequestMethod.POST)
    @Async
    public ResponseEntity<String> stackQ(
            @RequestParam(name = "criteriaString",required = false) String criteriaString,
            @RequestParam(name = "reportName",required = false) String reportName
    ){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");

//        LOGGER.info("DATA SAVE TO DB : {} , {}",criteriaString,reportName);
//        ReportQ reportQ  = new ReportQ();
//        reportQ.setStatus("On process");
//        reportQ.setReportName("test" + (index) + ".xlsx");
//        reportQ.setCriteriaString(criteriaString);
//
//        ReportQList reportQList = new ReportQList();
//        reportQList.setStatus("On process");
//        reportQList.setReportName("test" + (index) + ".xlsx");
//        reportQList.setCriteriaString(criteriaString);
//
        try {

            queueService.saveQueue("On process","test" + (++index) + ".xlsx",criteriaString);

            if(!checkProcess){
                try {
                    generateReport();
                } catch (InterruptedException e) {
                    checkProcess = false;
                    LOGGER.error("ERR {} ",e.getMessage());
                }
            }
        }catch (Exception e){
            checkProcess = false;
            LOGGER.error("ERR {} ",e.getMessage());
        }
        return new ResponseEntity<String>("OK",headers,HttpStatus.OK);

    }


    @Async
    public void generateReport() throws InterruptedException{
        LOGGER.info("@Async generateReport()");
        Long size = reportQRepository.findSize();
        if(size > 0){
            if(!checkProcess){
                checkProcess = true;
//                List<ReportQ> reportQList = reportQRepository.findAll();
//                ReportQList data;
                handleReportService.generateReport();
//                LOGGER.info("DOING REPORT QUEUES");
//                for(ReportQ reportQ : reportQList){
//
//                    XSSFWorkbook workbook = new XSSFWorkbook();
//                    XSSFSheet sheet = workbook.createSheet("Data type in Java");
//
//                    List<Customer> customerList = getCustomerByCountry(reportQ.getCriteriaString());
//                    LOGGER.info("reportQ.getCriteriaString() : {} ",reportQ.getCriteriaString());
//
//                    int rowNum = 0;
//                    for(Customer customer : customerList){
//                        Row row = sheet.createRow(rowNum++);
//                        int colNum = 0;
//                        Cell cell = row.createCell(colNum++);
//                        cell.setCellValue(customer.getId());
//                        cell = row.createCell(colNum++);
//                        cell.setCellValue(customer.getFirstName());
//                        cell = row.createCell(colNum++);
//                        cell.setCellValue(customer.getLastName());
//                        cell = row.createCell(colNum++);
//                        cell.setCellValue(customer.getCity());
//                        cell = row.createCell(colNum++);
//                        cell.setCellValue(customer.getCountry());
//                        cell = row.createCell(colNum++);
//                        cell.setCellValue(customer.getPhone());
//                    }
//
//
//                    try {
//
////                        FileOutputStream fileOutputStream = new FileOutputStream(SOURCE_FOLDER + "\\" + reportQ.getId() + "-" + reportQ.getReportName() + ".xlsx");
//                        FileOutputStream fileOutputStream = new FileOutputStream(reportQ.getId() + "-" + reportQ.getReportName());
//                        workbook.write(fileOutputStream);
//                        workbook.close();
//
//                        data = reportQListRepository.findOne(reportQ.getId());
//                        data.setStatus("Completed");
//                        reportQListRepository.saveAndFlush(data);
//
//                        reportQRepository.delete(reportQ.getId());
//
//                    } catch (FileNotFoundException e) {
//                        LOGGER.error("GET ERROR : {}",e.getMessage());
//                        e.printStackTrace();
//                        checkProcess = false;
//                    } catch (IOException e) {
//                        LOGGER.error("GET ERROR : {}",e.getMessage());
//                        e.printStackTrace();
//                        checkProcess = false;
//                    }
//                }
//                LOGGER.info("FINISHED QUEUES");
                checkProcess = false;
                size = reportQRepository.findSize();
//
                if(size > 0){
                    generateReport();
                }else {
                    checkProcess = false;
                }
            }
        }
    }

//    private List<Customer> getCustomerByCountry(@RequestParam("country") String country){
//        return customerRepository.findByCountryIgnoreCaseContaining(country);
//    }

    @SuppressWarnings("Duplicates")
    @GetMapping(value = "/getQueueNow",produces = "text/html;charset=utf-8", headers = "Accept=application/json; charset=utf-8")
    public ResponseEntity<String> getQ(){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");

        try {
            return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(reportQRepository.findAll()),headers,HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error("getQ Exception : {}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("Duplicates")
    @GetMapping(value = "/viewQueue",produces = "text/html;charset=utf-8", headers = "Accept=application/json; charset=utf-8")
    public ResponseEntity<String> viewQ(){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");

        try {
            return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(reportQListRepository.findAll()),headers,HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error("getQ Exception : {}",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> download(
            @PathVariable("file_name") String param,
            HttpServletResponse response) throws IOException{

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type","application/vnd.ms-excel");

        File file = new File(param);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return  ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(file.length())
                .body(resource);

    }
}
