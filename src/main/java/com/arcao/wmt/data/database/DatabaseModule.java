package com.arcao.wmt.data.database;

import android.app.Application;
import com.activeandroid.Configuration;
import com.arcao.wmt.data.database.model.FavoritedTrackableModel;
import com.arcao.wmt.data.database.model.GeocacheModel;
import com.arcao.wmt.data.database.model.MyTrackableModel;
import com.arcao.wmt.data.database.serializer.ImageDataCollectionSerializer;
import com.arcao.wmt.data.database.serializer.SimpleGeocacheSerializer;
import com.arcao.wmt.data.database.serializer.UserSerializer;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(
				complete = false,
				library = true
)
public final class DatabaseModule {
	@Provides
	@Singleton
	Configuration provideDatabaseConfiguration(Application app) {
		return new Configuration.Builder(app)
						.setDatabaseVersion(1)
						.addTypeSerializer(SimpleGeocacheSerializer.class)
						.addTypeSerializer(UserSerializer.class)
						.addTypeSerializer(ImageDataCollectionSerializer.class)
						.addModelClass(GeocacheModel.class)
						.addModelClass(MyTrackableModel.class)
						.addModelClass(FavoritedTrackableModel.class)
						.create();
	}
}
