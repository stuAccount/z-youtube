package com.zyoutube;

import com.jayway.jsonpath.JsonPath;
import com.zyoutube.feature.account.AccountRepository;
import com.zyoutube.feature.comment.CommentRepository;
import com.zyoutube.feature.engagement.VideoFavoriteRepository;
import com.zyoutube.feature.engagement.VideoReactionRepository;
import com.zyoutube.feature.video.VideoRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
abstract class IntegrationTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected VideoRepository videoRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected VideoReactionRepository videoReactionRepository;

    @Autowired
    protected VideoFavoriteRepository videoFavoriteRepository;

    @AfterEach
    void cleanupDatabase() {
        videoReactionRepository.deleteAllInBatch();
        videoFavoriteRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        videoRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    protected ResultActions registerAccount(String username, String email, String password) throws Exception {
        return mockMvc.perform(post("/api/accounts/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "%s",
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(username, email, password)));
    }

    protected ResultActions login(String loginId, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "loginId": "%s",
                          "password": "%s"
                        }
                        """.formatted(loginId, password)));
    }

    protected ResultActions logout(MockHttpSession session) throws Exception {
        return mockMvc.perform(post("/api/auth/logout").session(session));
    }

    protected MockHttpSession registerAndLogin(String username) throws Exception {
        String email = username + "@example.com";
        String password = "Password123!";
        registerAccount(username, email, password)
                .andExpect(status().isOk());
        return loginSuccessfully(username, password);
    }

    protected MockHttpSession loginSuccessfully(String loginId, String password) throws Exception {
        MvcResult result = login(loginId, password)
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotNull(session, "login should create a session");
        return session;
    }

    protected Long createVideo(MockHttpSession session, String title, String description, String visibility) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/videos")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "description": "%s",
                                  "visibility": "%s"
                                }
                                """.formatted(title, description, visibility)))
                .andExpect(status().isOk())
                .andReturn();

        return readLong(result, "$.data.id");
    }

    protected Long createPublishedVideo(MockHttpSession session, String title, String description, String visibility)
            throws Exception {
        Long videoId = createVideo(session, title, description, visibility);
        publishVideo(session, videoId);
        return videoId;
    }

    protected void publishVideo(MockHttpSession session, Long videoId) throws Exception {
        mockMvc.perform(post("/api/videos/{id}/publish", videoId).session(session))
                .andExpect(status().isOk());
    }

    protected Long createComment(MockHttpSession session, Long videoId, String content) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "videoId": %d,
                                  "content": "%s"
                                }
                                """.formatted(videoId, content)))
                .andExpect(status().isOk())
                .andReturn();

        return readLong(result, "$.data.id");
    }

    protected MvcResult setReaction(MockHttpSession session, Long videoId, String reactionType) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/videos/{id}/reaction", videoId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "%s"
                                }
                                """.formatted(reactionType)))
                .andReturn();
    }

    protected MvcResult clearReaction(MockHttpSession session, Long videoId) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/videos/{id}/reaction", videoId)
                        .session(session))
                .andReturn();
    }

    protected MvcResult addFavorite(MockHttpSession session, Long videoId) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/videos/{id}/favorite", videoId)
                        .session(session))
                .andReturn();
    }

    protected MvcResult removeFavorite(MockHttpSession session, Long videoId) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/videos/{id}/favorite", videoId)
                        .session(session))
                .andReturn();
    }

    protected List<Integer> readIntList(MvcResult result, String path) throws Exception {
        List<Number> values = JsonPath.read(result.getResponse().getContentAsString(), path);
        return values.stream()
                .map(Number::intValue)
                .toList();
    }

    protected Long readLong(MvcResult result, String path) throws Exception {
        Number value = JsonPath.read(result.getResponse().getContentAsString(), path);
        return value.longValue();
    }

    protected String readString(MvcResult result, String path) throws Exception {
        return JsonPath.read(result.getResponse().getContentAsString(), path);
    }
}
