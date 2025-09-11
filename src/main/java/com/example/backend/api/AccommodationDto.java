package com.example.backend.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccommodationDto {

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

    public void setlDongRegnCd(String lDongRegnCd) {
        this.lDongRegnCd = lDongRegnCd;
    }

    public void setlDongSignguCd(String lDongSignguCd) {
        this.lDongSignguCd = lDongSignguCd;
    }
}