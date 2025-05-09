package com.cursorboard.user.application;

import com.cursorboard.user.api.request.UserRequest;
import com.cursorboard.user.api.response.UserResponse;
import com.cursorboard.user.domain.User;
import com.cursorboard.user.domain.UserRole;
import com.cursorboard.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRequest signupRequest;
    private UserRequest loginRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new UserRequest("test@example.com", "password123");
        loginRequest = new UserRequest("test@example.com", "password123");
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        // when
        userService.signup(signupRequest);

        // then
        User savedUser = userRepository.findByEmail(signupRequest.getEmail()).orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo(signupRequest.getEmail());
        assertThat(passwordEncoder.matches(signupRequest.getPassword(), savedUser.getPassword())).isTrue();
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 실패")
    void signupFailWithDuplicateEmail() {
        // given
        userService.signup(signupRequest);

        // when & then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        userService.signup(signupRequest);

        // when
        UserResponse response = userService.login(loginRequest);

        // then
        assertThat(response.getEmail()).isEqualTo(loginRequest.getEmail());
        assertThat(response.getToken()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 실패")
    void loginFailWithNonExistentEmail() {
        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void loginFailWithWrongPassword() {
        // given
        userService.signup(signupRequest);
        UserRequest wrongPasswordRequest = new UserRequest("test@example.com", "wrongpassword");

        // when & then
        assertThatThrownBy(() -> userService.login(wrongPasswordRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
} 