package com.dash.complaints.entity;



import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
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

    public Complaint(String nameAr , String nameEn){
        this.nameAr = nameAr;
        this.nameEn =  nameEn;
    }




}
