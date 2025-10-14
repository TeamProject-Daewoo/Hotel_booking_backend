package com.example.backend.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignUp {
        private String username;
        private String password;
        private String name;
        private String phoneNumber;
        private Role role;

        // DTO를 Entity로 변환하는 메소드
        public User toEntity(String encodedPassword) {
            return User.builder()
                    .username(this.username)
                    .password(encodedPassword) // 암호화된 비밀번호 사용
                    .name(this.name)
                    .phoneNumber(this.phoneNumber)
                    .role(this.role) // 기본 역할을 USER로 설정
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Login {
        private String username;
        private String password;
        private String recaptchaToken;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Info {
        private String username;
        private String name;
        private String phoneNumber;
        private Role role;
        private Integer point;

        // User 엔티티를 Info DTO로 변환하는 정적 메소드
        public static Info from(User user) {
            return Info.builder()
                    .username(user.getUsername())
                    .name(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .point(user.getPoint())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccessTokenResponse {
        private String accessToken;
    }
}