package com.artiexh.api.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.StorageService;
import com.artiexh.model.rest.media.FileResponse;
import com.artiexh.model.rest.media.UpdateSharedUsersRequest;
import com.artiexh.model.rest.media.UploadRequest;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.Media.ROOT)
public class MediaController {
	private final StorageService storageService;
	@PostMapping(path = Endpoint.Media.PUBLIC_UPLOAD,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public FileResponse publicUpload( @Valid @ModelAttribute UploadRequest request) {
		try {
			return storageService.upload(request.getFile(), null);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UPLOAD_FAILED.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		}

	}

	@PostMapping(path = Endpoint.Media.UPLOAD,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public FileResponse upload(Authentication authentication,
							   @Valid @ModelAttribute UploadRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {
			return storageService.upload(request.getFile(), userId);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UPLOAD_FAILED.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		}
	}

	@PutMapping(path = Endpoint.Media.DETAIL)
	public void updateSharedUsers(Authentication authentication,
										  @PathVariable Long id,
										  @Valid @RequestBody UpdateSharedUsersRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {
			storageService.updateSharedUsers(userId, request.getSharedUserIds(), id);
		} catch (EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.MEDIA_NOT_FOUND.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}

	@GetMapping("download")
	public ResponseEntity<byte[]> download(Authentication authentication, @RequestParam String fileName) {
		var userId = (Long) authentication.getPrincipal();
		try {
			S3Object s3Object = storageService.download(fileName, userId);
			String contentType = s3Object.getObjectMetadata().getContentType();
			var bytes = s3Object.getObjectContent().readAllBytes();

			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.valueOf(contentType));
			header.setContentLength(bytes.length);
			return ResponseEntity.ok().headers(header).body(bytes);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DOWNLOAD_FAILED.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		} catch (EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.DOWNLOAD_NOT_ALLOWED.getMessage(), e);
		}
	}
}
