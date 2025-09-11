package com.example.backend.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.common.TourApi;

@RestController
@RequestMapping("/api") // 다른 컨트롤러와 일관성을 위해 /api 경로 추가
@RequiredArgsConstructor // @Autowired 대신 생성자 주입 방식 사용
public class AccommodationController {

    private final AccommodationService accommodationService;

    /**
     * 지역 기반으로 숙소 목록을 조회하는 API
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지 결과 수
     * @param areaCode 지역 코드 (옵션)
     * @return 숙소 DTO 리스트
     */
    @GetMapping("/accommodations")
    public ResponseEntity<List<AccommodationDto>> getAccommodations(
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "10") String numOfRows,
            @RequestParam(defaultValue = "") String areaCode) throws Exception {

        TourApi API = new TourApi();
        String uri = API.getAreaBase(pageNo, numOfRows, areaCode);
        List<AccommodationDto> accommodations = accommodationService.getAccommodations(uri);
        return ResponseEntity.ok(accommodations);
    }

    /**
     * 특정 숙소의 상세 정보를 조회하는 API
     * @param contentid 숙소의 contentId
     * @return 숙소 DTO
     */
    @GetMapping("/accommodations/{contentid}")
    public ResponseEntity<AccommodationDto> getAccommodation(@PathVariable String contentid) {
        // Service에서 Accommodation Entity를 찾아 Optional<Accommodation>으로 받음
        return accommodationService.getAccommodation(contentid)
            .map(entity -> {
                // Entity를 DTO로 변환하는 과정
                AccommodationDto dto = new AccommodationDto();
                dto.setContentid(entity.getContentid());
                dto.setTitle(entity.getTitle());
                dto.setAddr1(entity.getAddr1());
                dto.setTel(entity.getTel());
                dto.setFirstimage(entity.getFirstimage());
                dto.setFirstimage2(entity.getFirstimage2());
                dto.setModifiedtime(entity.getModifiedtime());
                dto.setCat1(entity.getCat1());
                dto.setCat2(entity.getCat2());
                dto.setCat3(entity.getCat3());
                dto.setContenttypeid(entity.getContenttypeid());
                dto.setMapx(entity.getMapx());
                dto.setMapy(entity.getMapy());
                dto.setAreaCode(entity.getAreaCode());
                dto.setSigunguCode(entity.getSigunguCode());
                dto.setlDongRegnCd(entity.getLDongRegnCd());
                dto.setlDongSignguCd(entity.getLDongSignguCd());
                dto.setLclsSystm1(entity.getLclsSystm1());
                dto.setLclsSystm2(entity.getLclsSystm2());
                dto.setLclsSystm3(entity.getLclsSystm3());
                return dto;
            })
            .map(ResponseEntity::ok) // 변환된 DTO를 담아 200 OK 응답 생성
            .orElse(ResponseEntity.notFound().build()); // 데이터가 없으면 404 Not Found 응답
    }
}