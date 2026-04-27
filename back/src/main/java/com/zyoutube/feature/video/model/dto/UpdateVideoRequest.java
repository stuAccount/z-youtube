package com.zyoutube.feature.video.model.dto;

import com.zyoutube.feature.video.model.type.VideoVisibility;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVideoRequest {
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    private VideoVisibility visibility;
}