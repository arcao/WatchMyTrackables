package com.arcao.wmt.data.marshalling;

import com.arcao.geocaching.api.data.User;
import com.arcao.geocaching.api.data.type.MemberType;
import com.arcao.utils.marshalling.Marshaller;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Created by msloup on 27.4.2014.
 */
public class UserMarshaller implements Marshaller<User> {
	public static final Marshaller<User> INSTANCE = new UserMarshaller();
	protected static int VERSION = 1;

	protected UserMarshaller() {}

	@Override
	public User from(ObjectInput in) throws IOException {
		if (in.readInt() != VERSION)
			return null;

		if (in.readBoolean())
			return null;

		return new User(
						in.readUTF(),
						in.readInt(),
						in.readInt(),
						CoordinatesMarshaller.INSTANCE.from(in),
						in.readLong(),
						in.readBoolean(),
						MemberType.parseMemeberTypeByGroundSpeakId(in.readInt()),
						in.readUTF(),
						in.readUTF()
		);
	}

	@Override
	public void to(ObjectOutput out, User data) throws IOException {
		out.writeInt(VERSION);

		out.writeBoolean(data == null);
		if (data == null)
			return;

		out.writeUTF(data.getAvatarUrl());
		out.writeInt(data.getFindCount());
		out.writeInt(data.getHideCount());
		CoordinatesMarshaller.INSTANCE.to(out, data.getHomeCoordinates());
		out.writeLong(data.getId());
		out.writeBoolean(data.isAdmin());
		out.writeInt(data.getMemberType().getGroundSpeakId());
		out.writeUTF(data.getPublicGuid());
		out.writeUTF(data.getUserName());
	}
}
