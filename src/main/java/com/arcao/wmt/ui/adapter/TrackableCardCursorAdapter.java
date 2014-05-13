package com.arcao.wmt.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.arcao.wmt.data.database.util.ModelUtils;
import com.arcao.wmt.ui.widget.card.TrackableCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridCursorAdapter;

/**
 * Created by msloup on 11.5.2014.
 */
public class TrackableCardCursorAdapter<M extends AbstractTrackableModel> extends CardGridCursorAdapter {
	private final Class<M> modelClass;

	public TrackableCardCursorAdapter(Context context, Class<M> modelClass) {
		super(context);
		this.modelClass = modelClass;
	}

	@Override
	protected Card getCardFromCursor(final Cursor cursor) {
		final M model = ModelUtils.getModelFromCursor(modelClass, cursor);

		Card card = new TrackableCard(mContext, model);
		card.setOnClickListener(new Card.OnCardClickListener() {
			@Override
			public void onClick(Card card, View view) {
				getCardGridView().performItemClick(view, cursor.getPosition(), model.getId());
			}
		});

		return card;
	}
}
