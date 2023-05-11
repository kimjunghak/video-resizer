package com.image.resizer.service;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.image.resizer.model.entity.FileInfo;
import com.image.resizer.model.result.FileInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class FFmpegService {

    @Value("${server.url}")
    private String serverUrl;

    @Value("${server.video-path}")
    private String videoPath;

    private final FileInfoService fileInfoService;

    @Async("taskExecutor")
    public void resize(String originalFilename, FileInfo fileInfo) {
        String filePath = videoPath.concat(originalFilename);

        String resizedFilename = getResizedFilename(originalFilename);
        String outputPath = videoPath.concat(resizedFilename);

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(filePath))
                .addArguments("-vf", "scale=360:-2")
                .addOutput(UrlOutput.toUrl(outputPath))
                .setOverwriteOutput(true)
                .execute();

        FileInfoResult resizedFileInfo = extractFileInfo(outputPath, resizedFilename);
        fileInfo.mergeResized(resizedFileInfo);
        fileInfoService.save(fileInfo);
    }

    public FileInfoResult extractFileInfo(String filePath, String filename) {
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

        return new FileInfoResult(fileSize, width, height, serverUrl.concat("/stream/video/").concat(filename));
    }

    private static String getResizedFilename(String filename) {
        return filename.replace(".mp4", "_resize.mp4");
    }
}
