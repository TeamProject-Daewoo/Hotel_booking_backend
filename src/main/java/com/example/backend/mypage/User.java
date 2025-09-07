package com.example.backend.mypage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_name")
    private String userName;

    private String email;

    @Column(name = "password_hash")
    private String password;

    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "join_date")
    private String joinDate;

    private String role;
    
    public void updateProfile(String name, String phoneNumber) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.phoneNumber = phoneNumber;
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}