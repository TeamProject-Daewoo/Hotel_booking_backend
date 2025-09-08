package com.example.backend.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto.Info> getMyInfo(Authentication authentication) {
        // SecurityContext에서 인증된 사용자의 username을 가져옴
        String username = authentication.getName();
        System.out.println(username);
        UserDto.Info userInfo = userService.getUserInfo(username);
        return ResponseEntity.ok(userInfo);
    }
}