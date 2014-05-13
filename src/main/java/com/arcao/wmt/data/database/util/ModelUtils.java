package com.arcao.wmt.data.database.util;

import android.database.Cursor;
import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import timber.log.Timber;

import java.lang.reflect.Constructor;

/**
 * Created by msloup on 11.5.2014.
 */
public class ModelUtils {
	@SuppressWarnings("unchecked")
	public static <M extends Model> M getModelFromCursor(Class<M> modelClass, Cursor cursor) {
		TableInfo tableInfo = Cache.getTableInfo(modelClass);
		String idName = tableInfo.getIdName();

		try {
			Constructor<?> entityConstructor = modelClass.getConstructor();

			M entity = (M) Cache.getEntity(modelClass, cursor.getLong(cursor.getColumnIndex(idName)));
			if (entity == null) {
				entity = (M) entityConstructor.newInstance();
			}

			entity.loadFromCursor(cursor);
			return entity;
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(
							"Your model " + modelClass.getName() + " does not define a default " +
											"constructor. The default constructor is required for " +
											"now in ActiveAndroid models, as the process to " +
											"populate the ORM model is : " +
											"1. instantiate default model " +
											"2. populate fields"
			);
		}
		catch (Exception e) {
			Timber.e("Failed to process cursor.", e);
		}

		return null;
	}
}
