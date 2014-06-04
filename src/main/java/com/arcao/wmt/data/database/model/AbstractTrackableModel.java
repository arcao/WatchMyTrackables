package com.arcao.wmt.data.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.arcao.geocaching.api.data.Trackable;
import com.arcao.geocaching.api.data.User;
import com.arcao.wmt.data.database.pojo.ImageDataCollection;

import java.util.Date;

public abstract class AbstractTrackableModel extends Model {
	@Column(name = "TrackableId", index = true)
	public long trackableId;
	@Column(name = "GUID")
	public String guid;
	@Column(name = "Name")
	public String name;
	@Column(name = "Goal")
	public String goal;
	@Column(name = "Description")
	public String description;
	@Column(name = "TrackableTypeName")
	public String trackableTypeName;
	@Column(name = "TrackableTypeImage")
	public String trackableTypeImage;
	@Column(name = "Owner")
	public User owner;
	@Column(name = "CurrentCacheCode")
	public String currentCacheCode;
	@Column(name = "CurrentOwner")
	public User currentOwner;
	@Column(name = "TrackingNumber", index = true)
	public String trackingNumber;
	@Column(name = "LookupCode")
	public String lookupCode;
	@Column(name = "Created")
	public Date created;
	@Column(name = "AllowedToBeCollected")
	public boolean allowedToBeCollected;
	@Column(name = "InCollection")
	public boolean inCollection;
	@Column(name = "Archived")
	public boolean archived;

	@Column(name = "Images")
	public ImageDataCollection images;

	public AbstractTrackableModel() {
	}

	public AbstractTrackableModel(Trackable trackable) {
		apply(trackable);
	}

	public AbstractTrackableModel apply(Trackable trackable) {
		trackableId = trackable.getId();
		guid = trackable.getGuid();
		name = trackable.getName();
		goal = trackable.getGoal();
		description = trackable.getDescription();
		trackableTypeName = trackable.getTrackableTypeName();
		trackableTypeImage = trackable.getTrackableTypeImage();
		owner = trackable.getOwner();
		currentCacheCode = trackable.getCurrentCacheCode();
		currentOwner = trackable.getCurrentOwner();
		trackingNumber = trackable.getTrackingNumber();
		lookupCode = trackable.getLookupCode();
		created = trackable.getCreated();
		allowedToBeCollected = trackable.isAllowedToBeCollected();
		inCollection = trackable.isInCollection();
		archived = trackable.isArchived();

		images = new ImageDataCollection(trackable.getImages());

		return this;
	}
}
