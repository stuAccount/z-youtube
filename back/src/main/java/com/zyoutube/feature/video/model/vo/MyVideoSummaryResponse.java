package com.zyoutube.feature.video.model.vo;

import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyVideoSummaryResponse {
    private Long id;
    private String title;
    private String videoUrl;
    private String coverUrl;
    private VideoStatus status;
    private VideoVisibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
