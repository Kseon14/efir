package com.ks.ui.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "WORKER")
@EntityListeners(AuditingEntityListener.class)
public class Worker implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @NotBlank
    @Getter @Setter
    private String firstName;

    @NotBlank
    @Getter @Setter
    private String lastName;

    @Getter @Setter
    private BigDecimal baseSalary;

    @Getter @Setter
    private WorkerStatus status;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("baseSalary", baseSalary)
                .append("status", status)
                .toString();
    }
}
