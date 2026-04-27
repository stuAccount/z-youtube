package com.zyoutube;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends IntegrationTestSupport {
    @Test
    void loginFailureReturns401InsteadOf500() throws Exception {
        registerAccount("alice", "alice@example.com", "Password123!")
                .andExpect(status().isOk());

        login("alice", "wrong-password")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void loginSuccessReturnsExpectedFields() throws Exception {
        MvcResult registerResult = registerAccount("bob", "bob@example.com", "Password123!")
                .andExpect(status().isOk())
                .andReturn();

        Long accountId = readLong(registerResult, "$.data.id");

        login("bob", "Password123!")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.id").value(accountId.intValue()))
                .andExpect(jsonPath("$.data.username").value("bob"))
                .andExpect(jsonPath("$.data.email").value("bob@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("bob"));
    }

    @Test
    void meReturnsCurrentUserIdAfterLogin() throws Exception {
        MvcResult registerResult = registerAccount("carol", "carol@example.com", "Password123!")
                .andExpect(status().isOk())
                .andReturn();
        Long accountId = readLong(registerResult, "$.data.id");

        MockHttpSession session = loginSuccessfully("carol", "Password123!");

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(accountId.intValue()));
    }

    @Test
    void logoutClearsSession() throws Exception {
        registerAccount("dave", "dave@example.com", "Password123!")
                .andExpect(status().isOk());
        MockHttpSession session = loginSuccessfully("dave", "Password123!");

        logout(session)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }
}
