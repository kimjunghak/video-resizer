package com.image.resizer.service.api;

import com.image.resizer.exception.UnSupportExtensionException;
import com.image.resizer.model.entity.FileInfo;
import com.image.resizer.model.entity.Video;
import com.image.resizer.model.result.FileInfoResult;
import com.image.resizer.model.result.VideoResult;
import com.image.resizer.service.FFmpegService;
import com.image.resizer.service.FileInfoService;
import com.image.resizer.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoApiService {

    @Value("${server.video-path}")
    private String videoPath;

    private final FFmpegService ffmpegService;
    private final VideoService videoService;
    private final FileInfoService fileInfoService;

    public void videoResize(MultipartFile file, String filename) {
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        if (extension == null || !extension.equals("mp4")) {
            throw new UnSupportExtensionException("확장자는 mp4만 가능합니다.");
        }

        String title = Strings.isEmpty(filename) ? originalFilename : filename;
        String filePath = videoPath.concat(originalFilename);

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패하였습니다.");
        }

        FileInfoResult originalFileInfo = ffmpegService.extractFileInfo(filePath, originalFilename);
        FileInfo original = FileInfo.toOriginal(originalFileInfo);
        fileInfoService.save(original);

        Video video = Video.builder()
                .title(title)
                .fileInfo(original)
                .build();
        videoService.save(video);

        // 파일 리사이징 async
        ffmpegService.resize(originalFilename, original);
    }

    public VideoResult getVideo(Long id) {
        Video video = videoService.getVideo(id);

        FileInfo fileInfo = video.getFileInfo();
        FileInfoResult originalFileInfo = FileInfoResult.toOriginalResult(fileInfo);
        FileInfoResult resizedFileInfo = FileInfoResult.toResizedResult(fileInfo);

        return VideoResult.builder()
                .id(video.getId())
                .title(video.getTitle())
                .original(originalFileInfo)
                .resized(resizedFileInfo)
                .createdAt(video.getCreatedAt())
                .build();
    }
}
