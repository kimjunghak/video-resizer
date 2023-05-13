package com.image.resizer.service.front;

import com.image.resizer.exception.UnSupportExtensionException;
import com.image.resizer.model.entity.FileInfo;
import com.image.resizer.model.entity.Video;
import com.image.resizer.model.enums.URLTYPE;
import com.image.resizer.model.result.FileInfoResult;
import com.image.resizer.model.result.ProgressResult;
import com.image.resizer.model.result.VideoResult;
import com.image.resizer.service.back.FFmpegService;
import com.image.resizer.service.back.FileInfoService;
import com.image.resizer.service.back.UtilService;
import com.image.resizer.service.back.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoApiService {

    private final FFmpegService ffmpegService;
    private final VideoService videoService;
    private final FileInfoService fileInfoService;
    private final UtilService utilService;


    public void videoResize(MultipartFile file, String filename) {
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        if (extension == null || !extension.equals("mp4")) {
            throw new UnSupportExtensionException("확장자는 mp4만 가능합니다.");
        }

        String title = Strings.isEmpty(filename) ? originalFilename : filename;
        String filePath = utilService.getVideoPath(originalFilename);

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패하였습니다.");
        }


        FileInfoResult originalFileInfo = ffmpegService.extractFileInfo(filePath, originalFilename);
        FileInfo original = FileInfo.toOriginal(originalFileInfo);
        fileInfoService.save(original);

        String thumbnailFilename = ffmpegService.extractThumbnail(originalFilename);

        String thumbnailUrl = utilService.getFileUrl(URLTYPE.THUMBNAIL, thumbnailFilename);
        Video video = Video.builder()
                .title(title)
                .fileInfo(original)
                .thumbnailUrl(thumbnailUrl)
                .build();
        videoService.save(video);

        // 파일 리사이징 async
        ffmpegService.resize(video.getId(), originalFilename, original);
    }

    public VideoResult getVideo(Long id) {
        Video video = videoService.getVideo(id);

        FileInfo fileInfo = video.getFileInfo();
        FileInfoResult originalFileInfo = FileInfoResult.toOriginalResult(fileInfo);
        FileInfoResult resizedFileInfo = FileInfoResult.toResizedResult(fileInfo);

        return VideoResult.builder()
                .id(video.getId())
                .title(video.getTitle())
                .thumbnailUrl(video.getThumbnailUrl())
                .original(originalFileInfo)
                .resized(resizedFileInfo)
                .createdAt(video.getCreatedAt())
                .build();
    }

    public ProgressResult getProgressStatus(Long videoId) {
        return new ProgressResult(videoId, ffmpegService.getProgress(videoId));
    }
}
