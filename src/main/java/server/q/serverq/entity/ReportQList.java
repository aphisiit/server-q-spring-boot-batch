package server.q.serverq.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import server.q.serverq.repository.ReportQListRepository;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class ReportQList {

    private @Id
    @GeneratedValue(strategy = GenerationType.AUTO) Long id;

    @Setter
    @Getter
    private String reportName;

    @Getter @Setter
    private String status;

    @Setter @Getter
    private String criteriaString;

    @Getter @Setter
    private @JsonFormat(pattern = "yyyy-mm-dd HH:MM:ss")
    Timestamp createdDate;

    @Getter @Setter
    private @JsonFormat(pattern = "yyyy-mm-dd HH:MM:ss")
    Timestamp finishDate;

    @Setter @Getter
    private String operation;

    @PrePersist
    public void preInsert(){
        audit("INSERT");
        setCreatedDate(new Timestamp(System.currentTimeMillis()));
    }

    @PreUpdate
    public void preUpdate(){
        audit("UPDATE");
        setFinishDate(new Timestamp(System.currentTimeMillis()));
    }

    @PreRemove
    public void preRemove(){

    }

    private void audit(String operation){
        setOperation(operation);
    }

}
