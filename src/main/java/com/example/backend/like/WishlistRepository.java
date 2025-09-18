package com.example.backend.like;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.mypage.LikeResponseDto;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT NEW com.example.backend.mypage.LikeResponseDto(h.hotelId, h.hotelName, h.address, h.imageUrl) " +
           "FROM Wishlist w JOIN w.hotel h WHERE w.user.memberId = :memberId")
    List<LikeResponseDto> findLikedHotelsByMemberId(@Param("memberId") String memberId);

}
