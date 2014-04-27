package com.arcao.wmt.data.database.serializer;

import com.activeandroid.serializer.TypeSerializer;
import com.arcao.geocaching.api.data.User;
import com.arcao.wmt.data.marshalling.UserMarshaller;
import org.apache.commons.io.IOUtils;
import timber.log.Timber;

import java.io.*;

/**
 * Created by msloup on 27.4.2014.
 */
public class UserSerializer extends TypeSerializer {
	@Override
	public Class<?> getDeserializedType() {
		return User.class;
	}

	@Override
	public Class<?> getSerializedType() {
		return byte[].class;
	}

	@Override
	public Object serialize(Object data) {
		if (data == null)
			return null;

		ByteArrayOutputStream bos = null;

		try {
			bos = new ByteArrayOutputStream();
			UserMarshaller.INSTANCE.to(new ObjectOutputStream(bos), (User) data);
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
		if (data == null)
			return null;

		ByteArrayInputStream bis = null;

		try {
			bis = new ByteArrayInputStream((byte[]) data);
			return UserMarshaller.INSTANCE.from(new ObjectInputStream(bis));
		} catch (IOException e) {
			Timber.e(e, e.getMessage());
			return null;
		} finally {
			IOUtils.closeQuietly(bis);
		}
	}
}
