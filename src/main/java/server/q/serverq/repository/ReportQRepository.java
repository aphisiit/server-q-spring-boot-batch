package server.q.serverq.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import server.q.serverq.entity.ReportQ;

import java.util.List;

public interface ReportQRepository extends JpaRepository<ReportQ, Long>,JpaSpecificationExecutor<ReportQ>,PagingAndSortingRepository<ReportQ,Long> {

    List<ReportQ> findReportQByReportNameIgnoreCaseContaining(@Param("reportName") String reportName);

    @Query("select count (r) from ReportQ r")
    Long findSize();

}
