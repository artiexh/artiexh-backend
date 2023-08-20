package com.artiexh.api.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.StorageService;
import com.artiexh.model.rest.file.FileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = Endpoint.File.ROOT)
public class StorageController {
	private final StorageService service;
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void upload(@Valid @ModelAttribute FileRequest request) {
		service.upload(request.getFile());
	}

	@GetMapping("download")
	@ResponseBody
	public ResponseEntity<byte[]> download(@RequestParam String presignedUrl) {
		try {
			S3Object s3Object = service.download(presignedUrl);
			String contentType = s3Object.getObjectMetadata().getContentType();
			var bytes = s3Object.getObjectContent().readAllBytes();

			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.valueOf(contentType));
			header.setContentLength(bytes.length);
			return ResponseEntity.ok().headers(header).body(bytes);
		} catch (IOException exception) {

		}
		return null;
	}

}
