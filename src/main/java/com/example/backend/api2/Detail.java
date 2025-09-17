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
public class Detail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contentid;
    private String roomcode;
    private String roomtitle;
    private String roomsize1;
    private Integer roomcount;
    private Integer roombasecount;
    private Integer roommaxcount;
    private Integer roomoffseasonminfee1;
    private Integer roomoffseasonminfee2;
    private Integer roompeakseasonminfee1;
    private Integer roompeakseasonminfee2;
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
    private String roomtoiletries;
    private String roomsofa;
    private String roomcook;
    private String roomtable;
    private String roomhairdryer;
    private String roomsize2;
    private String roomimg1;
    private String roomimg2;
    private String roomimg4;
    private String roomimg3;
    private String roomimg5;
}