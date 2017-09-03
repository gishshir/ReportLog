package fr.tsadeo.app.gwt.reportlog.client.wigdet;

import com.google.gwt.user.client.ui.Button;

import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.AbstractWidgetAndAccessKey.WidgetWithAccessKey;

public class MyButton extends Button implements WidgetWithAccessKey, IConstants {

	public MyButton() {
		super();
		this.addStyleName(STYLE_BUTTON);
	}

	public MyButton(final String text) {
		super(text);
		this.addStyleName(STYLE_BUTTON);
	}

	public MyButton(String text, String title, String width) {
		this(text);
		this.setWidth(width);
		this.setTitle(title);
	}
}
