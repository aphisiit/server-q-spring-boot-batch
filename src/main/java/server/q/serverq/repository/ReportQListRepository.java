package server.q.serverq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import server.q.serverq.entity.ReportQList;

import java.util.List;

public interface ReportQListRepository extends JpaRepository<ReportQList,Long>,JpaSpecificationExecutor<ReportQList>,PagingAndSortingRepository<ReportQList,Long>{

    List<ReportQList> findReportQListByReportNameIgnoreCaseContaining(@Param("reportName") String reportName);
}
