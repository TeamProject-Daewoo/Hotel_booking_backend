package com.example.backend.like;

import com.example.backend.api.Hotels;
import com.example.backend.authentication.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Wishlist {

    @Id
    private long wishlistId;

    @ManyToOne
    @JoinColumn(name = "user_name")
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotels hotel;
}