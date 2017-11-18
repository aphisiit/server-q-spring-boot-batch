package server.q.serverq.controller;

import flexjson.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.q.serverq.entity.Customer;
import server.q.serverq.entity.ReportQ;
import server.q.serverq.entity.ReportQList;
import server.q.serverq.repository.CustomerRepository;
import server.q.serverq.repository.ReportQListRepository;
import server.q.serverq.repository.ReportQRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAsync
@Controller
public class PrintQController {

    private static Logger LOGGER = LoggerFactory.getLogger(PrintQController.class);
    private static boolean checkProcess = false;
    private static String SOURCE_FOLDER = "C:\\Users\\aphisit\\Desktop\\";
    private static int index = 1;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ReportQRepository reportQRepository;

    @Autowired
    private CustomerRepository customerRepository;

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

        LOGGER.info("DATA SAVE TO DB : {} , {}",criteriaString,reportName);
        ReportQ reportQ  = new ReportQ();
        reportQ.setStatus("On process");
        reportQ.setReportName(reportName);
        reportQ.setCriteriaString(criteriaString);

        ReportQList reportQList = new ReportQList();
        reportQList.setStatus("On process");
        reportQList.setReportName(reportName);
        reportQList.setCriteriaString(criteriaString);

        LOGGER.info("reportQ : {}",reportQ);
        try {
            reportQRepository.save(reportQ);
            reportQListRepository.save(reportQList);
        }catch (Exception e){
            LOGGER.error("ERR {} ",e.getMessage());
            e.printStackTrace();
        }

        try {
            generateReport();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("OK",headers,HttpStatus.OK);

    }


    public void generateReport() throws InterruptedException{
//        Thread.sleep(10000);
        Long size = reportQRepository.findSize();
        LOGGER.info("ReportQ size : {}",size);
        if(size > 0){
            if(!checkProcess){
                List<ReportQ> reportQList = reportQRepository.findAll();
                ReportQList data;

                LOGGER.info("DOING REPORT QUEUES");
                for(ReportQ reportQ : reportQList){
                    try {
                        List<Customer> customerList = getCustomerByCountry(reportQ.getCriteriaString());
                        LOGGER.info("reportQ.getCriteriaString() : {} ",reportQ.getCriteriaString());
                        FileOutputStream fileOutputStream = new FileOutputStream(SOURCE_FOLDER + "/" + (index++) + ".json");
                        fileOutputStream.write(new JSONSerializer().prettyPrint(true).serialize(customerList).getBytes());
                        fileOutputStream.close();

                        data = reportQListRepository.findOne(reportQ.getId());
                        data.setStatus("Completed");
                        reportQListRepository.saveAndFlush(data);

                        reportQRepository.delete(reportQ.getId());

                    } catch (FileNotFoundException e) {
                        LOGGER.error("GET ERROR : {}",e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        LOGGER.error("GET ERROR : {}",e.getMessage());
                        e.printStackTrace();
                    }
                }
                LOGGER.info("FINISHED QUEUES");
                checkProcess = false;

                reportQList = null;
                data = null;

                size = reportQRepository.findSize();



                if(size > 0){
                    checkProcess = true;
                    generateReport();
                }
            }else {
                checkProcess = true;
                generateReport();
            }
        }
    }

    private List<Customer> getCustomerByCountry(@RequestParam("country") String country){
        return customerRepository.findByCountryIgnoreCaseContaining(country);
    }

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


}
