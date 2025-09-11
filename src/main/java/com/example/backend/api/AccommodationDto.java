package com.example.backend.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "HOTELS")
public class AccommodationDto {
    @Id
    private String contentid;
    private String title;
    private String addr1;
    private String tel;
    private String firstimage;
    private String firstimage2;
    private String modifiedtime;
    private String lclsSystm1;
    private String lclsSystm2;
    private String lclsSystm3;
    private String areaCode;
    private String sigunguCode;
    private String cat1;
    private String cat2;
    private String cat3;
    private String lDongRegnCd;
    private String lDongSignguCd;
    private String contenttypeid;
    private String mapx;
    private String mapy;

    // Lombok이 생성하지 않는 메서드를 명시적으로 추가
    public void setlDongRegnCd(String lDongRegnCd) {
        this.lDongRegnCd = lDongRegnCd;
    }

    public void setlDongSignguCd(String lDongSignguCd) {
        this.lDongSignguCd = lDongSignguCd;
    }
}