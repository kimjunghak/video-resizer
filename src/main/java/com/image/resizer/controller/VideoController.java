package com.image.resizer.controller;

import com.image.resizer.model.result.VideoResult;
import com.image.resizer.service.api.VideoApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoApiService videoApiService;

    @PostMapping("/video/resize")
    public ResponseEntity<String> uploadResizeImage(@RequestParam("file") MultipartFile file,
                                            @RequestParam(required = false) String filename) {
        videoApiService.videoResize(file, filename);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/video/{id}")
    public VideoResult getVideo(@PathVariable Long id) {
        return videoApiService.getVideo(id);
    }

}
