package com.zamazor.market.media.ports;

import com.zamazor.market.media.model.StoredMediaMetadata;

import java.io.InputStream;

public interface MediaStoragePort {
	StoredMediaMetadata upload(InputStream inputStream, String fileName, String folderPath);
	void delete(String publicId);
}
