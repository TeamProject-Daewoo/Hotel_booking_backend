// package com.example.backend.api;
// import com.example.backend.common.TourApi;
// import lombok.RequiredArgsConstructor;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// @Component
// @RequiredArgsConstructor
// public class HotelInitializer implements CommandLineRunner {

//     private final HotelsService accommodationService;
//     private final HotelsRepa hotelsRepa;

//     @Override
//     public void run(String... args) throws Exception {
//         long count = hotelsRepa.count();
//         if(count == 0) {
//             System.out.println(">> 데이터 초기화를 시작합니다...");

//             TourApi API = new TourApi();
//             String uri = API.getAreaBase("1", "100", "");

//             // 서비스 호출
//             accommodationService.getAccommodations(uri);

//             System.out.println(">> 데이터 초기화가 완료되었습니다.");
     
//         }
//     }
// }
