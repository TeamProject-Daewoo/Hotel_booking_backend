package com.example.backend.api2;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROOMS")
public class DetailDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomoffseasonminfee1;
    private String roomimg4;
    private String roomtoiletries;
    private String roomsofa;
    private String roomcook;
    private String roomtable;
    private String roomimg5alt;
    private String contentid;
    private String contenttypeid;
    private String fldgubun;
    private String infoname;
    private String infotext;
    private String serialnum;
    private String subcontentid;
    private String subdetailalt;
    private String subdetailimg;
    private String subdetailoverview;
    private String subname;
    private String subnum;
    private String roomcode;
    private String roomtitle;
    private String roomsize1;
    private String roomcount;
    private String roombasecount;
    private String roommaxcount;
    private String roomoffseasonminfee2;
    private String roompeakseasonminfee1;
    private String roompeakseasonminfee2;
    private String roomintro;
    private String roombathfacility;
    private String roombath;
    private String roomhometheater;
    private String roomaircondition;
    private String roomtv;
    private String roompc;
    private String roomcable;
    private String roominternet;
    private String roomrefrigerator;
    private String roomimg5;
    private String roomimg3;
    private String roomimg4alt;
    private String roomimg3alt;
    private String roomhairdryer;
    private String roomsize2;
    private String roomimg2alt;
    private String roomimg1;
    private String roomimg1alt;
    private String roomimg2;
    private String cpyrhtDivCd1;
    private String cpyrhtDivCd2;
    private String cpyrhtDivCd3;
    private String cpyrhtDivCd4;
    private String cpyrhtDivCd5;
}