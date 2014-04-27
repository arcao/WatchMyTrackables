package com.arcao.wmt.data.database.serializer;

import com.activeandroid.serializer.TypeSerializer;
import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.wmt.data.marshalling.SimpleGeocacheMarshaller;
import org.apache.commons.io.IOUtils;
import timber.log.Timber;

import java.io.*;

public class SimpleGeocacheSerializer extends TypeSerializer {
	@Override
	public Class<?> getDeserializedType() {
		return SimpleGeocache.class;
	}

	@Override
	public Class<?> getSerializedType() {
		return byte[].class;
	}

	@Override
	public Object serialize(Object data) {
		ByteArrayOutputStream bos = null;

		try {
			bos = new ByteArrayOutputStream();
			SimpleGeocacheMarshaller.INSTANCE.to(new ObjectOutputStream(bos), (SimpleGeocache) data);
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
		ByteArrayInputStream bis = null;

		try {
			bis = new ByteArrayInputStream((byte[]) data);
			return SimpleGeocacheMarshaller.INSTANCE.from(new ObjectInputStream(bis));
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
			return null;
		} finally {
			IOUtils.closeQuietly(bis);
		}
	}
}
