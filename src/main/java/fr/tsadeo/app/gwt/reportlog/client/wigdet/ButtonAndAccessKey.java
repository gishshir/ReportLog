package fr.tsadeo.app.gwt.reportlog.client.wigdet;

import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;

/**
 * Button and accessKey
 * 
 * @author sylvie
 * 
 */
public class ButtonAndAccessKey extends AbstractWidgetAndAccessKey implements
		IConstants {

	public ButtonAndAccessKey(String text, AccessKey accessKey, String title,
			String width, boolean dialog) {
		super(new MyButton(text, title, width), accessKey, width, dialog);
	}

	public ButtonAndAccessKey(String text, AccessKey accessKey, String title,
			String width) {

		super(new MyButton(text, title, width), accessKey, width);

	}

	public void setFocus(boolean focused) {
		((MyButton) super.getWidgetWithAccessKey()).setFocus(focused);
	}

	public void click() {
		((MyButton) super.getWidgetWithAccessKey()).click();
	}

}
