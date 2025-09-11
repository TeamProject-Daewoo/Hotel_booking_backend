package com.example.backend.searchRestApi;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.backend.api.QAccommodation;
import com.example.backend.api2.QDetail;
import com.example.backend.reservation.QReservation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class SearchRepositoryImpl implements SearchRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QAccommodation acc = QAccommodation.accommodation;
    private final QDetail rooms = QDetail.detail;
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
                    acc.contentid.as("contentId"),
                    acc.title.as("title"),
                    acc.firstimage.as("image"),
                    rooms.roomoffseasonminfee1.min().as("price"),
                    acc.addr1.as("address")
                )
            )
            .distinct()
            .from(acc)
            .join(rooms).on(acc.contentid.eq(rooms.contentid))
            .groupBy(acc.contentid)
             .where(
                //키워드 검색
                 keywordCondition(searchRequest.getKeyword()),      
                 //비용 0 제외
                 rooms.roomoffseasonminfee1.ne(0),      
                 //이미지 없는 목록 제외
                 acc.firstimage.isNotEmpty(),
                 //체크인, 체크아웃 기간에 예약 일정 없는지 체크
                 availableDateCondition(checkInDate, checkOutDate),
                 //객실와 인원 수 숙박 충분한지 체크
                 rooms.roomcount.goe(searchRequest.getRoomCount()),
                 rooms.roommaxcount.goe(searchRequest.getGuestCount()),
                 //비용 필터
                 rooms.roomoffseasonminfee1.between(searchRequest.getMinPrice(), searchRequest.getMaxPrice())
             )
            .fetch();
    }

    private BooleanExpression keywordCondition(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return acc.addr1.containsIgnoreCase(keyword)
            .or(acc.title.containsIgnoreCase(keyword));
    }

    private BooleanExpression availableDateCondition(LocalDate checkInDate, LocalDate checkOutDate) {
        return JPAExpressions.selectFrom(reservation)
            .where(
                reservation.hotel.contentid.eq(acc.contentid)
                    .and(
                        reservation.checkInDate.gt(checkInDate)
                        .and(reservation.checkOutDate.lt(checkOutDate))
                    )
            )
            .exists()
            .not();
    }
}