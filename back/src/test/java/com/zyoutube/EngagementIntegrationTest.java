package com.zyoutube;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EngagementIntegrationTest extends IntegrationTestSupport {
    @Test
    void unauthenticatedUserCannotSetReactionOrFavorite() throws Exception {
        MockHttpSession authorSession = registerAndLogin("engagement-owner");
        Long videoId = createPublishedVideo(authorSession, "Public target", "Public target description", "PUBLIC");

        mockMvc.perform(put("/api/videos/{id}/reaction", videoId)
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": "LIKE"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/videos/{id}/favorite", videoId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/videos/{id}/favorite", videoId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanLikeVisibleVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("like-owner");
        MockHttpSession viewerSession = registerAndLogin("like-viewer");
        Long videoId = createPublishedVideo(authorSession, "Like me", "Like me description", "PUBLIC");

        mockMvc.perform(put("/api/videos/{id}/reaction", videoId)
                        .session(viewerSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": "LIKE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.videoId").value(videoId.intValue()))
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.dislikeCount").value(0))
                .andExpect(jsonPath("$.data.myReaction").value("LIKE"));
    }

    @Test
    void repeatingSameReactionCancelsIt() throws Exception {
        MockHttpSession authorSession = registerAndLogin("toggle-like-owner");
        MockHttpSession viewerSession = registerAndLogin("toggle-like-viewer");
        Long videoId = createPublishedVideo(authorSession, "Toggle like", "Toggle like description", "PUBLIC");

        setReaction(viewerSession, videoId, "LIKE");

        mockMvc.perform(put("/api/videos/{id}/reaction", videoId)
                        .session(viewerSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": "LIKE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(0))
                .andExpect(jsonPath("$.data.dislikeCount").value(0))
                .andExpect(jsonPath("$.data.myReaction").doesNotExist());
    }

    @Test
    void switchingReactionUpdatesCounts() throws Exception {
        MockHttpSession authorSession = registerAndLogin("switch-owner");
        MockHttpSession viewerSession = registerAndLogin("switch-viewer");
        Long videoId = createPublishedVideo(authorSession, "Switch reaction", "Switch reaction description", "PUBLIC");

        setReaction(viewerSession, videoId, "DISLIKE");

        mockMvc.perform(put("/api/videos/{id}/reaction", videoId)
                        .session(viewerSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": "LIKE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.dislikeCount").value(0))
                .andExpect(jsonPath("$.data.myReaction").value("LIKE"));
    }

    @Test
    void clearReactionIsIdempotent() throws Exception {
        MockHttpSession authorSession = registerAndLogin("clear-owner");
        MockHttpSession viewerSession = registerAndLogin("clear-viewer");
        Long videoId = createPublishedVideo(authorSession, "Clear reaction", "Clear reaction description", "PUBLIC");
        setReaction(viewerSession, videoId, "DISLIKE");

        mockMvc.perform(delete("/api/videos/{id}/reaction", videoId).session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(0))
                .andExpect(jsonPath("$.data.dislikeCount").value(0))
                .andExpect(jsonPath("$.data.myReaction").doesNotExist());

        mockMvc.perform(delete("/api/videos/{id}/reaction", videoId).session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(0))
                .andExpect(jsonPath("$.data.dislikeCount").value(0));
    }

    @Test
    void favoriteEndpointsAreIdempotent() throws Exception {
        MockHttpSession authorSession = registerAndLogin("favorite-owner");
        MockHttpSession viewerSession = registerAndLogin("favorite-viewer");
        Long videoId = createPublishedVideo(authorSession, "Favorite target", "Favorite target description", "PUBLIC");

        mockMvc.perform(put("/api/videos/{id}/favorite", videoId).session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.favoriteCount").value(1))
                .andExpect(jsonPath("$.data.favorited").value(true));

        mockMvc.perform(put("/api/videos/{id}/favorite", videoId).session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.favoriteCount").value(1))
                .andExpect(jsonPath("$.data.favorited").value(true));

        mockMvc.perform(delete("/api/videos/{id}/favorite", videoId).session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.favoriteCount").value(0))
                .andExpect(jsonPath("$.data.favorited").value(false));

        mockMvc.perform(delete("/api/videos/{id}/favorite", videoId).session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.favoriteCount").value(0))
                .andExpect(jsonPath("$.data.favorited").value(false));
    }

    @Test
    void nonAuthorCannotInteractWithPrivateVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("private-engagement-owner");
        MockHttpSession viewerSession = registerAndLogin("private-engagement-viewer");
        Long videoId = createPublishedVideo(authorSession, "Private target", "Private target description", "PRIVATE");

        mockMvc.perform(put("/api/videos/{id}/reaction", videoId)
                        .session(viewerSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": "LIKE"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Video not found"));
    }

    @Test
    void authorCanInteractWithOwnDraftVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("draft-engagement-owner");
        Long videoId = createVideo(authorSession, "Draft target", "Draft target description", "PRIVATE");

        mockMvc.perform(put("/api/videos/{id}/reaction", videoId)
                        .session(authorSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "type": "LIKE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.myReaction").value("LIKE"));
    }

    @Test
    void videoDetailReturnsEngagementState() throws Exception {
        MockHttpSession authorSession = registerAndLogin("detail-owner");
        MockHttpSession viewerSession = registerAndLogin("detail-viewer");
        Long videoId = createPublishedVideo(authorSession, "Detail target", "Detail target description", "PUBLIC");
        setReaction(viewerSession, videoId, "LIKE");
        addFavorite(viewerSession, videoId);

        mockMvc.perform(get("/api/videos/{id}", videoId).session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.dislikeCount").value(0))
                .andExpect(jsonPath("$.data.favoriteCount").value(1))
                .andExpect(jsonPath("$.data.myReaction").value("LIKE"))
                .andExpect(jsonPath("$.data.favorited").value(true));
    }

    @Test
    void myFavoritesRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/me/favorites"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void myFavoritesReturnsOnlyCurrentUsersFavoritesInDescendingOrder() throws Exception {
        MockHttpSession authorSession = registerAndLogin("favorites-list-owner");
        MockHttpSession viewerSession = registerAndLogin("favorites-list-viewer");
        MockHttpSession otherSession = registerAndLogin("favorites-list-other");
        Long firstVideoId = createPublishedVideo(authorSession, "First favorite", "First favorite description", "PUBLIC");
        Long secondVideoId = createPublishedVideo(authorSession, "Second favorite", "Second favorite description", "PUBLIC");
        Long otherVideoId = createPublishedVideo(authorSession, "Other favorite", "Other favorite description", "PUBLIC");

        addFavorite(viewerSession, firstVideoId);
        addFavorite(viewerSession, secondVideoId);
        addFavorite(otherSession, otherVideoId);

        MvcResult result = mockMvc.perform(get("/api/me/favorites").session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].favoritedAt").exists())
                .andReturn();

        assertEquals(
                java.util.List.of(secondVideoId.intValue(), firstVideoId.intValue()),
                readIntList(result, "$.data.content[*].id")
        );
    }

    @Test
    void deletingVideoAlsoDeletesEngagementRelations() throws Exception {
        MockHttpSession authorSession = registerAndLogin("delete-engagement-owner");
        MockHttpSession viewerSession = registerAndLogin("delete-engagement-viewer");
        Long videoId = createPublishedVideo(authorSession, "Delete engagement", "Delete engagement description", "PUBLIC");
        setReaction(viewerSession, videoId, "LIKE");
        addFavorite(viewerSession, videoId);

        assertEquals(1, videoReactionRepository.count());
        assertEquals(1, videoFavoriteRepository.count());

        mockMvc.perform(delete("/api/videos/{id}", videoId).session(authorSession))
                .andExpect(status().isOk());

        assertEquals(0, videoReactionRepository.count());
        assertEquals(0, videoFavoriteRepository.count());
    }
}
