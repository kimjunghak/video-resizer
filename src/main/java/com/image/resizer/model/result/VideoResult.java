package com.image.resizer.model.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VideoResult {

    private Long id;

    private String title;

    private String thumbnailUrl;

    private FileInfoResult original;

    private FileInfoResult resized;

    private LocalDateTime createdAt;
}
