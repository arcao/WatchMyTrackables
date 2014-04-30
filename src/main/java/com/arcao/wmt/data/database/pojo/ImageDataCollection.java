package com.arcao.wmt.data.database.pojo;

import com.arcao.geocaching.api.data.ImageData;

import java.util.ArrayList;
import java.util.Collection;

public class ImageDataCollection extends ArrayList<ImageData> {
	public ImageDataCollection() {}

	public ImageDataCollection(Collection<? extends ImageData> collection) {
		super(collection);
	}
}
