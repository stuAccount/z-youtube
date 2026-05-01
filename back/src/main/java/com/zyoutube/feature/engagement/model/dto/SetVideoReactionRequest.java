package com.zyoutube.feature.engagement.model.dto;

import com.zyoutube.feature.engagement.model.type.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetVideoReactionRequest {
    @NotNull(message = "Reaction type is required")
    private ReactionType type;
}
