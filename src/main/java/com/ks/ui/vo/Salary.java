package com.ks.ui.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "SALARY")
@EntityListeners(AuditingEntityListener.class)
public class Salary implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @NotBlank
    @Getter @Setter
    private Worker worker;

    @Getter @Setter
    private BigDecimal salary;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Getter @Setter
    private Date createdDate;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("worker", worker)
                .append("salary", salary)
                .append("createdDate", createdDate)
                .toString();
    }
}
