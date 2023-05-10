package com.image.resizer.service.api;

import com.image.resizer.exception.UnSupportExtensionException;
import com.image.resizer.model.entity.FileInfo;
import com.image.resizer.model.entity.Video;
import com.image.resizer.model.result.FileInfoResult;
import com.image.resizer.model.result.VideoResult;
import com.image.resizer.service.FFmpegService;
import com.image.resizer.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.image.resizer.utils.Utils.getFilePath;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoApiService {

    private final FFmpegService ffmpegService;
    private final VideoService videoService;

    public void videoResize(MultipartFile file, String filename) {
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        if (extension == null || !extension.equals("mp4")) {
            throw new UnSupportExtensionException("확장자는 mp4만 가능합니다.");
        }

        String title = Strings.isEmpty(filename) ? originalFilename : filename;

        try {
            file.transferTo(new File(getFilePath(originalFilename)));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패하였습니다.");
        }
        // 파일 리사이징
        ffmpegService.resize(title, originalFilename);
    }

    public VideoResult getVideo(Long id) {
        Video video = videoService.getVideo(id);

        FileInfo fileInfo = video.getFileInfo();
        FileInfoResult originalFileInfo = new FileInfoResult(fileInfo.getOriginalFileSize(), fileInfo.getOriginalWidth(), fileInfo.getOriginalHeight(), fileInfo.getOriginalVideoUrl());
        FileInfoResult resizedFileInfo = new FileInfoResult(fileInfo.getResizedFileSize(), fileInfo.getResizedWidth(), fileInfo.getResizedHeight(), fileInfo.getResizedVideoUrl());

        return VideoResult.builder()
                .id(video.getId())
                .title(video.getTitle())
                .original(originalFileInfo)
                .resized(resizedFileInfo)
                .createdAt(video.getCreatedAt())
                .build();
    }
}
