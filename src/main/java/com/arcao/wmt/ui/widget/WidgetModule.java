package com.arcao.wmt.ui.widget;

import com.arcao.wmt.ui.widget.card.CardModule;
import dagger.Module;

/**
 * Created by msloup on 11.5.2014.
 */
@Module(
				includes = CardModule.class,
				complete = false,
				library = true
)
public final class WidgetModule {
}
