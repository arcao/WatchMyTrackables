package com.arcao.wmt.data.database.serializer;

import com.activeandroid.serializer.TypeSerializer;
import com.arcao.geocaching.api.data.User;
import com.arcao.wmt.data.marshalling.UserMarshaller;
import org.apache.commons.io.IOUtils;
import timber.log.Timber;

import java.io.*;

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
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			UserMarshaller.INSTANCE.to(oos, (User) data);
			oos.flush();
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
		if (data == null || ((byte[]) data).length ==0)
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
