package com.example.backend.CustomerService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.File.InquiryFile;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.File.InquiryFileRepository;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    private final InquiryFileRepository inquiryFileRepository;
    
    @Transactional
    public void createInquiry(String username, InquiryRequestDto dto) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Inquiry inquiry = Inquiry.create(
                dto.getCategory(),
                dto.getTitle(),
                dto.getContent(),
                dto.getAttachmentUrl(),
                user
        );

        inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
public List<InquiryResponseDto> getInquiriesByUser(String username) {
     List<Inquiry> inquiries = inquiryRepository.findByUser_Username(
        username,
        Sort.by(Sort.Direction.DESC, "createdAt")  // 생성일(createdAt) 기준 오름차순 정렬
    );
    return inquiries.stream()
            .map(InquiryResponseDto::fromEntity)
            .collect(Collectors.toList());
}

@Transactional(readOnly = true)
public InquiryResponseDto getInquiryDetail(Long inquiryId, String username) {
    Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다."));

    if (!inquiry.getUser().getUsername().equals(username)) {
        throw new IllegalArgumentException("권한이 없습니다.");
    }

    return InquiryResponseDto.fromEntity(inquiry);
}

public void createInquiryWithFiles(String username, InquiryRequestDto dto, List<String> fileUrls) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    Inquiry inquiry = Inquiry.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .category(dto.getCategory())
        .status(InquiryStatus.PENDING)
        .createdAt(LocalDateTime.now())
        .user(user)
        .build();

    inquiryRepository.save(inquiry);

    for (String url : fileUrls) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        InquiryFile file = InquiryFile.builder()
            .inquiry(inquiry)
            .fileName(fileName)
            .filePath(url)
            .build();
        inquiryFileRepository.save(file);
    }
}


}

