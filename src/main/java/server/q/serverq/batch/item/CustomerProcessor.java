package server.q.serverq.batch.item;

import flexjson.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import server.q.serverq.entity.Customer;
import server.q.serverq.entity.ReportQ;
import server.q.serverq.entity.ReportQList;
import server.q.serverq.repository.CustomerRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class CustomerProcessor implements ItemProcessor<ReportQ,ReportQ> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerProcessor.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public ReportQ process(ReportQ reportQ) throws Exception {
        final Long id = reportQ.getId();
        final Timestamp createdDate = reportQ.getCreatedDate();
        final String criteriaString = reportQ.getCriteriaString();
        final String reportName = reportQ.getReportName();
        final String status = reportQ.getStatus();
        final String operation = reportQ.getOperation();

        final ReportQ transformedPerson = new ReportQ(id,reportName,status,criteriaString,createdDate,operation);

        LOGGER.info("Converting ({}) in to ({})",reportName,transformedPerson);

//        Long size = reportQRepository.findSize();
//        LOGGER.info("ReportQ size : {}",size);
//        if(size > 0){
//            if(!checkProcess){
//                List<ReportQ> reportQList = reportQRepository.findAll();
//                ReportQList data;
//
//                for(ReportQ reportQ : reportQList){
//                    try {
//                        List<Customer> customerList = getCustomerByCountry(reportQ.getCriteriaString());
//                        LOGGER.info("reportQ.getCriteriaString() : {} ",reportQ.getCriteriaString());
//                        FileOutputStream fileOutputStream = new FileOutputStream(SOURCE_FOLDER + "/" + (index++) + ".json");
//                        fileOutputStream.write(new JSONSerializer().prettyPrint(true).serialize(customerList).getBytes());
//                        fileOutputStream.close();
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
//                    } catch (IOException e) {
//                        LOGGER.error("GET ERROR : {}",e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//                checkProcess = false;
//            }else {
////                checkProcess = true;
////                generateReport();
//            }
//        }

        return transformedPerson;


    }
}
