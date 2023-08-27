package com.artiexh.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.api.config.S3Config;
import com.artiexh.api.config.S3ConfigurationProperties;
import com.artiexh.api.service.StorageService;
import com.artiexh.api.utils.S3Util;
import com.artiexh.model.rest.media.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class StorageServiceImpl implements StorageService {
	private final S3ConfigurationProperties s3Config;
	private final AmazonS3 s3;
	@Override
	public FileResponse upload(MultipartFile multipartFile, boolean isPublic) throws IOException {
		String fileUrl = null;
		File file = convertMultiPartToFile(multipartFile);
		String fileName = S3Util.generateFileName(multipartFile);
		if (isPublic) {
			fileUrl = S3Util.getPresignedString(s3Config.getRegion(), s3Config.getPublicBucketName(), s3Config.getAccessKey(), s3Config.getSecretKey(), fileName, true);

			s3.putObject(new PutObjectRequest(s3Config.getPublicBucketName(), fileName, file)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} else {
			s3.putObject(new PutObjectRequest(s3Config.getPrivateBucketName(), fileName, file));
		}
		file.delete();
		return FileResponse.builder()
				.presignedUrl(fileUrl)
				.fileName(fileName)
				.build();
	}

	@Override
	public S3Object download(String fileName) throws MalformedURLException {
		URL url = new URL(S3Util.getPresignedString(s3Config.getRegion(), s3Config.getPrivateBucketName(), s3Config.getAccessKey(), s3Config.getSecretKey(), fileName, false));
		return s3.download(new PresignedUrlDownloadRequest(
			url
		)).getS3Object();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
			//fos.close();
			return convFile;
		}
	}
}
