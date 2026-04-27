package com.zyoutube.feature.video;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import com.zyoutube.feature.video.model.vo.MyVideoSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/videos")
public class MyVideoController {
    private final VideoService videoService;

    public MyVideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ApiResponse<Page<MyVideoSummaryResponse>> getMyVideos(@RequestParam(required = false) VideoStatus status,
                                                                 @RequestParam(required = false) VideoVisibility visibility,
                                                                 @RequestParam(required = false) String keyword,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(videoService.getMyVideos(status, visibility, keyword, page, size));
    }
}
