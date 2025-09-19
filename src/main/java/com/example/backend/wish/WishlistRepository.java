package com.example.backend.wish;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.api.Hotels;
import com.example.backend.authentication.User;
import com.example.backend.mypage.LikeResponseDto;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT NEW com.example.backend.mypage.LikeResponseDto(h.contentid, h.title, h.addr1, h.firstimage) " +
           "FROM Wishlist w JOIN w.hotel h WHERE w.user.username = :memberId")
    List<LikeResponseDto> findLikedHotelsByMemberId(@Param("memberId") String memberId);
    
    boolean existsByUserAndHotel(User user, Hotels hotel);

    void deleteByUser_UsernameAndHotel_Contentid(String username, String hotelId);
    
}
