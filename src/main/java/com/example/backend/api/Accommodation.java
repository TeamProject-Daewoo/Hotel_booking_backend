package com.example.backend.api;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "HOTELS")
public class Accommodation {

    @Id
    private String contentid;

    private String title;
    private String addr1;
    private String tel;
    private String firstimage;
    private String firstimage2;
    private String modifiedtime;
    private String cat1;
    private String cat2;
    private String cat3;
    private String contenttypeid;
    private String mapx;
    private String mapy;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "sigungu_code")
    private String sigunguCode;

    @Column(name = "l_dong_regn_cd")
    private String lDongRegnCd;

    @Column(name = "l_dong_signgu_cd")
    private String lDongSignguCd;

    @Column(name = "lcls_systm1")
    private String lclsSystm1;

    @Column(name = "lcls_systm2")
    private String lclsSystm2;

    @Column(name = "lcls_systm3")
    private String lclsSystm3;

    public Accommodation(AccommodationDto dto) {
        this.contentid = dto.getContentid();
        this.title = dto.getTitle();
        this.addr1 = dto.getAddr1();
        this.tel = dto.getTel();
        this.firstimage = dto.getFirstimage();
        this.firstimage2 = dto.getFirstimage2();
        this.modifiedtime = dto.getModifiedtime();
        this.cat1 = dto.getCat1();
        this.cat2 = dto.getCat2();
        this.cat3 = dto.getCat3();
        this.contenttypeid = dto.getContenttypeid();
        this.mapx = dto.getMapx();
        this.mapy = dto.getMapy();
        this.areaCode = dto.getAreaCode();
        this.sigunguCode = dto.getSigunguCode();
        this.lDongRegnCd = dto.getLDongRegnCd();
        this.lDongSignguCd = dto.getLDongSignguCd();
        this.lclsSystm1 = dto.getLclsSystm1();
        this.lclsSystm2 = dto.getLclsSystm2();
        this.lclsSystm3 = dto.getLclsSystm3();
    }
}