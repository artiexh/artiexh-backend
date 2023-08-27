package com.artiexh.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.api.config.S3Config;
import com.artiexh.api.config.S3ConfigurationProperties;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.StorageService;
import com.artiexh.api.utils.S3Util;
import com.artiexh.data.jpa.entity.AccountEntity;
import com.artiexh.data.jpa.entity.MediaEntity;
import com.artiexh.data.jpa.repository.AccountRepository;
import com.artiexh.data.jpa.repository.MediaRepository;
import com.artiexh.model.rest.media.FileResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StorageServiceImpl implements StorageService {
	private final S3ConfigurationProperties s3Config;
	private final AmazonS3 s3;
	private final MediaRepository mediaRepository;
	private final AccountRepository accountRepository;
	@Override
	@Transactional
	public FileResponse upload(MultipartFile multipartFile, Long userId) throws IOException {
		String fileUrl = null;
		File file = convertMultiPartToFile(multipartFile);
		String fileName = S3Util.generateFileName(multipartFile);
		if (userId == null) {
			fileUrl = S3Util.getPresignedString(s3Config.getRegion(), s3Config.getPublicBucketName(), s3Config.getAccessKey(), s3Config.getSecretKey(), fileName, true);

			s3.putObject(new PutObjectRequest(s3Config.getPublicBucketName(), fileName, file)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} else {

			s3.putObject(new PutObjectRequest(s3Config.getPrivateBucketName(), fileName, file));

			AccountEntity owner = accountRepository.getReferenceById(userId);
			MediaEntity entity = MediaEntity.builder()
				.owner(owner)
				.fileName(fileName)
				.sharedUsers(Collections.emptySet())
				.build();
			mediaRepository.save(entity);
		}
		file.delete();
		return FileResponse.builder()
				.presignedUrl(fileUrl)
				.fileName(fileName)
				.build();
	}

	@Override
	@Transactional
	public void updateSharedUsers(Long userId, Long[] sharedIds, Long mediaId) {
		int numOfNotExisted = accountRepository.countByIdsIn(sharedIds);
		if (numOfNotExisted < sharedIds.length) {
			throw new IllegalArgumentException(ErrorCode.ACCOUNT_INFO_NOT_FOUND.getMessage());
		}
		MediaEntity entity = mediaRepository.findByIdAndOwnerId(mediaId, userId).orElseThrow(EntityNotFoundException::new);
		entity.setSharedUsers(Arrays.stream(sharedIds)
			.map(sharedId -> AccountEntity.builder().id(sharedId).build())
			.collect(Collectors.toSet()));
		mediaRepository.save(entity);
	}

	@Override
	public S3Object download(String fileName, Long userId) throws MalformedURLException {
		mediaRepository.findByFileNameAndSharedUsersId(fileName, userId).orElseThrow(EntityNotFoundException::new);
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
