package com.artiexh.api.service;

import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.model.rest.media.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface StorageService {
	FileResponse upload(MultipartFile multipartFile, Long userId) throws IOException;
	void updateSharedUsers(Long userId, Long[] sharedIds, Long mediaId);
	S3Object download(String fileName, Long userId) throws MalformedURLException;
}
