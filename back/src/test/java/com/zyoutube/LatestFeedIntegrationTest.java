package com.zyoutube;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LatestFeedIntegrationTest extends IntegrationTestSupport {
    @Test
    void latestFeedOnlyReturnsPublishedPublicVideosInDescendingCreatedOrder() throws Exception {
        MockHttpSession authorSession = registerAndLogin("latest-feed-owner");

        createVideo(authorSession, "Draft hidden", "draft", "PUBLIC", "https://cdn.example.com/draft.mp4", null);
        createPublishedVideo(authorSession, "Private hidden", "private", "PRIVATE", "https://cdn.example.com/private.mp4",
                "https://cdn.example.com/private.jpg");
        createPublishedVideo(authorSession, "Unlisted hidden", "unlisted", "UNLISTED", "https://cdn.example.com/unlisted.mp4",
                "https://cdn.example.com/unlisted.jpg");
        Long olderPublicId = createPublishedVideo(authorSession, "Older public", "older", "PUBLIC",
                "https://cdn.example.com/older.mp4", "https://cdn.example.com/older.jpg");
        Long newerPublicId = createPublishedVideo(authorSession, "Newer public", "newer", "PUBLIC",
                "https://cdn.example.com/newer.mp4", "https://cdn.example.com/newer.jpg");

        MvcResult result = mockMvc.perform(get("/api/feed/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].coverUrl").value("https://cdn.example.com/newer.jpg"))
                .andExpect(jsonPath("$.data.content[0].videoUrl").value("https://cdn.example.com/newer.mp4"))
                .andReturn();

        assertEquals(List.of(newerPublicId.intValue(), olderPublicId.intValue()), readIntList(result, "$.data.content[*].id"));
    }
}
