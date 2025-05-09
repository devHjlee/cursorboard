package com.cursorboard.post.api;

import com.cursorboard.common.ApiResponse;
import com.cursorboard.post.api.request.CommentRequest;
import com.cursorboard.post.api.response.CommentResponse;
import com.cursorboard.post.api.request.PostRequest;
import com.cursorboard.post.api.response.PostResponse;
import com.cursorboard.post.application.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            Authentication authentication,
            @Valid @RequestBody PostRequest request) {
        String email = authentication.getName();
        PostResponse response = postService.createPost(email, request);
        return ResponseEntity.ok(ApiResponse.success(response, "게시글이 작성되었습니다."));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request) {
        String email = authentication.getName();
        PostResponse response = postService.updatePost(email, postId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "게시글이 수정되었습니다."));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            Authentication authentication,
            @PathVariable Long postId) {
        String email = authentication.getName();
        postService.deletePost(email, postId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(ApiResponse.success(posts, "게시글 목록을 조회했습니다."));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse post = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(post, "게시글을 조회했습니다."));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {
        String email = authentication.getName();
        CommentResponse response = postService.createComment(email, postId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "댓글이 작성되었습니다."));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            Authentication authentication,
            @PathVariable Long commentId) {
        String email = authentication.getName();
        postService.deleteComment(email, commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다."));
    }
} 