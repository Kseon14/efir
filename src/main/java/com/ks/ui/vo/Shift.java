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
@Table(name = "SHIFT")
@EntityListeners(AuditingEntityListener.class)
public class Shift implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @NotBlank
    @Getter @Setter
    private Worker worker;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Getter @Setter
    private Date createdDate;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @Getter @Setter
    private Date shiftDate;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("worker", worker)
                .append("createdDate", createdDate)
                .append("shiftDate", shiftDate)
                .toString();
    }
}
