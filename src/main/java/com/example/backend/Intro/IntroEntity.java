package com.example.backend.Intro;

import com.example.backend.Intro.IntroResponseDTO.Response.Body.Items.Item;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "HOTEL_INTRO")
public class IntroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Item DTO를 Entity로 변환하는 생성자
    public IntroEntity(Item itemDto) {
        BeanUtils.copyProperties(itemDto, this);
    }

    private String contentid;
    private String contenttypeid;
    private String roomcount;
    private String roomtype;
    private String refundregulation;
    private String checkintime;
    private String checkouttime;
    private String chkcooking;
    private String seminar;
    private String sports;
    private String sauna;
    private String beauty;
    private String beverage;
    private String karaoke;
    private String barbecue;
    private String campfire;
    private String bicycle;
    private String fitness;
    private String publicpc;
    private String publicbath;
    private String subfacility;
    private String foodplace;
    private String reservationurl;
    private String pickup;
    private String infocenterlodging;
    private String parkinglodging;
    private String reservationlodging;
    private String scalelodging;
    private String accomcountlodging;
}