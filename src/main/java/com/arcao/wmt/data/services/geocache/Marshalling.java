package com.arcao.wmt.data.services.geocache;

import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.geocaching.api.data.User;
import com.arcao.geocaching.api.data.coordinates.Coordinates;
import com.arcao.geocaching.api.data.type.CacheType;
import com.arcao.geocaching.api.data.type.ContainerType;
import com.arcao.geocaching.api.data.type.MemberType;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

public class Marshalling {
	public interface Marshaller<Object> {
		Object from(ObjectInput in) throws IOException;
		void to(ObjectOutput out, Object data) throws IOException;
	}

	public static final Marshaller<SimpleGeocache> SIMPLE_GEOCACHE = new Marshaller<SimpleGeocache>() {
		@Override
		public SimpleGeocache from(ObjectInput in) throws IOException {
			return new SimpleGeocache(
							in.readLong(),
							in.readUTF(),
							in.readUTF(),
							COORDINATES.from(in),
							CacheType.parseCacheTypeByGroundSpeakId(in.readInt()),
							in.readFloat(),
							in.readFloat(),
							USER.from(in),
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
			out.writeLong(data.getId());
			out.writeUTF(data.getCacheCode());
			out.writeUTF(data.getName());
			COORDINATES.to(out, data.getCoordinates());
			out.writeInt(data.getCacheType().getGroundSpeakId());
			out.writeFloat(data.getDifficultyRating());
			out.writeFloat(data.getTerrainRating());
			USER.to(out, data.getAuthor());
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
	};

	public static final Marshaller<User> USER = new Marshaller<User>() {
		@Override
		public User from(ObjectInput in) throws IOException {
			return new User(
							in.readUTF(),
							in.readInt(),
							in.readInt(),
							COORDINATES.from(in),
							in.readLong(),
							in.readBoolean(),
							MemberType.parseMemeberTypeByGroundSpeakId(in.readInt()),
							in.readUTF(),
							in.readUTF()
			);
		}

		@Override
		public void to(ObjectOutput out, User data) throws IOException {
			out.writeUTF(data.getAvatarUrl());
			out.writeInt(data.getFindCount());
			out.writeInt(data.getHideCount());
			COORDINATES.to(out, data.getHomeCoordinates());
			out.writeLong(data.getId());
			out.writeBoolean(data.isAdmin());
			out.writeInt(data.getMemberType().getGroundSpeakId());
			out.writeUTF(data.getPublicGuid());
			out.writeUTF(data.getUserName());
		}
	};

	public static final Marshaller<Coordinates> COORDINATES = new Marshaller<Coordinates>() {
		@Override
		public Coordinates from(ObjectInput in) throws IOException {
			return new Coordinates(
							in.readDouble(),
							in.readDouble()
			);
		}

		@Override
		public void to(ObjectOutput out, Coordinates data) throws IOException {
			out.writeDouble(data.getLatitude());
			out.writeDouble(data.getLongitude());
		}
	};
}
