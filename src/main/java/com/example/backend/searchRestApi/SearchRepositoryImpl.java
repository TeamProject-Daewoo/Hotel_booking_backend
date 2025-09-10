package com.example.backend.searchRestApi;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.backend.api.QAccommodation;
import com.example.backend.api2.QDetail;
import com.example.backend.mypage.QReservation;

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
                    acc.contentid,
                    acc.title,
                    acc.firstimage,
                    rooms.roomoffseasonminfee1,
                    acc.addr1
                )
            )
            .from(acc)
            .join(rooms).on(acc.contentid.eq(rooms.contentid))
            .where(
                keywordCondition(searchRequest.getKeyword()),
                availableDateCondition(checkInDate, checkOutDate),
                rooms.roomcount.goe(searchRequest.getRoomCount()),
                rooms.roommaxcount.goe(searchRequest.getGuestCount()),
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