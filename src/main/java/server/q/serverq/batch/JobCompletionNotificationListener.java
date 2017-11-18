package server.q.serverq.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import server.q.serverq.entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    @SuppressWarnings("Duplicates")
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
//            LOGGER.info("!!! JOB FINISHED! Time to verify the results");
//
//            List<Customer> results = jdbcTemplate.query("SELECT [id],[first_name],[last_name],[city],[country],[phone] FROM [Customer]", new RowMapper<Customer>() {
//                @Override
//                public Customer mapRow(ResultSet rs, int row) throws SQLException {
//                    return new Customer(rs.getLong("id"),
//                            rs.getString("first_name"),
//                            rs.getString("last_name"),
//                            rs.getString("city"),
//                            rs.getString("country"),
//                            rs.getString("phone"));
//                }
//            });
//
//            for (Customer person : results) {
//                LOGGER.info("Found <" + person + "> in the database.");
//            }
//
//        }
//    }

//    @SuppressWarnings("Duplicates")
//    @Override
//    public void beforeJob(JobExecution jobExecution) {
//        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
//            LOGGER.info("!!! J! Time to verify the results");
//
//            List<Customer> results = jdbcTemplate.query("SELECT first_name, last_name FROM people", new RowMapper<Customer>() {
//                @Override
//                public Customer mapRow(ResultSet rs, int row) throws SQLException {
//                    return new Customer(rs.getLong("id"),
//                            rs.getString("firstName"),
//                            rs.getString("lastName"),
//                            rs.getString("city"),
//                            rs.getString("country"),
//                            rs.getString("phone"));
//                }
//            });
//
//            for (Customer person : results) {
//                LOGGER.info("Found <" + person + "> in the database.");
//            }
//
//        }
//    }
}
