package com.example.backend.CustomerService;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

   @PostMapping
public ResponseEntity<?> createInquiry(
        Authentication authentication,
        @RequestBody InquiryRequestDto requestDto) {

    if (authentication == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
    }

    Object principal = authentication.getPrincipal();
    String username;

    if (principal instanceof org.springframework.security.core.userdetails.User) {
        username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
    } else if (principal instanceof String) {
        username = (String) principal;
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 인증 정보입니다.");
    }

    inquiryService.createInquiry(username, requestDto);

    return ResponseEntity.ok("문의가 등록되었습니다.");
}


    @GetMapping("/user")
public ResponseEntity<List<InquiryResponseDto>> getMyInquiries(
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
    String username = userDetails.getUsername();
    List<InquiryResponseDto> inquiries = inquiryService.getInquiriesByUser(username);
    return ResponseEntity.ok(inquiries);
}

@GetMapping("/{id}")
public ResponseEntity<InquiryResponseDto> getInquiryDetail(
        @PathVariable Long id,
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

    InquiryResponseDto inquiry = inquiryService.getInquiryDetail(id, userDetails.getUsername());
    return ResponseEntity.ok(inquiry);
}

@PostMapping("/with-files")
public ResponseEntity<?> createInquiryWithFiles(
        Authentication authentication,
        @RequestBody InquiryWithFilesRequestDto requestDto) {

    if (authentication == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
    }

    Object principal = authentication.getPrincipal();
    String username = (principal instanceof org.springframework.security.core.userdetails.User)
            ? ((org.springframework.security.core.userdetails.User) principal).getUsername()
            : (String) principal;

    inquiryService.createInquiryWithFiles(username, requestDto.getInquiry(), requestDto.getFileUrls());

    return ResponseEntity.ok("파일과 함께 문의가 등록되었습니다.");
}


}

