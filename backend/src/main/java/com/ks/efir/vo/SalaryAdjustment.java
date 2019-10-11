package com.ks.efir.vo;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SALARY_ADJUSTMENT")
@EntityListeners(AuditingEntityListener.class)
public class SalaryAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @NotBlank
    @Getter @Setter
    private Worker worker;

    @Getter @Setter
    private BigDecimal adjustment;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JsonIgnore
    @Getter @Setter
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-ММ-dd")
    @Getter @Setter
    private Date adjustmentDate;

    @Getter @Setter
    private String adjustmentNote;

    public SalaryAdjustment(@NotBlank int worker, Date deductionDate) {
        this.worker = new Worker(worker);
        this.adjustmentDate = deductionDate;
    }

    public SalaryAdjustment() {}

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("worker", worker)
                .append("adjustment", adjustment)
                .append("createdDate", createdDate)
                .append("adjustmentDate", adjustmentDate)
                .append("adjustmentNote", adjustmentNote)
                .toString();
    }
}
