package com.zyoutube.feature.video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DBDirectViewCountService implements ViewCountService {
    private final VideoRepository videoRepository;

    @Override
    @Transactional
    public long recordView(Long videoId) {
        int updatedRows = videoRepository.incrementViewCount(videoId);
        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to update view count");
        }

        return videoRepository.findViewCountById(videoId)
                .orElseThrow(() -> new IllegalStateException("Failed to read updated view count"));
    }
}
