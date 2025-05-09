package com.cursorboard.post.application;

import com.cursorboard.post.api.request.CommentRequest;
import com.cursorboard.post.api.request.PostRequest;
import com.cursorboard.post.api.response.CommentResponse;
import com.cursorboard.post.api.response.PostResponse;
import com.cursorboard.post.domain.Comment;
import com.cursorboard.post.domain.Post;
import com.cursorboard.post.infrastructure.CommentRepository;
import com.cursorboard.post.infrastructure.PostRepository;
import com.cursorboard.user.domain.User;
import com.cursorboard.user.domain.UserRole;
import com.cursorboard.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private User admin;
    private PostRequest postRequest;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        user = new User("user1@example.com", passwordEncoder.encode("password"), UserRole.USER);
        admin = new User("admin1@example.com", passwordEncoder.encode("password"), UserRole.ADMIN);
        userRepository.save(user);
        userRepository.save(admin);

        postRequest = new PostRequest("테스트 제목", "테스트 내용");
        commentRequest = new CommentRequest("테스트 댓글");
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void createPostSuccess() {
        // when
        PostResponse response = postService.createPost(user.getEmail(), postRequest);

        // then
        assertThat(response.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(response.getContent()).isEqualTo(postRequest.getContent());
        assertThat(response.getAuthorEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("게시글 수정 성공 - 작성자")
    void updatePostSuccessByAuthor() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);
        PostRequest updateRequest = new PostRequest("수정된 제목", "수정된 내용");

        // when
        PostResponse response = postService.updatePost(user.getEmail(), post.getId(), updateRequest);

        // then
        assertThat(response.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(response.getContent()).isEqualTo(updateRequest.getContent());
    }

    @Test
    @DisplayName("게시글 수정 성공 - 관리자")
    void updatePostSuccessByAdmin() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);
        PostRequest updateRequest = new PostRequest("수정된 제목", "수정된 내용");

        // when
        PostResponse response = postService.updatePost(admin.getEmail(), post.getId(), updateRequest);

        // then
        assertThat(response.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(response.getContent()).isEqualTo(updateRequest.getContent());
    }

    @Test
    @DisplayName("게시글 수정 실패 - 권한 없음")
    void updatePostFailWithNoPermission() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);
        PostRequest updateRequest = new PostRequest("수정된 제목", "수정된 내용");
        User otherUser = new User("other@example.com", passwordEncoder.encode("password"), UserRole.USER);
        userRepository.save(otherUser);

        // when & then
        assertThatThrownBy(() -> postService.updatePost(otherUser.getEmail(), post.getId(), updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글을 수정할 권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글 삭제 성공 - 작성자")
    void deletePostSuccessByAuthor() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);

        // when
        postService.deletePost(user.getEmail(), post.getId());

        // then
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    @DisplayName("게시글 삭제 성공 - 관리자")
    void deletePostSuccessByAdmin() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);

        // when
        postService.deletePost(admin.getEmail(), post.getId());

        // then
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 권한 없음")
    void deletePostFailWithNoPermission() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);
        User otherUser = new User("other@example.com", passwordEncoder.encode("password"), UserRole.USER);
        userRepository.save(otherUser);

        // when & then
        assertThatThrownBy(() -> postService.deletePost(otherUser.getEmail(), post.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글을 삭제할 권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getAllPostsSuccess() {
        // given
        Post post1 = new Post("제목1", "내용1", user);
        Post post2 = new Post("제목2", "내용2", admin);
        postRepository.save(post1);
        postRepository.save(post2);

        // when
        List<PostResponse> posts = postService.getAllPosts();

        // then
        assertThat(posts).hasSize(2);
        assertThat(posts.get(0).getTitle()).isEqualTo("제목1");
        assertThat(posts.get(1).getTitle()).isEqualTo("제목2");
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void getPostSuccess() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);

        // when
        PostResponse response = postService.getPost(post.getId());

        // then
        assertThat(response.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(response.getContent()).isEqualTo(postRequest.getContent());
        assertThat(response.getAuthorEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void createCommentSuccess() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);

        // when
        CommentResponse response = postService.createComment(user.getEmail(), post.getId(), commentRequest);

        // then
        assertThat(response.getContent()).isEqualTo(commentRequest.getContent());
        assertThat(response.getAuthorEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 작성자")
    void deleteCommentSuccessByAuthor() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);
        Comment comment = new Comment(commentRequest.getContent(), user, post);
        commentRepository.save(comment);

        // when
        postService.deleteComment(user.getEmail(), comment.getId());

        // then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 관리자")
    void deleteCommentSuccessByAdmin() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);
        Comment comment = new Comment(commentRequest.getContent(), user, post);
        commentRepository.save(comment);

        // when
        postService.deleteComment(admin.getEmail(), comment.getId());

        // then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void deleteCommentFailWithNoPermission() {
        // given
        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), user);
        postRepository.save(post);
        Comment comment = new Comment(commentRequest.getContent(), user, post);
        commentRepository.save(comment);
        User otherUser = new User("other@example.com", passwordEncoder.encode("password"), UserRole.USER);
        userRepository.save(otherUser);

        // when & then
        assertThatThrownBy(() -> postService.deleteComment(otherUser.getEmail(), comment.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글을 삭제할 권한이 없습니다.");
    }
} 