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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.backend.Intro.QIntro;
import com.example.backend.api.QHotels;
import com.example.backend.api2.QDetail;
import com.example.backend.common.HangulUtils;
import com.example.backend.region.QRegion;
import com.example.backend.reservation.QReservation;
import com.example.backend.review.QReview;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QHotels hotels = QHotels.hotels;
    private final QDetail rooms = QDetail.detail;
    private final QIntro intro = QIntro.intro;
    private final QReservation reservation = QReservation.reservation;
    private final QRegion region = QRegion.region;
    private final QReview review = QReview.review;
    

    private final static String RESERVATION_COUNT_ALIAS = "reservationCount";

    public SearchRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public SearchResponseDto findBySearchElements(SearchRequestDto searchRequest) {
        JPAQuery<?> baseQuery = createBaseQuery(searchRequest);

        List<SearchCardDto> cards = fetchSearchCards(baseQuery.clone(), searchRequest);
        Map<String, Integer> counts = fetchCategoryCounts(baseQuery.clone());
        
        SearchResponseDto responseDto = new SearchResponseDto();
        responseDto.setSearchCards(cards);
        responseDto.setCounts(counts);
        
        return responseDto;
    }
    
    // 검색 조건에 맞는 호텔을 조회하는 공통 BaseQuery를 생성
    @Override
    public JPAQuery<?> createBaseQuery(SearchRequestDto searchRequest) {
        BooleanBuilder commonCondition = getCommonConditions(searchRequest, searchRequest.getCheckInDate(), searchRequest.getCheckOutDate());
        BooleanExpression availabilityCondition = createAvailabilityCondition(searchRequest);

        return queryFactory
            .from(hotels)
            .leftJoin(rooms).on(hotels.contentid.eq(rooms.contentid))
            .leftJoin(intro).on(hotels.contentid.eq(intro.contentid))
            .leftJoin(review).on(review.hotel.contentid.eq(hotels.contentid))
            .where(
                commonCondition
                    .and(availabilityCondition) // 예약 가능 조건
                    .and(categorySelectCondition(searchRequest.getCategory())) // 카테고리 선택 조건 O
            )
            .having(ratingGoe(searchRequest.getRating()));
    }

    @Override
    public JPAQuery<?> createCountBaseQuery(SearchRequestDto searchRequest) {
        BooleanBuilder commonCondition = getCommonConditions(searchRequest, searchRequest.getCheckInDate(), searchRequest.getCheckOutDate());
        BooleanExpression availabilityCondition = createAvailabilityCondition(searchRequest);

        return queryFactory
            .from(hotels)
            .leftJoin(rooms).on(hotels.contentid.eq(rooms.contentid))
            .leftJoin(intro).on(hotels.contentid.eq(intro.contentid))
            .leftJoin(review).on(review.hotel.contentid.eq(hotels.contentid))
            .where(
                commonCondition.and(availabilityCondition)
                // 카테고리 선택 조건 X
            )
            .having(ratingGoe(searchRequest.getRating()));
    }

    // 실제 카드 목록만 받아오는 쿼리
    @Override
    public List<SearchCardDto> fetchSearchCards(JPAQuery<?> baseQuery, SearchRequestDto searchRequest) {
        QReservation reservationSub = new QReservation("reservationSub");

        return baseQuery
            .select(Projections.fields(SearchCardDto.class,
                hotels.contentid.as("contentId"),
                hotels.title.as("title"),
                hotels.firstimage.as("image"),
                rooms.roomoffseasonminfee1.min().as("price"),
                hotels.addr1.as("address"),
                review.rating.avg().as("rating"),
                rooms.roomcount.sum().as("roomCount"),
                Expressions.as(
                    JPAExpressions
                        .select(Wildcard.count)
                        .from(reservationSub)
                        .where(reservationSub.hotel.contentid.eq(hotels.contentid)),
                    RESERVATION_COUNT_ALIAS
                ),
                review.reviewId.countDistinct().as("totalReviews"),
                hotels.mapx.as("mapX"),
                hotels.mapy.as("mapY"),
                getIntroAmenitiesCount().min()
                    .add(getRoomAmenitiesCount().sum())
                    .as("totalAminities"))
            )
            .groupBy(hotels.contentid, hotels.title, hotels.firstimage, hotels.addr1, hotels.mapx, hotels.mapy)
            .orderBy(orderCondition(searchRequest.getOrder(), RESERVATION_COUNT_ALIAS))
            .fetch();
    }
    private BooleanExpression createAvailabilityCondition(SearchRequestDto searchRequest) {
        return rooms.id.notIn(
            JPAExpressions
                .select(Expressions.numberTemplate(Long.class, "CAST({0} as long)", reservation.roomcode))
                .from(reservation)
                .where(reservation.hotel.contentid.eq(hotels.contentid),
                    reservation.status.eq("PAID"),
                    reservation.checkInDate.goe(searchRequest.getCheckInDate()),
                    reservation.checkOutDate.loe(searchRequest.getCheckOutDate()))
        );
    }

    // 카테고리 카운트 받아오는 쿼리
    public Map<String, Integer> fetchCategoryCounts(JPAQuery<?> baseQuery) {
        List<Tuple> countsResult = baseQuery
            .groupBy(hotels.contentid, hotels.category)
            .select(hotels.category, hotels.contentid)
            .fetch()
            .stream()
            .distinct()
            .collect(Collectors.toList());

        Map<String, Long> categoryCountsMap = countsResult.stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(hotels.category),
                Collectors.counting()
            ));

        Map<String, Integer> finalCounts = new LinkedHashMap<>();
        int totalCount = countsResult.size();
        
        int[] countArr = new int[4]; // 0:All, 1:Hotels, 2:Motels, 3:Cottages
        countArr[0] = totalCount;

        for (Map.Entry<String, Long> entry : categoryCountsMap.entrySet()) {
            int idx = switch (entry.getKey()) {
                case "B02010100" -> 1;
                case "B02010900" -> 2;
                case "B02010700" -> 3;
                default -> -1;
            };
            if (idx != -1) {
                countArr[idx] = entry.getValue().intValue();
            }
        }

        String[] categoryNames = {"All", "Hotels", "Motels", "Cottages"};
        for (int i = 0; i < categoryNames.length; i++) {
            finalCounts.put(categoryNames[i], countArr[i]);
        }
        
        return finalCounts;
    }

    //검색+필터링 조건
    private BooleanBuilder getCommonConditions(SearchRequestDto searchRequest, LocalDate checkInDate, LocalDate checkOutDate) {
            BooleanBuilder builder = new BooleanBuilder();
            //키워드 검색
            BooleanBuilder keywordExpr = keywordCondition(searchRequest.getKeyword());
            if (keywordExpr != null) {
                builder.and(keywordExpr);
            }
            //비용 0 제외
            builder.and(rooms.roomoffseasonminfee1.ne(0));
            //이미지 없는 목록 제외
            // builder.and(hotels.firstimage.isNotEmpty());
            //체크인, 체크아웃 기간에 예약 일정 없는지 체크
            builder.and(availableDateCondition(checkInDate, checkOutDate));
            //인원 수 충분한지 체크
            builder.and(rooms.roommaxcount.goe(searchRequest.getGuestCount()));
            //비용 필터
            builder.and(rooms.roomoffseasonminfee1.between(searchRequest.getMinPrice(), searchRequest.getMaxPrice()));
            //필터링
            builder.and(filterAmenitiesCondition(searchRequest.getAmenities()));
            builder.and(filterFreebiesCondition(searchRequest.getFreebies()));

            return builder;
    }

    private BooleanExpression ratingGoe(double rating) {
        if (rating <= 0) {
            return null;
        }
        // 0보다 큰 값이 들어오면, avg() >= rating 조건을 생성하여 반환
        return review.rating.avg().goe(rating);
    }

    private BooleanExpression categorySelectCondition(String category) {
        return switch (category) {
            case "Hotels" -> hotels.category.eq("B02010100");
            case "Motels" -> hotels.category.eq("B02010900");
            case "Cottages" -> hotels.category.eq("B02010700");
            default -> null;
        };
    }

    private BooleanBuilder keywordCondition(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        //초성+환성 한글 조합일 때 처리
        String processedKeyword = keyword.replaceAll("[ㄱ-ㅎㅏ-ㅣ]$", "");

        BooleanBuilder builder = new BooleanBuilder();
        if (!processedKeyword.isEmpty()) {
            builder.or(hotels.title.containsIgnoreCase(processedKeyword));
            builder.or(hotels.addr1.containsIgnoreCase(processedKeyword));
        }

        List<String> chosungMatchRegionNames = queryFactory
            .select(region.name)
            .from(region)
            .where(region.nameChosung.like(keyword + "%")) // 여기는 원본 keyword 사용
            .fetch();

        if (!chosungMatchRegionNames.isEmpty()) {
            for (String name : chosungMatchRegionNames) {
                builder.or(hotels.addr1.containsIgnoreCase(name));
            }
        }

        return builder;
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
            case "평점 높은 순" -> review.rating.avg().desc();
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

    @Override
    public List<String> findByRecommendElements(String keyword) {
        String keywordChosung = HangulUtils.getChosung(keyword);
        
        //지역명 검색
        List<String> candidatesFromRegions = queryFactory
            .select(region.name).distinct()
            .from(region)
            .where(
                region.name.like(keyword + "%")
                .or(region.nameChosung.like(keywordChosung + "%"))
            )
            .limit(20)
            .fetch();

        List<String> finalSuggestions = candidatesFromRegions.stream()
            .filter(candidate -> HangulUtils.isMixedMatch(candidate, keyword))
            .limit(10)
            .collect(Collectors.toList());

        return new ArrayList<>(finalSuggestions);
    }
    
}