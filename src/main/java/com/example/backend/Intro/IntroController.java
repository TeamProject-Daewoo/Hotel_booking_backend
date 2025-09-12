package com.example.backend.Intro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.common.TourApi;

@RestController
@RequestMapping("/tour")
public class IntroController {

    @Autowired
    private IntroService introService;

    @GetMapping("/intro/{contentId}")
    public ResponseEntity<Intro> getIntro(@PathVariable String contentId) throws Exception {
        // KorService2 detailIntro2 호출용 URI 생성
        TourApi api = new TourApi();
        String introUri = api.getIntroUri("1", "10", contentId);

        Intro dto = introService.getIntroDTO(introUri);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}