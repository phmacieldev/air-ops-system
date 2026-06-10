package com.air_ops_system.config;

import com.air_ops_system.auth.service.TokenService;
import com.air_ops_system.users.repository.UserRepository;
import com.air_ops_system.users.service.UserDetailsServiceImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Provides shared security-layer mocks for @WebMvcTest slices.
 *
 * We mock JwtAuthenticationFilter's *dependencies* (TokenService, UserRepository)
 * rather than the filter itself. This way the real filter runs, finds no Bearer token
 * in tests, and simply delegates to the next filter — letting the request reach the
 * controller. @WithMockUser then sets up the SecurityContext for individual test methods.
 */
@TestPropertySource(properties = {
    "cors.allowed-origins=http://localhost:3000",
    "app.jwt.secret=dGVzdHNlY3JldGtleXRoYXRpc2xvbmcxMjM0NTY3ODkwMTIzNDU2Nzg5MA=="
})
public abstract class ControllerTestBase {

  @MockitoBean
  protected TokenService tokenService;

  @MockitoBean
  protected UserRepository userRepository;

  @MockitoBean
  protected UserDetailsServiceImpl userDetailsServiceImpl;
}
