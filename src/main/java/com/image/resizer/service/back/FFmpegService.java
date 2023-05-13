package com.image.resizer.service.back;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.NullOutput;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import com.image.resizer.model.entity.FileInfo;
import com.image.resizer.model.enums.URLTYPE;
import com.image.resizer.model.result.FileInfoResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class FFmpegService {

    private final FileInfoService fileInfoService;
    private final UtilService utilService;

    private final Map<Long, String> progressMap = new ConcurrentHashMap<>();

    @Async("taskExecutor")
    public void resize(Long videoId, String originalFilename, FileInfo fileInfo) {
        String filePath = utilService.getVideoPath(originalFilename);

        String resizedFilename = utilService.getResizedFilename(originalFilename);
        String outputPath = utilService.getVideoPath(resizedFilename);

        AtomicLong duration = new AtomicLong();
        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(filePath))
                .addOutput(new NullOutput())
                .setOverwriteOutput(true)
                .setProgressListener(progress -> duration.set(progress.getFrame()))
                .execute();

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(filePath))
                .addArguments("-vf", "scale=360:-2")
                .addOutput(UrlOutput.toUrl(outputPath))
                .setOverwriteOutput(true)
                .setProgressListener(progress -> {
                    double cal = 100. * progress.getFrame() / duration.get();
                    BigDecimal percent = new BigDecimal(cal);
                    progressMap.put(videoId, String.format("%d%%", percent.intValue()));
                })
                .execute();

        progressMap.remove(videoId);

        FileInfoResult resizedFileInfo = extractFileInfo(outputPath, resizedFilename);
        fileInfo.mergeResized(resizedFileInfo);
        fileInfoService.save(fileInfo);
    }

    public String extractThumbnail(String filename) {
        String originalFilename = utilService.getVideoPath(filename);

        String thumbnailFilename = utilService.getThumbnailFilename(filename);
        String thumbnailOutput = utilService.getThumbnailPath(thumbnailFilename);

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(originalFilename))
                .addArguments("-ss", "00:00:01.000")
                .addArguments("-vframes", "1")
                .addArguments("-vf", "format=yuv420p")
                .addOutput(UrlOutput.toUrl(thumbnailOutput))
                .setOverwriteOutput(true)
                .execute();

        return thumbnailFilename;
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

        return new FileInfoResult(fileSize, width, height, utilService.getFileUrl(URLTYPE.VIDEO, filename));
    }

    public String getProgress(Long videoId) {
        return progressMap.get(videoId);
    }
}
