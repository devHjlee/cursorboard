package com.cursorboard.post.infrastructure;

import com.cursorboard.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
} 