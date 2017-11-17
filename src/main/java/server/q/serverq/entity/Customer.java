package server.q.serverq.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import sun.util.resources.cldr.gv.LocaleNames_gv;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "customer")
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter @Getter
    private String firstName;

    @Setter @Getter
    private String lastName;

    @Setter @Getter
    private String city;

    @Setter @Getter
    private String country;

    @Setter @Getter
    private String phone;
}
