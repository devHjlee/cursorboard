package com.cursorboard.post.application;

import com.cursorboard.post.api.request.CommentRequest;
import com.cursorboard.post.api.response.CommentResponse;
import com.cursorboard.post.api.request.PostRequest;
import com.cursorboard.post.api.response.PostResponse;
import com.cursorboard.post.domain.Comment;
import com.cursorboard.post.domain.Post;
import com.cursorboard.post.infrastructure.CommentRepository;
import com.cursorboard.post.infrastructure.PostRepository;
import com.cursorboard.user.domain.User;
import com.cursorboard.user.domain.UserRole;
import com.cursorboard.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(String email, PostRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = new Post(request.getTitle(), request.getContent(), user);
        postRepository.save(post);

        return convertToPostResponse(post);
    }

    @Transactional
    public PostResponse updatePost(String email, Long postId, PostRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!post.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent());
        return convertToPostResponse(post);
    }

    @Transactional
    public void deletePost(String email, Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!post.getUser().equals(user) || user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
            .map(this::convertToPostResponse)
            .collect(Collectors.toList());
    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return convertToPostResponse(post);
    }

    @Transactional
    public CommentResponse createComment(String email, Long postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment(request.getContent(), user, post);
        commentRepository.save(comment);

        return convertToCommentResponse(comment);
    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!comment.getUser().equals(user) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    private PostResponse convertToPostResponse(Post post) {
        List<CommentResponse> commentResponses = post.getComments().stream()
            .map(this::convertToCommentResponse)
            .collect(Collectors.toList());

        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getUser().getEmail(),
            commentResponses,
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            comment.getUser().getEmail(),
            comment.getCreatedAt()
        );
    }
} 