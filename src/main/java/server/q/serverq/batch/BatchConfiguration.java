package server.q.serverq.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import server.q.serverq.batch.item.CustomerProcessor;
import server.q.serverq.entity.Customer;
import server.q.serverq.entity.ReportQ;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @SuppressWarnings("all")
    @Autowired
    public DataSource dataSource;

//    @Bean
//    public FlatFileItemReader<Customer> reader(){
//        FlatFileItemReader<Customer> reader = new FlatFileItemReader<Customer>();
//        reader.setResource(new ClassPathResource("sample-data.csv"));
//        reader.setLineMapper(new DefaultLineMapper<Customer>() {{
//            setLineTokenizer(new DelimitedLineTokenizer() {{
//                setNames(new String[] { "id","firstName", "lastName","city","country","phone" });
//            }});
//            setFieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {{
//                setTargetType(Customer.class);
//            }});
//        }});
//        return reader;
//    }

    @Bean
    public CustomerProcessor processor(){
        return new CustomerProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> writer() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Customer>());
        writer.setSql("SET IDENTITY_INSERT Customer ON\n" +
                "INSERT INTO [Customer] ([id],[first_name], [last_name],[city],[country],[phone]) VALUES (:id,:firstName, :lastName,:city,:country,:phone)\n" +
                "SET IDENTITY_INSERT Customer OFF");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public JdbcCursorItemReader<ReportQ> reader(){
        JdbcCursorItemReader<ReportQ> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM [REPORTQ]"); // WHERE [CITY] like concat('%',@P01,'%')");
//        reader.setPreparedStatementSetter(new PreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement preparedStatement) throws SQLException {
//                preparedStatement.setString(0,"us");
//            }
//        });

//        reader.setRowMapper(new RowMapper<Customer>() {
//            @Override
//            public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
//                return new Customer(resultSet.getLong("id"),
//                        resultSet.getString("first_name"),
//                        resultSet.getString("last_name"),
//                        resultSet.getString("city"),
//                        resultSet.getString("country"),
//                        resultSet.getString("phone"));
//            }
//        });
        reader.setRowMapper(new BeanPropertyRowMapper<>(ReportQ.class));

        return reader;
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importCustomerJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<ReportQ, ReportQ> chunk(10)
                .reader(reader())
                .processor(processor())
//                .writer(writer())
                .build();
    }

}
