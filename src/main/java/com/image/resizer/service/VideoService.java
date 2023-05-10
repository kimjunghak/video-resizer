package com.image.resizer.service;

import com.image.resizer.model.entity.Video;
import com.image.resizer.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    public void save(Video video) {
        videoRepository.save(video);
    }

    public Video getVideo(Long id) {
        return videoRepository.findById(id).orElseThrow(() -> new RuntimeException("잘못된 요청입니다."));
    }
}
