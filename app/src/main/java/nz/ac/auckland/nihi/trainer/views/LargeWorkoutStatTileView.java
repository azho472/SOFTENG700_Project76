package nz.ac.auckland.nihi.trainer.views;

import android.content.Context;
import android.util.AttributeSet;

public class LargeWorkoutStatTileView extends AbstractWorkoutStatTileView {

	public LargeWorkoutStatTileView(Context context) {
		super(context);
	}

	public LargeWorkoutStatTileView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LargeWorkoutStatTileView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected int getLayoutId() {
		return nz.ac.auckland.nihi.trainer.R.layout.workout_screen_large_stat_tile;
	}

}
