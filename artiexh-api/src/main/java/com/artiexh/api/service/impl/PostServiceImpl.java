package com.artiexh.api.service.impl;

import com.artiexh.api.service.PostService;
import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.PostCommentEntity;
import com.artiexh.data.jpa.entity.PostEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.data.jpa.repository.PostCommentRepository;
import com.artiexh.data.jpa.repository.PostRepository;
import com.artiexh.data.jpa.repository.UserRepository;
import com.artiexh.model.domain.Post;
import com.artiexh.model.domain.PostComment;
import com.artiexh.model.mapper.PostCommentMapper;
import com.artiexh.model.mapper.PostMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final PostCommentRepository postCommentRepository;
	private final ArtistRepository artistRepository;
	private final UserRepository userRepository;
	private final PostMapper postMapper;
	private final PostCommentMapper postCommentMapper;
	@Override
	public Post create(Long userId, Post post) {
		ArtistEntity artist = artistRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
		PostEntity postEntity = postMapper.domainToEntity(post);

		postEntity.setOwner(artist);

		postRepository.save(postEntity);
		return postMapper.entityToDomain(postEntity);
	}

	@Override
	public Page<Post> getAllPost(Long userId, Pageable pageable) {
		Page<PostEntity> posts = postRepository.findAllByOwnerId(userId, pageable);
		return posts.map(postMapper::entityToDomain);
	}

	@Override
	public PostComment create(Long userId, Long postId, PostComment postComment) {
		UserEntity user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
		PostEntity post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);

		PostCommentEntity postCommentEntity = postCommentMapper.domainToEntity(postComment);

		postCommentEntity.setOwner(user);
		postCommentEntity.setPost(post);

		postCommentRepository.save(postCommentEntity);
		return postCommentMapper.entityToDomain(postCommentEntity);
	}

	@Override
	public Page<PostComment> getAllPostComment(Long postId, Pageable pageable) {
		Page<PostCommentEntity> postComments = postCommentRepository.findAllByPostId(postId, pageable);
		return postComments.map(postCommentMapper::entityToDomain);
	}
}
