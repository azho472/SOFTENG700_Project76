package nz.ac.auckland.nihi.trainer.views;

import android.content.Context;
import android.util.AttributeSet;

public class SmallExerciseSummaryStatsView extends AbstractExerciseSummaryStatsView {

	public SmallExerciseSummaryStatsView(Context context) {
		super(context);
	}

	public SmallExerciseSummaryStatsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SmallExerciseSummaryStatsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected int getLayoutId() {
		return nz.ac.auckland.nihi.trainer.R.layout.exercise_summary_stats_collapsed_view;
	}

}
