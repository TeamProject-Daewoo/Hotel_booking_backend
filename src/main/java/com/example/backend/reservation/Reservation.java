package com.example.backend.reservation;

import com.example.backend.api.Hotels; // import 변경
import com.example.backend.authentication.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentid")
    private Hotels hotel;

    private String roomcode;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "num_adults")
    private Integer numAdults;

    @Column(name = "num_children")
    private Integer numChildren;

    private String status;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "reservation_date", insertable = false, updatable = false)
    private LocalDateTime reservationDate;
}