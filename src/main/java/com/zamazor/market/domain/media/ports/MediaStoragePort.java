package com.zamazor.market.domain.media.ports;

import com.zamazor.market.domain.media.model.StoredMediaMetadata;

import java.io.InputStream;

public interface MediaStoragePort {
	StoredMediaMetadata uploadImage(InputStream inputStream, String fileName, String folderPath);
	void deleteAsset(String publicId);
}
