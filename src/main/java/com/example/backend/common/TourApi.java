package com.example.backend.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

import org.hibernate.boot.cfgxml.internal.ConfigLoader;

public class TourApi {
    public static final String KEY;

    static {
        Properties properties = new Properties();
        // 클래스패스에서 파일을 읽어옵니다.
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("경로 못찾음");
            }
            properties.load(inputStream);
            KEY = properties.getProperty("API_KEY");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("예외 발생", e);
        }
    }

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