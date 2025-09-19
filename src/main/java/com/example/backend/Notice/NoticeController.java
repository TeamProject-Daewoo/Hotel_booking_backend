package com.example.backend.Notice;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
     
    @GetMapping
    public List<NoticeDTO> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @GetMapping("/{id}")
    public NoticeDTO getNotice(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

     @GetMapping("/paged")
    public PagedResponse<NoticeDTO> getNoticesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NoticeDTO> noticePage = noticeService.getNoticesWithPaging(page, size);

        return new PagedResponse<>(
                noticePage.getContent(),
                noticePage.getNumber(),
                noticePage.getSize(),
                noticePage.getTotalElements(),
                noticePage.getTotalPages(),
                noticePage.isLast()
        );
}
}
