package com.arcao.wmt.data.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.arcao.geocaching.api.data.SimpleGeocache;
import com.arcao.geocaching.api.data.User;
import com.arcao.geocaching.api.data.coordinates.Coordinates;
import com.arcao.geocaching.api.data.type.CacheType;
import com.arcao.geocaching.api.data.type.ContainerType;

import java.util.Date;

public class ParcelableSimpleGeocache extends SimpleGeocache implements Parcelable {
	public ParcelableSimpleGeocache(long id, String cacheCode, String name, Coordinates coordinates, CacheType cacheType, float difficultyRating, float terrainRating, User author, boolean available, boolean archived, boolean premiumListing, Date created, Date placed, Date lastUpdated, String contactName, ContainerType containerType, int trackableCount, boolean found) {
		super(id, cacheCode, name, coordinates, cacheType, difficultyRating, terrainRating, author, available, archived, premiumListing, created, placed, lastUpdated, contactName, containerType, trackableCount, found);
	}

	private ParcelableSimpleGeocache(Parcel in) {
		this(
						in.readLong(),
						in.readString(),
						in.readString(),
						new Coordinates(in.readDouble(), in.readDouble()),
						CacheType.parseCacheTypeByGroundSpeakId(in.readInt()),
						in.readFloat(),
						in.readFloat(),
						ParcelableUser.CREATOR.createFromParcel(in),
						in.readByte() == 1,
						in.readByte() == 1,
						in.readByte() == 1,
						new Date(in.readLong()),
						new Date(in.readLong()),
						new Date(in.readLong()),
						in.readString(),
						ContainerType.parseContainerTypeByGroundSpeakId(in.readInt()),
						in.readInt(),
						in.readByte() == 1
		);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(getId());
		dest.writeString(getCacheCode());
		dest.writeString(getName());
		dest.writeDouble(getCoordinates().getLatitude());
		dest.writeDouble(getCoordinates().getLongitude());
		dest.writeInt(getCacheType().getGroundSpeakId());
		dest.writeFloat(getDifficultyRating());
		dest.writeFloat(getTerrainRating());
		ParcelableUser.from(getAuthor()).writeToParcel(dest, flags);
		dest.writeByte((byte) (isAvailable() ? 1 : 0));
		dest.writeByte((byte) (isArchived() ? 1 : 0));
		dest.writeByte((byte) (isPremiumListing() ? 1 : 0));
		dest.writeLong(getCreated().getTime());
		dest.writeLong(getPlaced().getTime());
		dest.writeLong(getLastUpdated().getTime());
		dest.writeString(getContactName());
		dest.writeInt(getContainerType().getGroundSpeakId());
		dest.writeInt(getTrackableCount());
		dest.writeByte((byte) (isFound() ? 1 : 0));
	}

	public static ParcelableSimpleGeocache from(SimpleGeocache simpleGeocache) {
		if (simpleGeocache instanceof ParcelableSimpleGeocache) {
			return (ParcelableSimpleGeocache) simpleGeocache;
		}

		return new ParcelableSimpleGeocache(
						simpleGeocache.getId(),
						simpleGeocache.getCacheCode(),
						simpleGeocache.getName(),
						simpleGeocache.getCoordinates(),
						simpleGeocache.getCacheType(),
						simpleGeocache.getDifficultyRating(),
						simpleGeocache.getTerrainRating(),
						ParcelableUser.from(simpleGeocache.getAuthor()),
						simpleGeocache.isAvailable(),
						simpleGeocache.isArchived(),
						simpleGeocache.isPremiumListing(),
						simpleGeocache.getCreated(),
						simpleGeocache.getPlaced(),
						simpleGeocache.getLastUpdated(),
						simpleGeocache.getContactName(),
						simpleGeocache.getContainerType(),
						simpleGeocache.getTrackableCount(),
						simpleGeocache.isFound()
		);
	}

	protected static final Parcelable.Creator<ParcelableSimpleGeocache> CREATOR = new Parcelable.Creator<ParcelableSimpleGeocache>() {
		public ParcelableSimpleGeocache createFromParcel(Parcel source) {
			return new ParcelableSimpleGeocache(source);
		}

		public ParcelableSimpleGeocache[] newArray(int size) {
			return new ParcelableSimpleGeocache[size];
		}

	};
}
