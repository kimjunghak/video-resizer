package com.image.resizer.model.result;

import com.image.resizer.model.entity.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfoResult {

    private Long fileSize;

    private Integer width;

    private Integer height;

    private String videoUrl;

    public static FileInfoResult toOriginalResult(FileInfo fileInfo) {
        return new FileInfoResult(fileInfo.getOriginalFileSize(), fileInfo.getOriginalWidth(), fileInfo.getOriginalHeight(), fileInfo.getOriginalVideoUrl());
    }

    public static FileInfoResult toResizedResult(FileInfo fileInfo) {
        return new FileInfoResult(fileInfo.getResizedFileSize(), fileInfo.getResizedWidth(), fileInfo.getResizedHeight(), fileInfo.getResizedVideoUrl());
    }
}
