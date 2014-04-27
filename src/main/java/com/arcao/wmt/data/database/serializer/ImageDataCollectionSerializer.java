package com.arcao.wmt.data.database.serializer;

import com.activeandroid.serializer.TypeSerializer;
import com.arcao.geocaching.api.data.ImageData;
import com.arcao.wmt.data.database.pojo.ImageDataCollection;
import com.arcao.wmt.data.marshalling.ImageDataMarshaller;
import org.apache.commons.io.IOUtils;
import timber.log.Timber;

import java.io.*;

/**
 * Created by msloup on 27.4.2014.
 */
public class ImageDataCollectionSerializer extends TypeSerializer {
	@Override
	public Class<?> getDeserializedType() {
		return ImageDataCollection.class;
	}

	@Override
	public Class<?> getSerializedType() {
		return byte[].class;
	}

	@Override
	public Object serialize(Object data) {
		ByteArrayOutputStream bos = null;

		if (data == null)
			return null;

		try {
			bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			oos.writeInt(((ImageDataCollection) data).size());
			for (ImageData imageData : ((ImageDataCollection) data)) {
				ImageDataMarshaller.INSTANCE.to(oos, imageData);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
			return null;
		} finally {
			IOUtils.closeQuietly(bos);
		}
	}

	@Override
	public Object deserialize(Object data) {
		ImageDataCollection collection = new ImageDataCollection();
		ByteArrayInputStream bis = null;

		if (data == null)
			return null;

		try {
			bis = new ByteArrayInputStream((byte[]) data);
			ObjectInputStream ois = new ObjectInputStream(bis);
			int len = ois.readInt();
			for (int i = 0; i < len; i++) {
				collection.add(ImageDataMarshaller.INSTANCE.from(ois));
			}
			return collection;
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
			return collection;
		} finally {
			IOUtils.closeQuietly(bis);
		}
	}

}
