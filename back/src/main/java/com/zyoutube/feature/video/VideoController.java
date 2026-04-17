package com.zyoutube.feature.video;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.video.model.dto.CreateVideoRequest;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.vo.VideoDetailResponse;
import com.zyoutube.feature.video.model.vo.VideoSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/videos")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ApiResponse<VideoDetailResponse> createVideo(@Valid @RequestBody CreateVideoRequest req) {
        return ApiResponse.success(videoService.createVideo(req));
    }

    @GetMapping("{id}")
    public ApiResponse<VideoDetailResponse> getDetail(@PathVariable Long id) {
        return ApiResponse.success(videoService.getDetail(id));
    }

    @GetMapping
    public ApiResponse<Page<VideoSummaryResponse>> getVideos(@RequestParam(required = false) Long authorId,
                                                             @RequestParam(required = false) VideoStatus status,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(videoService.getVideos(authorId, status, page, size));
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> deleteVideo(@PathVariable Long id,
                                    @RequestParam Long requesterAccountId) {
        videoService.deleteVideo(id, requesterAccountId);
        return ApiResponse.success(null);
    }
}
