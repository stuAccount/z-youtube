package com.zyoutube;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

class AccountSubscriptionIntegrationTest extends IntegrationTestSupport {
    @Test
    void subscribeRequiresAuthentication() throws Exception {
        registerAccount("sub-target", "sub-target@example.com", "Password123!")
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/accounts/{username}/subscription", "sub-target"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanSubscribeAndProfileReflectsSubscriptionState() throws Exception {
        MockHttpSession targetSession = registerAndLogin("channel-owner");
        MockHttpSession subscriberSession = registerAndLogin("channel-fan");

        mockMvc.perform(put("/api/accounts/{username}/subscription", "channel-owner").session(subscriberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.targetUsername").value("channel-owner"))
                .andExpect(jsonPath("$.data.subscribed").value(true))
                .andExpect(jsonPath("$.data.subscriberCount").value(1));

        mockMvc.perform(get("/api/accounts/profile/{username}", "channel-owner").session(subscriberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subscriberCount").value(1))
                .andExpect(jsonPath("$.data.subscriptionCount").value(0))
                .andExpect(jsonPath("$.data.subscribedByCurrentUser").value(true));

        mockMvc.perform(get("/api/accounts/profile").session(targetSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subscriberCount").value(1))
                .andExpect(jsonPath("$.data.subscriptionCount").value(0));
    }

    @Test
    void repeatSubscribeAndUnsubscribeAreIdempotent() throws Exception {
        registerAndLogin("repeat-target");
        MockHttpSession subscriberSession = registerAndLogin("repeat-fan");

        mockMvc.perform(put("/api/accounts/{username}/subscription", "repeat-target").session(subscriberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subscribed").value(true))
                .andExpect(jsonPath("$.data.subscriberCount").value(1));

        mockMvc.perform(put("/api/accounts/{username}/subscription", "repeat-target").session(subscriberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subscribed").value(true))
                .andExpect(jsonPath("$.data.subscriberCount").value(1));

        assertEquals(1, accountSubscriptionRepository.count());

        mockMvc.perform(delete("/api/accounts/{username}/subscription", "repeat-target").session(subscriberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subscribed").value(false))
                .andExpect(jsonPath("$.data.subscriberCount").value(0));

        mockMvc.perform(delete("/api/accounts/{username}/subscription", "repeat-target").session(subscriberSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subscribed").value(false))
                .andExpect(jsonPath("$.data.subscriberCount").value(0));

        assertEquals(0, accountSubscriptionRepository.count());
    }

    @Test
    void userCannotSubscribeToSelf() throws Exception {
        MockHttpSession selfSession = registerAndLogin("self-owner");

        mockMvc.perform(put("/api/accounts/{username}/subscription", "self-owner").session(selfSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You cannot subscribe to yourself"));
    }

    @Test
    void profileShowsSubscriptionCountsForCurrentUser() throws Exception {
        MockHttpSession alphaSession = registerAndLogin("alpha-owner");
        MockHttpSession betaSession = registerAndLogin("beta-owner");
        MockHttpSession gammaSession = registerAndLogin("gamma-owner");

        mockMvc.perform(put("/api/accounts/{username}/subscription", "beta-owner").session(alphaSession))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/accounts/{username}/subscription", "gamma-owner").session(alphaSession))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/accounts/{username}/subscription", "alpha-owner").session(betaSession))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/accounts/profile").session(alphaSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subscriberCount").value(1))
                .andExpect(jsonPath("$.data.subscriptionCount").value(2));
    }
}
