package com.artiexh.api.service.media.impl;

import com.artiexh.api.service.media.FileAccessStrategy;
import com.artiexh.api.service.media.FileStreamResponse;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SystemFileAccessStrategy implements FileAccessStrategy {

	@Override
	public String upload(File file, String fileName, boolean isPublic) {
		return null;
	}

	@Override
	public FileStreamResponse download(String fileName) {
		return null;
	}
}
