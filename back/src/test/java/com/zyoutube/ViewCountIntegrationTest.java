package com.zyoutube;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ViewCountIntegrationTest extends IntegrationTestSupport {
    @Test
    void unauthenticatedUserCanRecordViewForPublicVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("public-view-owner");
        Long videoId = createPublishedVideo(authorSession, "Public view target", "Public view description", "PUBLIC");

        mockMvc.perform(post("/api/videos/{id}/view", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.videoId").value(videoId.intValue()))
                .andExpect(jsonPath("$.data.viewCount").value(1));

        mockMvc.perform(get("/api/videos/{id}", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(1));
    }

    @Test
    void repeatedViewCallsIncreaseViewCount() throws Exception {
        MockHttpSession authorSession = registerAndLogin("repeated-view-owner");
        Long videoId = createPublishedVideo(authorSession, "Repeat view target", "Repeat view description", "PUBLIC");

        mockMvc.perform(post("/api/videos/{id}/view", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(1));

        mockMvc.perform(post("/api/videos/{id}/view", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(2));
    }

    @Test
    void inaccessibleVideoDoesNotExposeViewEndpoint() throws Exception {
        MockHttpSession authorSession = registerAndLogin("private-view-owner");
        Long videoId = createPublishedVideo(authorSession, "Private view target", "Private view description", "PRIVATE");

        mockMvc.perform(post("/api/videos/{id}/view", videoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Video not found"));
    }

    @Test
    void authorCanRecordViewForOwnDraftVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("draft-view-owner");
        Long videoId = createVideo(authorSession, "Draft view target", "Draft view description", "PRIVATE");

        mockMvc.perform(post("/api/videos/{id}/view", videoId).session(authorSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(1));

        mockMvc.perform(get("/api/videos/{id}", videoId).session(authorSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(1));
    }
}
