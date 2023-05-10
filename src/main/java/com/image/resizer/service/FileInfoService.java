package com.image.resizer.service;

import com.image.resizer.model.entity.FileInfo;
import com.image.resizer.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileInfoService {

    private final FileInfoRepository fileInfoRepository;

    public void save(FileInfo fileInfo) {
        fileInfoRepository.save(fileInfo);
    }
}
