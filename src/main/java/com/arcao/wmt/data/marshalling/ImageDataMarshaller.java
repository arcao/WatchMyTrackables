package com.arcao.wmt.data.marshalling;

import com.arcao.geocaching.api.data.ImageData;
import com.arcao.utils.marshalling.Marshaller;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Created by msloup on 27.4.2014.
 */
public class ImageDataMarshaller implements Marshaller<ImageData> {
	public static final Marshaller<ImageData> INSTANCE = new ImageDataMarshaller();
	protected static int VERSION = 1;

	protected ImageDataMarshaller() {}

	@Override
	public ImageData from(ObjectInput in) throws IOException {
		if (in.readInt() != VERSION)
			return null;

		if (in.readBoolean())
			return null;

		return new ImageData(
						in.readUTF(),
						in.readUTF(),
						in.readUTF(),
						in.readUTF(),
						in.readUTF()
		);
	}

	@Override
	public void to(ObjectOutput out, ImageData data) throws IOException {
		out.writeInt(VERSION);

		out.writeBoolean(data == null);
		if (data == null)
			return;

		out.writeUTF(data.getDescription());
		out.writeUTF(data.getMobileUrl());
		out.writeUTF(data.getName());
		out.writeUTF(data.getThumbUrl());
		out.writeUTF(data.getUrl());
	}
}
