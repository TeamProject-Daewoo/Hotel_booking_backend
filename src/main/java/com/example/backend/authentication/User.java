package com.example.backend.authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @Column(name = "user_name", updatable = false, unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;
    
    @Column(name = "login_type")
    private String loginType;
    
    @Column(name = "uuid")
    private String uuid;
    
    @Column(name = "business_registration_number")
    private String business_registration_number;

    @Builder
    public User(String username, String email, String password, String name, String phoneNumber, Role role, String loginType, String uuid) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.joinDate = LocalDateTime.now();
        this.role = role;
        this.loginType = loginType;
        this.uuid = uuid;

        if (role == Role.ADMIN || role == Role.BUSINESS) {
            this.approvalStatus = ApprovalStatus.PENDING;
        } else {
            this.approvalStatus = ApprovalStatus.APPROVED;
        }
    }

    // --- UserProfile로부터 가져온 메소드들 ---
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
    // ------------------------------------

    // --- Spring Security UserDetails 인터페이스 구현 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.approvalStatus == ApprovalStatus.APPROVED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

  public boolean isNewUser() {
    return joinDate != null 
           && joinDate.toLocalDate().isEqual(LocalDate.now());
}


    
}