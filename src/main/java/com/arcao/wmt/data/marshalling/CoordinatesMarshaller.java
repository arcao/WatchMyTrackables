package com.arcao.wmt.data.marshalling;

import com.arcao.geocaching.api.data.coordinates.Coordinates;
import com.arcao.utils.marshalling.Marshaller;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Created by msloup on 27.4.2014.
 */
public class CoordinatesMarshaller implements Marshaller<Coordinates> {
	public static final Marshaller<Coordinates> INSTANCE =  new CoordinatesMarshaller();
	protected static int VERSION = 1;

	protected CoordinatesMarshaller() {}

	@Override
	public Coordinates from(ObjectInput in) throws IOException {
		if (in.readInt() != VERSION)
			return null;

		if (in.readBoolean())
			return null;

		return new Coordinates(
						in.readDouble(),
						in.readDouble()
		);
	}

	@Override
	public void to(ObjectOutput out, Coordinates data) throws IOException {
		out.writeInt(VERSION);

		out.writeBoolean(data == null);
		if (data == null)
			return;

		out.writeDouble(data.getLatitude());
		out.writeDouble(data.getLongitude());
	}
}
