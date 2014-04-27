package com.arcao.wmt.data.database.pojo;

import com.arcao.geocaching.api.data.ImageData;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by msloup on 27.4.2014.
 */
public class ImageDataCollection extends ArrayList<ImageData> {
	public ImageDataCollection() {
	}

	public ImageDataCollection(Collection<? extends ImageData> collection) {
		super(collection);
	}
}
