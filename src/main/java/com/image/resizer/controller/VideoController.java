package com.image.resizer.controller;

import com.image.resizer.model.result.ProgressResult;
import com.image.resizer.model.result.VideoResult;
import com.image.resizer.service.back.UtilService;
import com.image.resizer.service.front.VideoApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoApiService videoApiService;
    private final UtilService utilService;

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
        File file = new File(utilService.getVideoPath(filename));
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.parseMediaType("video/mp4"));
        responseHeaders.setContentLength(file.length());

        return ResponseEntity.ok().headers(responseHeaders).body(getStreamingResponseBody(file));
    }

    @GetMapping("/thumbnail/{filename}")
    public ResponseEntity<Object> getThumbnail(@PathVariable String filename) {
        File file = new File(utilService.getThumbnailPath(filename));
        FileSystemResource resource = new FileSystemResource(file);
        if (!file.exists() || !resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok().headers(responseHeaders).body(resource);
    }

    @GetMapping("/video/{id}/progress")
    public ProgressResult getProgress(@PathVariable Long id) {
        return videoApiService.getProgressStatus(id);
    }

    private static StreamingResponseBody getStreamingResponseBody(File file) {
        return outputStream -> {
            final InputStream inputStream = new FileInputStream(file);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) >= 0) {
                outputStream.write(bytes, 0, length);
            }
            inputStream.close();
            outputStream.flush();
        };
    }

}
