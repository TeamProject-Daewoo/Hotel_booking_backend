package com.example.backend.searchRestApi;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.backend.Intro.QIntro;
import com.example.backend.api.QHotels;
import com.example.backend.api2.QDetail;
import com.example.backend.reservation.QReservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final QHotels hotels = QHotels.hotels;
    private final QDetail rooms = QDetail.detail;
    private final QIntro intro = QIntro.intro;
    private final QReservation reservation = QReservation.reservation;

    public SearchRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<SearchResponseDto> findBySearchElements(SearchRequestDto searchRequest) {
        
        // DTO에서 Date를 받아서 LocalDate로 변환
        LocalDate checkInDate = searchRequest.getCheckInDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
        LocalDate checkOutDate = searchRequest.getCheckOutDate().toInstant()
                                     .atZone(ZoneId.systemDefault())
                                     .toLocalDate();
        
        return queryFactory
            .select(
                Projections.fields(
                    SearchResponseDto.class,
                    hotels.contentid.as("contentId"),
                    hotels.title.as("title"),
                    hotels.firstimage.as("image"),
                    rooms.roomoffseasonminfee1.min().as("price"),
                    hotels.addr1.as("address")
                )
            )
            .distinct()
            .from(hotels)
            .join(rooms).on(hotels.contentid.eq(rooms.contentid))
            .join(intro).on(hotels.contentid.eq(intro.contentid))
            .groupBy(hotels.contentid)
            .where(
                //키워드 검색
                keywordCondition(searchRequest.getKeyword()),      
                //비용 0 제외
                rooms.roomoffseasonminfee1.ne(0),      
                //이미지 없는 목록 제외
                hotels.firstimage.isNotEmpty(),
                //체크인, 체크아웃 기간에 예약 일정 없는지 체크
                //availableDateCondition(checkInDate, checkOutDate),
                //객실과 인원 수 숙박 충분한지 체크
                rooms.roomcount.goe(searchRequest.getRoomCount()),
                rooms.roommaxcount.goe(searchRequest.getGuestCount()),
                //비용 필터
                rooms.roomoffseasonminfee1.between(searchRequest.getMinPrice(), searchRequest.getMaxPrice()),
                //필터링
                filterAmenitiesCondition(searchRequest.getAmenities()),
                filterFreebiesCondition(searchRequest.getFreebies())
             )
            .fetch();
    }

    private BooleanExpression keywordCondition(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return hotels.addr1.containsIgnoreCase(keyword)
            .or(hotels.title.containsIgnoreCase(keyword));
    }

    private BooleanExpression availableDateCondition(LocalDate checkInDate, LocalDate checkOutDate) {
        return JPAExpressions.selectOne()
            .from(reservation)
            .where(
                reservation.hotel.contentid.eq(hotels.contentid)
                .and(reservation.status.eq("PAID"))
                .and(
                    // 기존 예약의 체크아웃 날짜가 새로운 체크인 날짜보다 뒤에 있고,
                    // 기존 예약의 체크인 날짜가 새로운 체크아웃 날짜보다 앞에 있는 경우 (겹치는 조건)
                    reservation.checkOutDate.gt(checkInDate).and(
                        reservation.checkInDate.lt(checkOutDate)
                    )
                )
            )
            .exists() // 겹치는 예약이 있으면 true
            .not();
    }
    private BooleanBuilder filterAmenitiesCondition(Map<String, Boolean> amenities) {
        BooleanBuilder builder = new BooleanBuilder();

        String[] sportsKeywords = {"스포츠", "운동", "농구", "족구", "축구", "골프", "테니스"};
        String[] restKeywords = {"휴게실", "라운지", "카페", "정원", "테라스", "평상", "루프탑"};
        // Map을 순회하며 true로 설정된 필터 조건들을 쿼리에 추가
        for (String key : amenities.keySet()) {
            
            // 값이 true인 경우에만 조건을 추가
            if (Boolean.TRUE.equals(amenities.get(key))) {
                switch (key) {
                    case "주차가능":
                        builder.and(intro.parkinglodging.containsIgnoreCase("가능")
                                        .or(intro.parkinglodging.containsIgnoreCase("있음")));
                        break;
                    case "수영장":
                        builder.and(intro.subfacility.containsIgnoreCase("수영장")
                                        .or(intro.subfacility.containsIgnoreCase("물놀이장"))
                                        .or(intro.subfacility.containsIgnoreCase("야외풀장")));
                        break;
                    case "세미나":
                        builder.and(intro.subfacility.containsIgnoreCase("세미나")
                                        .or(intro.seminar.eq("1")));
                        break;
                    case "스포츠시설":
                        BooleanBuilder sportsConditions = new BooleanBuilder();
                        for (String keyword : sportsKeywords)
                            sportsConditions.or(intro.subfacility.containsIgnoreCase(keyword));
                        builder.and(sportsConditions);
                        break;
                    case "바베큐":
                        builder.and(intro.subfacility.containsIgnoreCase("바베큐")
                                        .or(intro.subfacility.containsIgnoreCase("바비큐"))
                                        .or(intro.barbecue.eq("1")));
                    case "캠프파이어":
                        builder.and(intro.subfacility.containsIgnoreCase("캠프파이어")
                                        .or(intro.campfire.eq("1")));
                        break;
                    case "휴게시설":
                        BooleanBuilder restConditions = new BooleanBuilder();
                        for (String keyword : restKeywords)
                            restConditions.or(intro.subfacility.containsIgnoreCase(keyword));
                        builder.and(restConditions);
                        break;
                    case "사우나":
                        builder.and(intro.subfacility.containsIgnoreCase("사우나")
                                        .or(intro.subfacility.containsIgnoreCase("찜질방"))
                                        .or(intro.subfacility.containsIgnoreCase("황토방"))
                                        .or(intro.sauna.containsIgnoreCase("1")));
                        break;
                    case "피트니스":
                        builder.and(intro.subfacility.containsIgnoreCase("피트니스")
                                        .or(intro.fitness.containsIgnoreCase("1")));
                        break;
                    case "계곡":
                        builder.and(intro.subfacility.containsIgnoreCase("계곡"));
                        break;
                    case "대중탕":
                        builder.and(intro.publicbath.containsIgnoreCase("1"));
                        break;
                    default:
                        // 정의되지 않은 키는 무시
                        break;
                }
            }
        }
        
        return builder;
    }
    private BooleanBuilder filterFreebiesCondition(Map<String, Boolean> freebies) {
        BooleanBuilder builder = new BooleanBuilder();

        for (String key : freebies.keySet()) {
            if (Boolean.TRUE.equals(freebies.get(key))) {
                switch (key) {
                    case "욕실":
                        builder.and(rooms.roombathfacility.eq("Y"));
                        break;
                    case "욕조":
                        builder.and(rooms.roombath.eq("Y"));
                        break;
                    case "세면도구제공":
                        builder.and(rooms.roomtoiletries.eq("Y"));
                        break;
                    case "홈시어터":
                        builder.and(rooms.roomhometheater.eq("Y"));
                        break;
                    case "에어컨":
                        builder.and(rooms.roomaircondition.eq("Y"));
                        break;
                    case "Tv":
                        builder.and(rooms.roomtv.eq("Y"));
                        break;
                    case "Pc":
                        builder.and(rooms.roompc.eq("Y"));
                        break;
                    case "Wifi":
                        builder.and(rooms.roominternet.eq("Y"));
                        break;
                    case "냉장고":
                        builder.and(rooms.roomrefrigerator.eq("Y"));
                        break;
                    case "취사가능":
                        builder.and(rooms.roomcook.eq("Y"));
                        break;   
                    default:
                        break;
                }
            }
        }

        return builder;
    }
    
}