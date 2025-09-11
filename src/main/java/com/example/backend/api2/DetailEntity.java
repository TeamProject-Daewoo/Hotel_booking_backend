package com.example.backend.api2;

import com.example.backend.api2.DetailResponseDTO1.Body.Items.Item;
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
@Table(name = "ROOMS")
public class DetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer roomoffseasonminfee1;
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
    private Integer roomcount; 
    private Integer roombasecount; 
    private Integer roommaxcount;
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
    
    // Item DTO를 Entity로 변환하는 생성자
    public DetailEntity(Item itemDto) {
        // 모든 String 필드는 그대로 복사
        BeanUtils.copyProperties(itemDto, this,
            "roomoffseasonminfee1", "roomcount", "roombasecount", "roommaxcount",
            "roomoffseasonminfee2", "roompeakseasonminfee1", "roompeakseasonminfee2");

        // String -> Integer 변환 필요한 필드만 수동으로 처리
        this.roomoffseasonminfee1 = safeParseInt(itemDto.getRoomoffseasonminfee1());
        this.roomcount = safeParseInt(itemDto.getRoomcount());
        this.roombasecount = safeParseInt(itemDto.getRoombasecount());
        this.roommaxcount = safeParseInt(itemDto.getRoommaxcount());
        this.roomoffseasonminfee2 = safeParseInt(itemDto.getRoomoffseasonminfee2());
        this.roompeakseasonminfee1 = safeParseInt(itemDto.getRoompeakseasonminfee1());
        this.roompeakseasonminfee2 = safeParseInt(itemDto.getRoompeakseasonminfee2());
    }

    private Integer safeParseInt(String s) {
        if (s == null || s.trim().isEmpty()) {
            return 0; // 혹은 null을 원하시면 null 반환
        }
        try {
            // 콤마(,)가 포함된 숫자 문자열도 처리
            return Integer.parseInt(s.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 0; // 혹은 null
        }
    }
}