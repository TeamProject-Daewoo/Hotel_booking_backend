package com.example.backend.Intro;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.common.TourApi;

@RestController
@RequestMapping("/tour")
@RequiredArgsConstructor
public class IntroController {

    private final IntroService introService;

    @GetMapping("/intro/{contentId}")
    public ResponseEntity<IntroDTO> getIntro(@PathVariable String contentId) throws Exception {
        TourApi api = new TourApi();
        String introUri = api.getIntroUri("1", "10", contentId);

        IntroDTO introData = introService.getIntro(introUri);

        if (introData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(introData);
    }
}