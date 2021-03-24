package com.quality.complaints.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Complaint {
    @Id
    @SequenceGenerator(name="COMP_SEQ",sequenceName="COMP_SEQ", allocationSize=1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="COMP_SEQ")
    private Integer id;


    private String nameAr;
    private String nameEn;
    private String mobile;
    private String description;
    private Integer numOfAttaches;
    private Long voiceFileId;
    private Date creationDate;
    private Integer statusCode;
    private String changedBy;
    private Date changeDate;
    private Integer numberOfChanges;

    public Complaint(String description) {
        this.description =description;
    }
    public Complaint(){

    }
}
