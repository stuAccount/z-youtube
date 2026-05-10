package com.zyoutube.feature.engagement;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.engagement.model.dto.SetVideoReactionRequest;
import com.zyoutube.feature.engagement.model.vo.FavoriteVideoSummaryResponse;
import com.zyoutube.feature.engagement.model.vo.VideoEngagementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EngagementController {
    private final EngagementService engagementService;

    @PutMapping("/api/videos/{id}/reaction")
    public ApiResponse<VideoEngagementResponse> setReaction(@PathVariable Long id,
                                                            @Valid @RequestBody SetVideoReactionRequest req) {
        return ApiResponse.success(engagementService.setReaction(id, req.getType()));
    }

    @DeleteMapping("/api/videos/{id}/reaction")
    public ApiResponse<VideoEngagementResponse> clearReaction(@PathVariable Long id) {
        return ApiResponse.success(engagementService.clearReaction(id));
    }

    @PutMapping("/api/videos/{id}/favorite")
    public ApiResponse<VideoEngagementResponse> addFavorite(@PathVariable Long id) {
        return ApiResponse.success(engagementService.addFavorite(id));
    }

    @DeleteMapping("/api/videos/{id}/favorite")
    public ApiResponse<VideoEngagementResponse> removeFavorite(@PathVariable Long id) {
        return ApiResponse.success(engagementService.removeFavorite(id));
    }

    @GetMapping("/api/me/favorites")
    public ApiResponse<Page<FavoriteVideoSummaryResponse>> getMyFavorites(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(engagementService.getMyFavorites(page, size));
    }
}
