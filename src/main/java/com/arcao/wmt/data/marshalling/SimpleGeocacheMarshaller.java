package com.arcao.wmt.data.marshalling;

import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.geocaching.api.data.type.CacheType;
import com.arcao.geocaching.api.data.type.ContainerType;
import com.arcao.utils.marshalling.Marshaller;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * Created by msloup on 27.4.2014.
 */
public class SimpleGeocacheMarshaller implements Marshaller<SimpleGeocache> {
	public static final Marshaller<SimpleGeocache> INSTANCE = new SimpleGeocacheMarshaller();
	protected static int VERSION = 1;

	protected SimpleGeocacheMarshaller() {}

	@Override
	public SimpleGeocache from(ObjectInput in) throws IOException {
		if (in.readInt() != VERSION)
			return null;

		return new SimpleGeocache(
						in.readLong(),
						in.readUTF(),
						in.readUTF(),
						CoordinatesMarshaller.INSTANCE.from(in),
						CacheType.parseCacheTypeByGroundSpeakId(in.readInt()),
						in.readFloat(),
						in.readFloat(),
						UserMarshaller.INSTANCE.from(in),
						in.readBoolean(),
						in.readBoolean(),
						in.readBoolean(),
						new Date(in.readLong()),
						new Date(in.readLong()),
						new Date(in.readLong()),
						in.readUTF(),
						ContainerType.parseContainerTypeByGroundSpeakId(in.readInt()),
						in.readInt(),
						in.readBoolean()
		);
	}

	@Override
	public void to(ObjectOutput out, SimpleGeocache data) throws IOException {
		out.writeInt(VERSION);

		out.writeLong(data.getId());
		out.writeUTF(data.getCacheCode());
		out.writeUTF(data.getName());
		CoordinatesMarshaller.INSTANCE.to(out, data.getCoordinates());
		out.writeInt(data.getCacheType().getGroundSpeakId());
		out.writeFloat(data.getDifficultyRating());
		out.writeFloat(data.getTerrainRating());
		UserMarshaller.INSTANCE.to(out, data.getAuthor());
		out.writeBoolean(data.isAvailable());
		out.writeBoolean(data.isArchived());
		out.writeBoolean(data.isPremiumListing());
		out.writeLong(data.getCreated().getTime());
		out.writeLong(data.getPlaced().getTime());
		out.writeLong(data.getLastUpdated().getTime());
		out.writeUTF(data.getContactName());
		out.writeInt(data.getContainerType().getGroundSpeakId());
		out.writeInt(data.getTrackableCount());
		out.writeBoolean(data.isFound());
	}
}
