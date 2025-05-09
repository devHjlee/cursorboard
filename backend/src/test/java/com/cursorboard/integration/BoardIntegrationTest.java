package com.cursorboard.integration;

import com.cursorboard.common.ApiResponse;
import com.cursorboard.post.api.request.CommentRequest;
import com.cursorboard.post.api.request.PostRequest;
import com.cursorboard.post.api.response.CommentResponse;
import com.cursorboard.post.api.response.PostResponse;
import com.cursorboard.user.api.request.UserRequest;
import com.cursorboard.user.api.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BoardIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl;
    private TestRestTemplate restTemplate = new TestRestTemplate();

    private String jwtToken;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // 1. 회원가입
        UserRequest signup = new UserRequest("test@cursor.com", "password123");
        restTemplate.postForEntity(baseUrl + "/api/auth/signup", signup, Void.class);

        // 2. 로그인 → 토큰 획득
        ParameterizedTypeReference<ApiResponse<UserResponse>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<ApiResponse<UserResponse>> loginResponse = restTemplate.exchange(
                baseUrl + "/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(signup),
                responseType
        );

        UserResponse response = loginResponse.getBody().getData();
        jwtToken = response.getToken();

        assertThat(jwtToken).isNotNull();
    }

    @Test
    @DisplayName("회원가입 → 로그인 → 글 등록 → 수정 → 댓글 등록 → 상세 조회까지 성공")
    void integrationFlow() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. 게시글 등록
        PostRequest postRequest = new PostRequest("제목입니다", "내용입니다");
        HttpEntity<PostRequest> postEntity = new HttpEntity<>(postRequest, headers);

        ParameterizedTypeReference<ApiResponse<PostResponse>> postType = new ParameterizedTypeReference<>() {};
        ResponseEntity<ApiResponse<PostResponse>> createPostResponse = restTemplate.exchange(
                baseUrl + "/api/posts",
                HttpMethod.POST,
                postEntity,
                postType
        );
        PostResponse postResponse = createPostResponse.getBody().getData();
        Long postId = postResponse.getId();
        assertThat(postId).isNotNull();

        // 4. 게시글 수정
        PostRequest updatedPost = new PostRequest("수정 제목", "수정 내용");
        HttpEntity<PostRequest> updateEntity = new HttpEntity<>(updatedPost, headers);

        ResponseEntity<ApiResponse<PostResponse>> updatePostResponse = restTemplate.exchange(
                baseUrl + "/api/posts/" + postId,
                HttpMethod.PUT,
                updateEntity,
                postType
        );

        // 5. 댓글 등록
        CommentRequest commentRequest = new CommentRequest("댓글 내용입니다.");
        HttpEntity<CommentRequest> commentEntity = new HttpEntity<>(commentRequest, headers);

        ParameterizedTypeReference<ApiResponse<CommentResponse>> commentType = new ParameterizedTypeReference<>() {};
        ResponseEntity<ApiResponse<CommentResponse>> commentResponse = restTemplate.exchange(
                baseUrl + "/api/posts/" + postId + "/comments",
                HttpMethod.POST,
                commentEntity,
                commentType
        );
        assertThat(commentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(commentResponse.getBody().getData()).isNotNull();

        // 6. 게시글 상세 조회
        ParameterizedTypeReference<ApiResponse<PostResponse>> detailType = new ParameterizedTypeReference<>() {};
        ResponseEntity<ApiResponse<PostResponse>> detailResponse = restTemplate.exchange(
                baseUrl + "/api/posts/" + postId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                detailType
        );

        PostResponse detail = detailResponse.getBody().getData();
        assertThat(detail.getTitle()).isEqualTo("수정 제목");
        assertThat(detail.getContent()).isEqualTo("수정 내용");
        assertThat(detail.getComments()).anyMatch(c -> c.getContent().equals("댓글 내용입니다."));
    }

}

