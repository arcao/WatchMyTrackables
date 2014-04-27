package com.arcao.wmt.data.database.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.arcao.geocaching.api.data.SimpleGeocache;

/**
 * Created by msloup on 27.4.2014.
 */
@Table(name = "Geocaches")
public class GeocacheModel extends Model {
	@Column(name = "CacheCode", index = true)
	public String cacheCode;

	@Column(name = "Data")
	public SimpleGeocache data;

	public GeocacheModel() {
	}

	public GeocacheModel(String cacheCode, SimpleGeocache data) {
		this.cacheCode = cacheCode;
		this.data = data;
	}
}
