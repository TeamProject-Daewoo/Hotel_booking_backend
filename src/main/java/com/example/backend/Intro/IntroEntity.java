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

    // --- 모든 필드를 String 또는 DB 타입에 맞게 수정 ---
    private String contentid;
    private String contenttypeid;
    private String roomcount; // Integer -> String
    private String roomtype;
    private String refundregulation;
    private String checkintime;
    private String checkouttime;
    private String chkcooking;
    private String seminar; // Integer -> String
    private String sports; // Integer -> String
    private String sauna; // Integer -> String
    private String subfacility;
    private String foodplace;
    private String reservationurl;
    private String pickup;
    private String infocenterlodging;
    private String parkinglodging;
    private String reservationlodging;
    private String scalelodging;
    private String accomcountlodging; // Integer -> String

    public IntroEntity(Item itemDto) {
        // DTO의 모든 필드는 String이므로, 그대로 복사
        BeanUtils.copyProperties(itemDto, this);
    }
}