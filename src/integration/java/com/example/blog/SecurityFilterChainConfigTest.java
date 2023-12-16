package com.example.blog;

import com.example.blog.security.JwtAuthenticationTokenFilter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SecurityFilterChainConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtAuthenticationTokenFilter jwtFilter;

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/categories",
            "/api/v1/tags",
            "/api/v1/users",
            "/api/v1/posts/1/comments",
            "/api/v1/posts",
            "/api/v1/posts"})
    public void testSecuredPostEndpointsWithoutAuthentication(String endpoint) throws Exception {
        mockMvc.perform(post(endpoint)).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/categories/1",
            "/api/v1/tags/1",
            "/api/v1/posts/1/comments/1",
            "/api/v1/posts/1",
            "/api/v1/users/1/promote-to-admin",
            "/api/v1/users/1/remove-admin-role"})
    public void testSecuredPutEndpointsWithoutAuthentication(String endpoint) throws Exception {
        mockMvc.perform(put(endpoint)).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/categories/1",
            "/api/v1/tags/1",
            "/api/v1/posts/1/comments/1",
            "/api/v1/posts/1"})
    public void testSecuredDeleteEndpointsWithoutAuthentication(String endpoint) throws Exception {
        mockMvc.perform(delete(endpoint)).andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/users/me",
            "/api/v1/users/me/comments"})
    public void testSecuredGetEndpointsWithoutAuthentication(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint)).andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user", roles = "USER")
    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/categories",
            "/api/v1/tags",
            "/api/v1/users"
    })
    public void testSecuredPostEndpointForAdminOnlyWithUserAuthentication(String endpoint) throws Exception {
        mockMvc.perform(post(endpoint))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user", roles = "USER")
    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/categories/1",
            "/api/v1/tags/1",
            "/api/v1/users/1/promote-to-admin",
            "/api/v1/users/*/remove-admin-role"
    })
    public void testSecuredPutEndpointForAdminOnlyWithUserAuthentication(String endpoint) throws Exception {
        mockMvc.perform(put(endpoint))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user", roles = "USER")
    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/categories/1",
            "/api/v1/tags/1"
    })
    public void testSecuredDeleteEndpointForAdminOnlyWithUserAuthentication(String endpoint) throws Exception {
        mockMvc.perform(delete(endpoint))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user", roles = "ADMIN")
    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/users/1/promote-to-admin",
            "/api/v1/users/1/remove-admin-role"
    })
    public void testSecuredPutEndpointForSuperAdminOnlyWithAdminAuthentication(String endpoint) throws Exception {
        mockMvc.perform(put(endpoint))
                .andExpect(status().isForbidden());
    }
}