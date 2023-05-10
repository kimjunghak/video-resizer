package com.image.resizer.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileInfoResult {

    private Long fileSize;

    private Integer width;

    private Integer height;

    private String videoUrl;
}
