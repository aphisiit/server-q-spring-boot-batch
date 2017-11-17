package server.q.serverq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import server.q.serverq.entity.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long>,JpaSpecificationExecutor<CustomerRepository>,PagingAndSortingRepository<Customer,Long>{

    List<Customer> findByCountryIgnoreCaseContaining(@Param("country") String city);
}
