package com.example.backend.Intro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "HOTEL_INTRO")
public class Intro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contentid;
    private String contenttypeid;
    private int roomcount;
    private String roomtype;
    private String refundregulation;
    private String checkintime;
    private String checkouttime;
    private String chkcooking;
    private int seminar;
    private int sports;
    private int sauna;
    private String subfacility;
    private String foodplace;
    private String reservationurl;
    private String pickup;
    private String infocenterlodging;
    private String parkinglodging;
    private String reservationlodging;
    private String scalelodging;
    private int accomcountlodging;
}