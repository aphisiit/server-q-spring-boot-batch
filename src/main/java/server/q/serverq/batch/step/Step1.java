//package server.q.serverq.batch.step;
//
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.database.JdbcCursorItemReader;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.RowMapper;
//import server.q.serverq.entity.Customer;
//
//import javax.sql.DataSource;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Map;
//
//@Configuration
//public class Step1 {
//
//    @Bean
//    public ItemReader<Customer> jdbcReader(DataSource dataSource){
//
//        JdbcCursorItemReader<Customer> jdbcCursorItemReader = new JdbcCursorItemReader<>();
//        jdbcCursorItemReader.setDataSource(dataSource);
//        jdbcCursorItemReader.setName("jdbc-reader");
//        jdbcCursorItemReader.setSql("SELECT * FROM CUSOMTER");
//        jdbcCursorItemReader.setRowMapper(new RowMapper<Customer>() {
//            @Override
//            public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
//
//                Customer customer = new Customer();
//                customer.setPhone(resultSet.getString("phone"));
//                customer.setLastName(resultSet.getString("lastName"));
//                customer.setFirstName(resultSet.getString("firstName"));
//                customer.setId(resultSet.getLong("id"));
//                customer.setCity(resultSet.getString("city"));
//                customer.setCountry(resultSet.getString("country"));
//                return customer;
//            }
//        });
//
//        return jdbcCursorItemReader;
//    }
//}
