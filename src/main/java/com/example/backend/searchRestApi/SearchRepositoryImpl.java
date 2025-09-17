package com.example.backend.searchRestApi;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.backend.Intro.QIntro;
import com.example.backend.api.QHotels;
import com.example.backend.api2.QDetail;
import com.example.backend.reservation.QReservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Repository;

@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QHotels hotels = QHotels.hotels;
    private final QDetail rooms = QDetail.detail;
    private final QIntro intro = QIntro.intro;
    private final QReservation reservation = QReservation.reservation;
    
    private final static int CATEGORY_COUNT = 4;
    private final String RESERVATION_COUNT_ALIAS = "reservationCount";

    public SearchRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public SearchResponseDto findBySearchElements(SearchRequestDto searchRequest) {


        String specificContentId = "137913";

        Tuple result = queryFactory
            .select(
                hotels.title,
                intro.subfacility, // [확인 1] DB에 저장된 subfacility 원본 문자열
                
                getIntroAmenitiesCount().min().as("hotelAmenitiesCalculated"), // [확인 2] 호텔 편의시설 계산 결과
                
                getRoomAmenitiesCount().sum().as("roomAmenitiesCalculated"), // [확인 3] 객실 편의시설 합산 결과
                
                getIntroAmenitiesCount().min()
                    .add(getRoomAmenitiesCount().sum())
                    .as("totalCalculated") // [확인 4] 최종 합계
            )
            .from(hotels)
            .leftJoin(rooms).on(hotels.contentid.eq(rooms.contentid))
            .leftJoin(intro).on(hotels.contentid.eq(intro.contentid))
            .where(hotels.contentid.eq(specificContentId)) // 특정 호텔 하나만 조회
            .groupBy(hotels.contentid, hotels.title, intro.subfacility)
            .fetchOne();

        // 조회된 결과를 출력해서 확인
        System.out.println("Result for Hotel ID " + specificContentId + ": " + result);

        
        // DTO에서 Date를 받아서 LocalDate로 변환
        LocalDate checkInDate = searchRequest.getCheckInDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
        LocalDate checkOutDate = searchRequest.getCheckOutDate().toInstant()
                                     .atZone(ZoneId.systemDefault())
                                     .toLocalDate();
        BooleanBuilder commonCondition = getCommonConditions(searchRequest, checkInDate, checkOutDate);
   
        QReservation reservationSub = new QReservation("reservationSub");
        //카드에 표시될 내용에 대한 쿼리
        List<SearchCardDto> card = queryFactory
            .select(Projections.fields(SearchCardDto.class,
                    hotels.contentid.as("contentId"),
                    hotels.title.as("title"),
                    hotels.firstimage.as("image"),
                    rooms.roomoffseasonminfee1.min().as("price"),
                    hotels.addr1.as("address"),
                    Expressions.as(
                        JPAExpressions
                            .select(Wildcard.count)
                            .from(reservationSub)
                            .where(reservationSub.hotel.contentid.eq(hotels.contentid)),
                            RESERVATION_COUNT_ALIAS
                    ),
                    hotels.mapx.as("mapX"),
                    hotels.mapy.as("mapY"),
                    getIntroAmenitiesCount().min()
                        .add(getRoomAmenitiesCount().sum())
                        .as("totalAminities") 
                )
            )
            .from(hotels)
            .leftJoin(rooms).on(hotels.contentid.eq(rooms.contentid))
            .leftJoin(intro).on(hotels.contentid.eq(intro.contentid)) 
            // .leftJoin(reservation).on(reservation.hotel.contentid.eq(hotels.contentid))
            .where(commonCondition, categorySelectCondition(searchRequest.getCategory()))
            .groupBy(hotels.contentid, hotels.title, hotels.firstimage, hotels.addr1)
            .orderBy(orderCondition(searchRequest.getOrder(), RESERVATION_COUNT_ALIAS)) // 2. orderBy 부분에도 상수 전달
            .fetch();

        //카테고리별 개수 반환 쿼리
        List<Tuple> counts = queryFactory
            .select(
                hotels.contentid.countDistinct(),
                hotels.category
            )
            .from(hotels)
            .join(rooms).on(hotels.contentid.eq(rooms.contentid))
            .join(intro).on(hotels.contentid.eq(intro.contentid))
            .where(commonCondition)
            .groupBy(hotels.category)
            .fetch();

        //전체 합계 반환 쿼리
        long totalCount = queryFactory
            .select(hotels.contentid.countDistinct())
            .from(hotels)
            .join(rooms).on(hotels.contentid.eq(rooms.contentid))
            .join(intro).on(hotels.contentid.eq(intro.contentid))
            .where(commonCondition)
            .fetchOne();

        SearchResponseDto responseDto = new SearchResponseDto();
        if (card != null)
            responseDto.setSearchCards(card);

        int[] countArr = new int[CATEGORY_COUNT];
        for (Tuple t : counts) {
            String category = t.get(hotels.category);
            Long count = t.get(hotels.contentid.countDistinct());
            //카테고리중 세 가지만 구분
            int idx = switch (category) {
                case "B02010100" -> 1;  //호텔
                case "B02010900" -> 2;  //모텔
                case "B02010700" -> 3;  //펜션
                default -> -1;
            };
            if(idx != -1) countArr[idx] = count.intValue();
        }
        //프론트에 표시될 순서 보장 + LinkedHashMap
        String[] categorys = {"All", "Hotels", "Motels", "Cottages"};
        countArr[0] = (int)totalCount;
        for (int i = 0; i < CATEGORY_COUNT; i++)
            responseDto.getCounts().put(categorys[i], countArr[i]);
        
        return responseDto;
    }

    //검색+필터링 조건
    private BooleanBuilder getCommonConditions(SearchRequestDto searchRequest, LocalDate checkInDate, LocalDate checkOutDate) {
            BooleanBuilder builder = new BooleanBuilder();
            //키워드 검색
            BooleanExpression keywordExpr = keywordCondition(searchRequest.getKeyword());
            if (keywordExpr != null) {
                builder.and(keywordExpr);
            }
            //비용 0 제외
            builder.and(rooms.roomoffseasonminfee1.ne(0));
            //이미지 없는 목록 제외
            builder.and(hotels.firstimage.isNotEmpty());
            //체크인, 체크아웃 기간에 예약 일정 없는지 체크
            builder.and(availableDateCondition(checkInDate, checkOutDate));
            //객실과 인원 수 숙박 충분한지 체크
            builder.and(rooms.roomcount.goe(searchRequest.getRoomCount()));
            builder.and(rooms.roommaxcount.goe(searchRequest.getGuestCount()));
            //비용 필터
            builder.and(rooms.roomoffseasonminfee1.between(searchRequest.getMinPrice(), searchRequest.getMaxPrice()));
            //필터링
            builder.and(filterAmenitiesCondition(searchRequest.getAmenities()));
            builder.and(filterFreebiesCondition(searchRequest.getFreebies()));

            return builder;
    }

    private BooleanExpression categorySelectCondition(String category) {
        return switch (category) {
            case "Hotels" -> hotels.category.eq("B02010100");
            case "Motels" -> hotels.category.eq("B02010900");
            case "Cottages" -> hotels.category.eq("B02010700");
            default -> null;
        };
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
                StringPath path = switch (key) {
                case "욕실" -> rooms.roombathfacility;
                case "욕조" -> rooms.roombath;
                case "세면도구제공" -> rooms.roomtoiletries;
                case "홈시어터" -> rooms.roomhometheater;
                case "에어컨" -> rooms.roomaircondition;
                case "Tv" -> rooms.roomtv;
                case "Pc" -> rooms.roompc;
                case "Wifi" -> rooms.roominternet;
                case "냉장고" -> rooms.roomrefrigerator;
                case "취사가능" -> rooms.roomcook;
                default -> null;
                };
                if(path != null) builder.and(path.eq("Y"));
            }
        }

        return builder;
    }

    private OrderSpecifier<?>[] orderCondition(String order, String countAlias) {
        List<OrderSpecifier<?>> specifiers = new ArrayList<>();
    
        NumberPath<Long> countPath = Expressions.numberPath(Long.class, countAlias);

        OrderSpecifier<?> orderSpecifier = switch (order) {
            case "인기 순" -> countPath.desc();
            case "낮은 가격 순" -> rooms.roomoffseasonminfee1.min().asc();
            case "높은 가격 순" -> rooms.roomoffseasonminfee1.min().desc();
            default -> null;
        };
        
        if (orderSpecifier != null) {
            specifiers.add(orderSpecifier);
        }
        
        return specifiers.toArray(new OrderSpecifier[0]);
    }
    private NumberExpression<Integer> getIntroAmenitiesCount() {
        StringExpression subfacilityReplaced = Expressions.stringTemplate(
        "REPLACE({0}, {1}, {2})",
        intro.subfacility, " / ", ", "
    );
    // 통일된 구분자 ", "를 모두 제거
    StringExpression commaDeleted = Expressions.stringTemplate(
        "REPLACE({0}, {1}, {2})",
        subfacilityReplaced, ", ", ""
    );

    // 원본 길이에서 제거된 후의 길이를 빼서, 구분자들이 차지했던 총 길이를 구함
    NumberExpression<Integer> totalSeparatorLength = subfacilityReplaced.length()
        .subtract(commaDeleted.length());

    // ", "는 2글자이므로, 총 길이 차이를 2로 나누어 구분자의 '개수'를 구함
    NumberExpression<Integer> separatorCount = totalSeparatorLength.divide(2);

    // 아이템 총 개수 = 구분자 개수 + 1
    NumberExpression<Integer> subfacilityCount = Expressions.cases()
        .when(intro.subfacility.isNull().or(intro.subfacility.isEmpty())).then(0)
        .otherwise(separatorCount.add(1));


    // --- 2. paths 배열 중복 제거 및 계산 ---
    
    // parkinglodging 중복 제거
    StringPath[] paths = {
        intro.parkinglodging, intro.publicbath, 
        intro.seminar, intro.sports, intro.barbecue, intro.campfire,
        intro.sauna, intro.fitness
    };
    
    NumberExpression<Integer> counts = Expressions.asNumber(0);
    for (StringPath path : paths) {
        NumberExpression<Integer> currentPathCount = Expressions.cases()
            .when(path.eq("1").or(path.equalsIgnoreCase("Y"))).then(1) // Y/N도 처리 가능하도록 수정
            .otherwise(0);
        counts = counts.add(currentPathCount);
    }
    
    // 최종적으로 subfacility 개수와 나머지 개수를 더해서 반환
    return subfacilityCount.add(counts);
    }
    private NumberExpression<Integer> getRoomAmenitiesCount() {
        NumberExpression<Integer> roomAmenitiesCount = Expressions.asNumber(0);
        // rooms 테이블의 편의시설 컬럼만 포함
        StringPath[] paths = { 
            rooms.roombath, rooms.roomtv, rooms.roomaircondition, rooms.roomcook, 
            rooms.roominternet, rooms.roomrefrigerator, rooms.roomtoiletries, 
            rooms.roomhometheater, rooms.roompc 
        };

        for (StringPath path : paths) {
            NumberExpression<Integer> currentPathCount = Expressions.cases()
                .when(path.eq("Y")).then(1)
                .otherwise(0);
            roomAmenitiesCount = roomAmenitiesCount.add(currentPathCount);
        }
        return roomAmenitiesCount;
    }
    
}