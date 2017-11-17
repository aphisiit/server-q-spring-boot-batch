package server.q.serverq.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

//import static server.q.serverq.controller.PrintQController.generateReport;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class ReportQ {

    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long id;

    @Setter @Getter
    private String reportName;

    @Getter @Setter
    private String status;

    @Setter @Getter
    private String criteriaString;

    @Getter @Setter
    private @JsonFormat(pattern = "yyyy-mm-dd HH:MM:ss") Timestamp createdDate;

    @Setter @Getter
    private String operation;

    @PrePersist
    public void preInsert(){
        audit("INSERT");
        auditTime(new Timestamp(System.currentTimeMillis()));
    }

    @PreUpdate
    public void preUpdate(){
        audit("UPDATE");
        auditTime(new Timestamp(System.currentTimeMillis()));
    }

    @PreRemove
    public void preRemove(){
    }

    private void audit(String operation){
        setOperation(operation);
    }

    private void auditTime(Timestamp timestamp){
        setCreatedDate(timestamp);
    }
}
