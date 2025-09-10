package com.example.backend.Intro;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntroResponseDTO {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;

        @Data
        public static class Header {
            private String resultCode;
            private String resultMsg;
        }

        @Data
        public static class Body {
            private Items items;
            private Integer numOfRows;
            private Integer pageNo;
            private Integer totalCount;

            @Data
            public static class Items {
                private List<Item> item;

                @Data
                public static class Item {
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
            }
        }
    }
}