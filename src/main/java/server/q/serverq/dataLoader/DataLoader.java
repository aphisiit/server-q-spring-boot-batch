package server.q.serverq.dataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import server.q.serverq.entity.ReportQ;
import server.q.serverq.repository.ReportQRepository;

import java.sql.Timestamp;

//@Component
public class DataLoader implements CommandLineRunner{

    @Autowired
    private ReportQRepository reportQRepository;

    @Override
    public void run(String... strings) throws Exception {
        ReportQ reportQ = new ReportQ();
        reportQ.setCriteriaString("text=1234&id=1&name=aphisit");
        reportQ.setCreatedDate(Timestamp.valueOf("2017-11-13 22:13:50"));
        reportQ.setReportName("TestReport.xlsx");
        reportQRepository.save(reportQ);

        ReportQ reportQ2 = new ReportQ();
        reportQ2.setCriteriaString("text=1111&id=1&name=namracha");
        reportQ2.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        reportQ2.setReportName("TestReport.xlsx");
        reportQRepository.save(reportQ2);

    }
}
