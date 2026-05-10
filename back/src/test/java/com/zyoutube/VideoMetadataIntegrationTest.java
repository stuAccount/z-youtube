package com.zyoutube;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VideoMetadataIntegrationTest extends IntegrationTestSupport {
    @Test
    void createVideoReturnsPlaybackFields() throws Exception {
        MockHttpSession authorSession = registerAndLogin("metadata-create-owner");

        Long videoId = createVideo(
                authorSession,
                "Metadata title",
                "Metadata description",
                "PUBLIC",
                "https://cdn.example.com/metadata.mp4",
                "https://cdn.example.com/metadata.jpg"
        );

        mockMvc.perform(get("/api/videos/{id}", videoId).session(authorSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.videoUrl").value("https://cdn.example.com/metadata.mp4"))
                .andExpect(jsonPath("$.data.coverUrl").value("https://cdn.example.com/metadata.jpg"));
    }

    @Test
    void updateVideoCanChangePlaybackFields() throws Exception {
        MockHttpSession authorSession = registerAndLogin("metadata-update-owner");
        Long videoId = createVideo(
                authorSession,
                "Before update",
                "Before update description",
                "PRIVATE",
                "https://cdn.example.com/before.mp4",
                "https://cdn.example.com/before.jpg"
        );

        mockMvc.perform(patch("/api/videos/{id}", videoId)
                        .session(authorSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "videoUrl": "https://cdn.example.com/after.mp4",
                                  "coverUrl": "https://cdn.example.com/after.jpg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.videoUrl").value("https://cdn.example.com/after.mp4"))
                .andExpect(jsonPath("$.data.coverUrl").value("https://cdn.example.com/after.jpg"));
    }
}
