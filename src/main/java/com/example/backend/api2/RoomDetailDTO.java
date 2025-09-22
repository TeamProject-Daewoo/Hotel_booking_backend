package com.example.backend.api2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetailDTO {
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
    private Integer finalPrice;
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
    
    public static RoomDetailDTO fromEntity(Detail entity, Integer finalPrice) {
        return RoomDetailDTO.builder()
                .id(entity.getId())
                .contentid(entity.getContentid())
                .roomcode(entity.getRoomcode())
                .roomtitle(entity.getRoomtitle())
                .roomsize1(entity.getRoomsize1())
                .roomcount(entity.getRoomcount())
                .roombasecount(entity.getRoombasecount())
                .roommaxcount(entity.getRoommaxcount())
                .roomoffseasonminfee1(entity.getRoomoffseasonminfee1())
                .roomoffseasonminfee2(entity.getRoomoffseasonminfee2())
                .roompeakseasonminfee1(entity.getRoompeakseasonminfee1())
                .roompeakseasonminfee2(entity.getRoompeakseasonminfee2())
                .finalPrice(finalPrice)
                .roomintro(entity.getRoomintro())
                .roombathfacility(entity.getRoombathfacility())
                .roombath(entity.getRoombath())
                .roomhometheater(entity.getRoomhometheater())
                .roomaircondition(entity.getRoomaircondition())
                .roomtv(entity.getRoomtv())
                .roompc(entity.getRoompc())
                .roomcable(entity.getRoomcable())
                .roominternet(entity.getRoominternet())
                .roomrefrigerator(entity.getRoomrefrigerator())
                .roomtoiletries(entity.getRoomtoiletries())
                .roomsofa(entity.getRoomsofa())
                .roomcook(entity.getRoomcook())
                .roomtable(entity.getRoomtable())
                .roomhairdryer(entity.getRoomhairdryer())
                .roomsize2(entity.getRoomsize2())
                .roomimg1(entity.getRoomimg1())
                .roomimg2(entity.getRoomimg2())
                .roomimg3(entity.getRoomimg3())
                .roomimg4(entity.getRoomimg4())
                .roomimg5(entity.getRoomimg5())
                .build();
    }
}
