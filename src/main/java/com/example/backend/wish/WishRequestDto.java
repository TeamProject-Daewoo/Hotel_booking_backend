package com.example.backend.wish;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishRequestDto {
    private int hotelId;
    private String username;
}