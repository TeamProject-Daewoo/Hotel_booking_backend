package com.example.backend.api2;

import lombok.Data;

@Data
public class DetailRequestDto {
    private String mobileOS;
    private String mobileApp;
    private String _type;
    private String contentId;
    private String contentTypeId;
    private Integer numOfRows;
    private Integer pageNo;
    private String serviceKey;
}

