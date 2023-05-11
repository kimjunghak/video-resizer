package com.image.resizer.controller;

import com.image.resizer.model.result.VideoResult;
import com.image.resizer.service.api.VideoApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class VideoController {

    @Value("${server.video-path}")
    private String videoPath;

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

    @GetMapping("/stream/video/{filename}")
    public ResponseEntity<StreamingResponseBody> getStreamVideo(@PathVariable String filename) {
        File file = new File(videoPath.concat(filename));
        if (!file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody streamingResponseBody = outputStream -> {
            final InputStream inputStream = new FileInputStream(file);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) >= 0) {
                outputStream.write(bytes, 0, length);
            }
            inputStream.close();
            outputStream.flush();
        };

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.parseMediaType("video/mp4"));
        responseHeaders.setContentLength(file.length());

        return ResponseEntity.ok().headers(responseHeaders).body(streamingResponseBody);
    }

}
