package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Post.ROOT)
public class PostController {
	private final PostService postService;
	private final PostMapper postMapper;

	@PostMapping
	public PostDetail create(Authentication authentication, @Valid @RequestBody PostDetail postDetail) {
		Post post = postMapper.detailToDomain(postDetail);
		post = postService.create(postDetail);
		return postMapper.domainToDetail(post);
	}
}
