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
public class Hotels {
    private Long id;
    
    @Id
    private String contentid;
    
    private String title;
    private String addr1;
    private String tel;
    private String firstimage;
    private Integer areaCode;
    private Integer sigunguCode;
    private String category;
    private String mapx;
    private String mapy;
    private String business_registration_number;
}