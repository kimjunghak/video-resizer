package com.image.resizer.model.entity;

import com.image.resizer.model.result.FileInfoResult;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter(AccessLevel.PRIVATE)
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

    public static FileInfo toOriginal(FileInfoResult original) {
        return FileInfo.builder()
                .originalFileSize(original.getFileSize())
                .originalWidth(original.getWidth())
                .originalHeight(original.getHeight())
                .originalVideoUrl(original.getVideoUrl())
                .build();
    }

    public void mergeResized(FileInfoResult resized) {
        this.resizedFileSize = resized.getFileSize();
        this.resizedWidth = resized.getWidth();
        this.resizedHeight = resized.getHeight();
        this.resizedVideoUrl = resized.getVideoUrl();
    }
}
