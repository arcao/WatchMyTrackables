package com.arcao.wmt.ui.widget.card;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.arcao.wmt.App;
import com.arcao.wmt.R;
import com.arcao.wmt.data.database.model.AbstractTrackableModel;
import com.squareup.picasso.Picasso;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

import javax.inject.Inject;

public class TrackableCard extends Card {
	private final AbstractTrackableModel model;

	@Inject
	Picasso picasso;
	@InjectView(R.id.card_title)
	TextView cardTitle;
	@InjectView(R.id.card_subTitle)
	TextView cardSubTitle;
	@InjectView(R.id.card_goal)
	TextView cardGoal;

	public TrackableCard(Context context, AbstractTrackableModel model) {
		super(context, R.layout.card_trackable_content);
		this.model = model;
		init();
	}

	private void init() {
		App.get(mContext).inject(this);

		addCardThumbnail(new TrackableThumbnail(mContext, picasso, model));
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		ButterKnife.inject(this, view);

		cardTitle.setText(model.trackingNumber);
		cardSubTitle.setText(model.name);
		cardGoal.setText(Html.fromHtml(model.goal).toString());
	}

	private static class TrackableThumbnail extends CardThumbnail {
		private final AbstractTrackableModel model;
		private final Picasso picasso;


		public TrackableThumbnail(Context context, Picasso picasso, AbstractTrackableModel model) {
			super(context);
			setExternalUsage(true);

			this.picasso = picasso;
			this.model = model;
		}

		@Override
		public void setupInnerViewElements(ViewGroup parent, View view) {
			ImageView imageView = (ImageView) view;
			//imageView.getLayoutParams().width = 64;
			//imageView.getLayoutParams().height = 64;
			imageView.setScaleType(ImageView.ScaleType.CENTER);

			if (model.images.size() > 0) {
				picasso.load(model.images.get(0).getThumbUrl())
								.resize(imageView.getLayoutParams().width, imageView.getLayoutParams().height)
								.centerCrop().into(imageView);
			} else {
				picasso.load(model.trackableTypeImage).resize(dpToPx(32), dpToPx(32)).into(imageView);
			}
		}

		private int dpToPx(int dp) {
			float density = mContext.getResources().getDisplayMetrics().density;
			return Math.round((float)dp * density);
		}
	}
}
