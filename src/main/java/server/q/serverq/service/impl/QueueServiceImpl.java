package server.q.serverq.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.q.serverq.entity.ReportQ;
import server.q.serverq.entity.ReportQList;
import server.q.serverq.repository.ReportQListRepository;
import server.q.serverq.repository.ReportQRepository;
import server.q.serverq.service.QueueService;

@Service("QueueService")
public class QueueServiceImpl implements QueueService{

    private static Logger LOGGER = LoggerFactory.getLogger(QueueServiceImpl.class);

    @Autowired
    private ReportQRepository reportQRepository;

    @Autowired
    private ReportQListRepository reportQListRepository;


    @Override
    public void saveQueue(String status, String reportName, String criteriaString) {
        LOGGER.info("DATA SAVE TO DB : {} , {}",criteriaString,reportName);
        ReportQ reportQ  = new ReportQ();
        reportQ.setStatus(status);
        reportQ.setReportName(reportName);
        reportQ.setCriteriaString(criteriaString);

        ReportQList reportQList = new ReportQList();
        reportQList.setStatus(status);
        reportQList.setReportName(reportName);
        reportQList.setCriteriaString(criteriaString);

        LOGGER.info("QueueServiceImplement : {}",reportQ);
        try {
            reportQRepository.save(reportQ);
            reportQListRepository.save(reportQList);
        }catch (Exception e){
            LOGGER.error("ERR {} ",e.getMessage());
            e.printStackTrace();
        }

    }
}
