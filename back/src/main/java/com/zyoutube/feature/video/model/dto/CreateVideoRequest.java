package com.zyoutube.feature.video.model.dto;

import com.zyoutube.feature.video.model.type.VideoVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVideoRequest {
    @NotBlank(message = "Title can not be blank")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @NotBlank(message = "Description can not be blank")
    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    @NotBlank(message = "Video url can not be blank")
    @Size(max = 1000, message = "Video url must be at most 1000 characters")
    @URL(message = "Video url must be a valid URL")
    private String videoUrl;

    @Size(max = 1000, message = "Cover url must be at most 1000 characters")
    @URL(message = "Cover url must be a valid URL")
    private String coverUrl;

    private VideoVisibility visibility;
}
