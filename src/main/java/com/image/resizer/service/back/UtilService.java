package com.image.resizer.service.back;

import com.image.resizer.model.enums.URLTYPE;
import com.image.resizer.model.properties.PathProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {

    @Value("${server.url}")
    private String serverUrl;

    private final PathProperties pathProperties;

    public String getFileUrl(URLTYPE type, String filename) {
        if (type.equals(URLTYPE.VIDEO)) {
            return serverUrl.concat("/video/").concat(filename);
        } else {
            return serverUrl.concat("/thumbnail").concat(filename);
        }
    }

    public String getVideoPath(String filename) {
        return pathProperties.getVideo().concat(filename);
    }

    public String getThumbnailPath(String filename) {
        return pathProperties.getThumbnail().concat(filename);
    }

    public String getResizedFilename(String filename) {
        return filename.replace(".mp4", "_resize.mp4");
    }

    public String getThumbnailFilename(String filename) {
        return filename.replace(".mp4", "_thumbnail.jpg");
    }
}
