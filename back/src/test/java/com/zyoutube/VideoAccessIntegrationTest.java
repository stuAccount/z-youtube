package com.zyoutube;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VideoAccessIntegrationTest extends IntegrationTestSupport {
    @Test
    void unauthenticatedUserCannotViewDraftDetail() throws Exception {
        MockHttpSession authorSession = registerAndLogin("draft-owner");
        Long videoId = createVideo(authorSession, "Draft title", "Draft description", "PUBLIC");

        mockMvc.perform(get("/api/videos/{id}", videoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Video not found"));
    }

    @Test
    void authorCanViewOwnDraftDetail() throws Exception {
        MockHttpSession authorSession = registerAndLogin("draft-author");
        Long videoId = createVideo(authorSession, "Author draft", "Author draft description", "PRIVATE");

        mockMvc.perform(get("/api/videos/{id}", videoId).session(authorSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(videoId.intValue()))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    void unauthenticatedUserCannotViewPrivatePublishedDetail() throws Exception {
        MockHttpSession authorSession = registerAndLogin("private-owner");
        Long videoId = createPublishedVideo(authorSession, "Private title", "Private description", "PRIVATE");

        mockMvc.perform(get("/api/videos/{id}", videoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Video not found"));
    }

    @Test
    void authorCanViewOwnPrivatePublishedDetail() throws Exception {
        MockHttpSession authorSession = registerAndLogin("private-author");
        Long videoId = createPublishedVideo(authorSession, "Owner private", "Owner private description", "PRIVATE");

        mockMvc.perform(get("/api/videos/{id}", videoId).session(authorSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(videoId.intValue()))
                .andExpect(jsonPath("$.data.visibility").value("PRIVATE"));
    }

    @Test
    void unauthenticatedUserCanViewUnlistedDetail() throws Exception {
        MockHttpSession authorSession = registerAndLogin("unlisted-owner");
        Long videoId = createPublishedVideo(authorSession, "Unlisted title", "Unlisted description", "UNLISTED");

        mockMvc.perform(get("/api/videos/{id}", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(videoId.intValue()))
                .andExpect(jsonPath("$.data.visibility").value("UNLISTED"));
    }

    @Test
    void publicListingOnlyShowsPublishedPublicVideos() throws Exception {
        MockHttpSession authorSession = registerAndLogin("listing-owner");
        createVideo(authorSession, "Draft public", "draft", "PUBLIC");
        createPublishedVideo(authorSession, "Private published", "private", "PRIVATE");
        createPublishedVideo(authorSession, "Unlisted published", "unlisted", "UNLISTED");
        Long publicVideoId = createPublishedVideo(authorSession, "Public published", "public", "PUBLIC");

        MvcResult result = mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andReturn();

        List<Integer> ids = readIntList(result, "$.data.content[*].id");
        assertEquals(List.of(publicVideoId.intValue()), ids);
    }

    @Test
    void publicListingRejectsNonPublicFilters() throws Exception {
        mockMvc.perform(get("/api/videos").param("visibility", "UNLISTED"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Only public videos can be searched"));

        mockMvc.perform(get("/api/videos").param("status", "DRAFT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Only published videos can be searched"));
    }
}
