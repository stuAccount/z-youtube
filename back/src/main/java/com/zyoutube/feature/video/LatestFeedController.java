package com.zyoutube.feature.video;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.video.model.vo.PublicVideoSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
public class LatestFeedController {
    private final VideoService videoService;

    public LatestFeedController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/latest")
    public ApiResponse<Page<PublicVideoSummaryResponse>> getLatestFeed(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(videoService.getLatestFeed(page, size));
    }
}
