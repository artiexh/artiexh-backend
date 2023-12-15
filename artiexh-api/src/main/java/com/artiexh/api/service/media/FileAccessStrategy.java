package com.artiexh.api.service.media;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public interface FileAccessStrategy {
	String upload(File file, String fileName, boolean isPublic) throws IOException;

	FileStreamResponse download(String fileName) throws MalformedURLException;
}
