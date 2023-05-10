package com.image.resizer.model.entity;

import com.image.resizer.model.result.FileInfoResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity @Table
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long originalFileSize;

    private Integer originalWidth;

    private Integer originalHeight;

    private String originalVideoUrl;

    private Long resizedFileSize;

    private Integer resizedWidth;

    private Integer resizedHeight;

    private String resizedVideoUrl;

    public static FileInfo makeFileInfo(FileInfoResult originalFileInfo, FileInfoResult resizedFileInfo) {
        return FileInfo.builder()
                .originalFileSize(originalFileInfo.getFileSize())
                .originalWidth(originalFileInfo.getWidth())
                .originalHeight(originalFileInfo.getHeight())
                .originalVideoUrl(originalFileInfo.getVideoUrl())
                .resizedFileSize(resizedFileInfo.getFileSize())
                .resizedWidth(resizedFileInfo.getWidth())
                .resizedHeight(resizedFileInfo.getHeight())
                .resizedVideoUrl(resizedFileInfo.getVideoUrl())
                .build();
    }
}
