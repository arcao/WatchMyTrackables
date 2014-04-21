package com.arcao.wmt.data.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.arcao.geocaching.api.data.User;
import com.arcao.geocaching.api.data.coordinates.Coordinates;
import com.arcao.geocaching.api.data.type.MemberType;

public class ParcelableUser extends User implements Parcelable {
	public ParcelableUser(String avatarUrl, int findCount, int hideCount, Coordinates homeCoordinates, long id, boolean admin, MemberType memberType, String publicGuid, String userName) {
		super(avatarUrl, findCount, hideCount, homeCoordinates, id, admin, memberType, publicGuid, userName);
	}

	private ParcelableUser(Parcel in) {
		this(
						in.readString(),
						in.readInt(),
						in.readInt(),
						new Coordinates(in.readDouble(), in.readDouble()),
						in.readLong(),
						in.readByte() == 1,
						MemberType.parseMemeberTypeByGroundSpeakId(in.readInt()),
						in.readString(),
						in.readString()
		);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getAvatarUrl());
		dest.writeInt(getFindCount());
		dest.writeInt(getHideCount());
		dest.writeDouble(getHomeCoordinates().getLatitude());
		dest.writeDouble(getHomeCoordinates().getLongitude());
		dest.writeLong(getId());
		dest.writeByte((byte) (isAdmin() ? 1 : 0));
		dest.writeInt(getMemberType().getGroundSpeakId());
		dest.writeString(getPublicGuid());
		dest.writeString(getUserName());
	}

	public static ParcelableUser from(User user) {
		if (user instanceof ParcelableUser) {
			return (ParcelableUser) user;
		}

		return new ParcelableUser(
						user.getAvatarUrl(),
						user.getFindCount(),
						user.getHideCount(),
						user.getHomeCoordinates(),
						user.getId(),
						user.isAdmin(),
						user.getMemberType(),
						user.getPublicGuid(),
						user.getUserName()
		);
	}

	protected static final Parcelable.Creator<ParcelableUser> CREATOR = new Parcelable.Creator<ParcelableUser>() {
		public ParcelableUser createFromParcel(Parcel source) {
			return new ParcelableUser(source);
		}

		public ParcelableUser[] newArray(int size) {
			return new ParcelableUser[size];
		}

	};
}
