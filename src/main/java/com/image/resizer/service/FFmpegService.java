package com.image.resizer.service;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.image.resizer.model.entity.FileInfo;
import com.image.resizer.model.entity.Video;
import com.image.resizer.model.result.FileInfoResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

import static com.image.resizer.model.entity.FileInfo.makeFileInfo;
import static com.image.resizer.utils.Utils.getFilePath;
import static com.image.resizer.utils.Utils.getResizedFilename;

@Slf4j
@Service
public class FFmpegService {

    @Value("${server.url}")
    private String serverUrl;

    private final VideoService videoService;
    private final FileInfoService fileInfoService;

    public FFmpegService(VideoService videoService, FileInfoService fileInfoService) {
        this.videoService = videoService;
        this.fileInfoService = fileInfoService;
    }

    @Async("taskExecutor")
    public void resize(String title, String originalFilename) {
        String filePath = getFilePath(originalFilename);

        String resizedFilename = getResizedFilename(originalFilename);
        String outputPath = getFilePath(resizedFilename);

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(filePath))
                .addArguments("-vf", "scale=360:-2")
                .addOutput(UrlOutput.toUrl(outputPath))
                .setOverwriteOutput(true)
                .execute();

        FileInfoResult originalFileInfo = extractFileInfo(filePath, originalFilename);
        FileInfoResult resizedFileInfo = extractFileInfo(outputPath, resizedFilename);
        FileInfo fileInfo = makeFileInfo(originalFileInfo, resizedFileInfo);
        fileInfoService.save(fileInfo);

        Video video = Video.builder()
                .title(title)
                .fileInfo(fileInfo)
                .build();
        videoService.save(video);
    }

    public FileInfoResult extractFileInfo(String filePath, String title) {
        FFprobeResult result = FFprobe.atPath()
                .setShowStreams(true)
                .setShowFormat(true)
                .setInput(Path.of(filePath))
                .execute();

        int width = -1, height = -1;
        for (Stream stream : result.getStreams()) {
            Integer streamWidth = stream.getWidth();
            if (streamWidth != null) {
                width = streamWidth;
            }

            Integer streamHeight = stream.getHeight();
            if (streamHeight != null) {
                height = streamHeight;
            }
        }

        Long fileSize = result.getFormat().getSize();

        return new FileInfoResult(fileSize, width, height, serverUrl.concat(title));
    }
}
