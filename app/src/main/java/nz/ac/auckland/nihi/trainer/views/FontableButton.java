package nz.ac.auckland.nihi.trainer.views;

import nz.ac.auckland.nihi.trainer.R.style;
import nz.ac.auckland.nihi.trainer.R.styleable;
import nz.ac.auckland.nihi.trainer.util.UiUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class FontableButton extends Button {
	public FontableButton(Context context) {
		// super(context);
		this(context, null);
	}

	public FontableButton(Context context, AttributeSet attrs) {
		// super(context, attrs);
		// UiUtil.setCustomFont(this, context, attrs, styleable.nz_ac_auckland_nihi_trainer_views_FontableButton,
		// styleable.nz_ac_auckland_nihi_trainer_views_FontableButton_font);
		this(context, attrs, android.R.attr.buttonStyle);
	}

	public FontableButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		UiUtils.setCustomFont(this, context, attrs, styleable.nz_ac_auckland_nihi_trainer_views_FontableButton,
				defStyle, style.PrimaryButtonStyle, styleable.nz_ac_auckland_nihi_trainer_views_FontableButton_font);
	}
}