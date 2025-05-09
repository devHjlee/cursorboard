package com.cursorboard.user.api;

import com.cursorboard.common.ApiResponse;
import com.cursorboard.user.api.request.UserRequest;
import com.cursorboard.user.api.response.UserResponse;
import com.cursorboard.user.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody UserRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원 가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }
} 