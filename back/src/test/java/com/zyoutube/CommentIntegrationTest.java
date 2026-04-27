package com.zyoutube;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentIntegrationTest extends IntegrationTestSupport {
    @Test
    void authenticatedUserCanCommentOnPublicVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("public-owner");
        MockHttpSession viewerSession = registerAndLogin("public-viewer");
        Long videoId = createPublishedVideo(authorSession, "Public video", "Public description", "PUBLIC");

        MvcResult result = mockMvc.perform(post("/api/comments")
                        .session(viewerSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "videoId": %d,
                                  "content": "Great video"
                                }
                                """.formatted(videoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.videoId").value(videoId.intValue()))
                .andExpect(jsonPath("$.data.content").value("Great video"))
                .andReturn();

        Long commentId = readLong(result, "$.data.id");
        assertEquals(1, commentRepository.count());
        assertTrue(commentRepository.findById(commentId).isPresent());
    }

    @Test
    void authenticatedUserCanCommentOnUnlistedVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("unlisted-comment-owner");
        MockHttpSession viewerSession = registerAndLogin("unlisted-comment-viewer");
        Long videoId = createPublishedVideo(authorSession, "Unlisted video", "Unlisted description", "UNLISTED");

        createComment(viewerSession, videoId, "Hidden but reachable");

        assertEquals(1, commentRepository.count());
    }

    @Test
    void authorCannotCommentOnPrivatePublishedVideo() throws Exception {
        MockHttpSession authorSession = registerAndLogin("private-comment-owner");
        Long videoId = createPublishedVideo(authorSession, "Private comment", "Private description", "PRIVATE");

        mockMvc.perform(post("/api/comments")
                        .session(authorSession)
                        .contentType("application/json")
                        .content("""
                                {
                                  "videoId": %d,
                                  "content": "My own private comment"
                                }
                                """.formatted(videoId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Commenting is not allowed for this video"));
    }

    @Test
    void nonAuthorCannotReadPrivateVideoComments() throws Exception {
        MockHttpSession authorSession = registerAndLogin("private-comments-owner");
        MockHttpSession viewerSession = registerAndLogin("private-comments-viewer");
        Long videoId = createPublishedVideo(authorSession, "Private list", "Private list description", "PRIVATE");

        mockMvc.perform(get("/api/comments")
                        .session(viewerSession)
                        .param("videoId", videoId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Video not found"));
    }

    @Test
    void unauthenticatedUserCannotReadDraftVideoComments() throws Exception {
        MockHttpSession authorSession = registerAndLogin("draft-comments-owner");
        Long videoId = createVideo(authorSession, "Draft comments", "Draft comments description", "PUBLIC");

        mockMvc.perform(get("/api/comments").param("videoId", videoId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Video not found"));
    }

    @Test
    void deletingVideoAlsoDeletesItsComments() throws Exception {
        MockHttpSession authorSession = registerAndLogin("delete-video-owner");
        MockHttpSession viewerSession = registerAndLogin("delete-video-viewer");
        Long videoId = createPublishedVideo(authorSession, "Delete me", "Delete me description", "PUBLIC");
        createComment(viewerSession, videoId, "Comment to be deleted");

        mockMvc.perform(delete("/api/videos/{id}", videoId).session(authorSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertEquals(0, commentRepository.count());
        assertEquals(0, videoRepository.count());
    }
}
