package com.example.backend.wish;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.api.Hotels;
import com.example.backend.authentication.User;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT NEW com.example.backend.wish.WishResponseDto(" +
           "h.contentid, " +
           "h.title, " +
           "COALESCE(AVG(r.rating), 0.0), " +
           "COUNT(r.id), " +
           "h.addr1, " +
           "h.firstimage) " +
       "FROM Wishlist w " +
       "JOIN w.hotel h " +
       "LEFT JOIN Review r ON r.hotel.contentid = h.contentid " +
       "WHERE w.user.username = :memberId " +
       "GROUP BY h.contentid, h.title, h.addr1, h.firstimage")
    List<WishResponseDto> findLikedHotelsByMemberId(@Param("memberId") String memberId);
    
    boolean existsByUserAndHotel(User user, Hotels hotel);

    void deleteByUser_UsernameAndHotel_Contentid(String username, String hotelId);
    
}
