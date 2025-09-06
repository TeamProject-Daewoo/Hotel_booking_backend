package com.example.backend.common;

import java.net.URLEncoder;

public class TourApi {
    public final String KEY = "d3edf95d6c9d0b621067fbce1f7fd2521372055015a6d19f6dd61b5c9879b661";

    private String getUri(String apiType, String pageNo, String pageCount) throws Exception {
        return "http://apis.data.go.kr/B551011/KorService2/"+apiType
        + "?ServiceKey="+ URLEncoder.encode(KEY, "UTF-8")
        + "&pageNo=" + pageNo
        + "&numOfRows="+ pageCount
        + "&_type=json"
        + "&MobileOS=ETC"
        + "&MobileApp=AppTest"
        + "&contentTypeId=32";    
    }
    public String getAreaBase(String pageNo, String pageCount, String areaCode) throws Exception {
        return getUri("areaBasedList2", pageNo, pageCount)+ "&areaCode=" + areaCode;
    }
    public String getDetailUri(String pageNo, String pageCount, String contentId) throws Exception  {
        return getUri("detailInfo2", pageNo, pageCount)+"&contentId="+contentId;
    }
    public String getIntroUri(String pageNo, String pageCount, String contentId) throws Exception  {
        return getUri("detailIntro2", pageNo, pageCount)+"&contentId="+contentId;
    }
}