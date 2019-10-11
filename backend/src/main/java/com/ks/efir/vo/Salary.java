package com.ks.efir.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

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

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-ММ-dd")
    @Getter @Setter
    private Date salaryDate;

    public Salary(int workerId, Date salaryDate) {
        this.worker = new Worker(workerId);
        this.salaryDate = salaryDate;
    }

    public Salary() {}

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
