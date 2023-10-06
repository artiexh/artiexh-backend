package com.artiexh.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.api.config.S3ConfigurationProperties;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.StorageService;
import com.artiexh.api.utils.S3Util;
import com.artiexh.data.jpa.entity.AccountEntity;
import com.artiexh.data.jpa.entity.MediaEntity;
import com.artiexh.data.jpa.repository.AccountRepository;
import com.artiexh.data.jpa.repository.MediaRepository;
import com.artiexh.model.rest.media.FileResponse;
import com.artiexh.model.rest.media.FileResponseList;
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
import java.util.*;
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
	public FileResponse upload(MultipartFile multipartFile, Long userId) {
		String fileUrl = null;
		File file = null;
		MediaEntity entity = null;
		String fileName = null;
		try {
			file = convertMultiPartToFile(multipartFile);
			fileName = S3Util.generateFileName(multipartFile);
			if (userId == null) {
				fileUrl = S3Util.getPresignedString(s3Config.getRegion(), s3Config.getPublicBucketName(), s3Config.getAccessKey(), s3Config.getSecretKey(), fileName, true);

				s3.putObject(new PutObjectRequest(s3Config.getPublicBucketName(), fileName, file)
					.withCannedAcl(CannedAccessControlList.PublicRead));
			} else {

				s3.putObject(new PutObjectRequest(s3Config.getPrivateBucketName(), fileName, file));

				AccountEntity owner = accountRepository.getReferenceById(userId);
				entity = MediaEntity.builder()
					.owner(owner)
					.fileName(fileName)
					.name(multipartFile.getOriginalFilename().replace(" ", "_"))
					.sharedUsers(Collections.emptySet())
					.build();
				mediaRepository.save(entity);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}
		return FileResponse.builder()
			.id(entity == null ? null : entity.getId())
			.presignedUrl(fileUrl)
			.name(entity == null ? "" : entity.getName())
			.fileName(fileName)
			.build();
	}

	@Override
	@Transactional
	public FileResponseList upload(List<MultipartFile> multipartFile, Long userId) {
		List<FileResponse> fileResponses = new ArrayList<>();
		for (MultipartFile file : multipartFile) {
			FileResponse fileResponse = upload(file, userId);
			fileResponses.add(fileResponse);
		}
		return FileResponseList.builder()
			.fileResponses(fileResponses)
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

		if (Arrays.stream(sharedIds).anyMatch(id -> id.equals(entity.getOwner().getId()))) {
			throw new IllegalArgumentException(ErrorCode.OWNER_NOT_ALLOWED.getMessage());
		}
		entity.setSharedUsers(Arrays.stream(sharedIds)
			.map(sharedId -> AccountEntity.builder().id(sharedId).build())
			.collect(Collectors.toSet()));
		mediaRepository.save(entity);
	}

	@Override
	public S3Object download(Long id, Long userId, boolean isStaff) throws MalformedURLException {
		String fileName;
		if (!isStaff) {
			MediaEntity media = mediaRepository.findByIdAndSharedUsersId(id, userId).orElseThrow(EntityNotFoundException::new);
			fileName = media.getFileName();
		} else {
			MediaEntity media = mediaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            fileName = media.getFileName();
		}

		URL url = new URL(S3Util.getPresignedString(s3Config.getRegion(), s3Config.getPrivateBucketName(), s3Config.getAccessKey(), s3Config.getSecretKey(), fileName, false));
		return s3.download(new PresignedUrlDownloadRequest(
			url
		)).getS3Object();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
			return convFile;
		}
	}
}
