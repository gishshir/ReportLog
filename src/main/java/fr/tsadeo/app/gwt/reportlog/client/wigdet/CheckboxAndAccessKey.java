package fr.tsadeo.app.gwt.reportlog.client.wigdet;

import com.google.gwt.user.client.ui.CheckBox;

import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;

public class CheckboxAndAccessKey extends AbstractWidgetAndAccessKey implements
		IConstants {

	public CheckboxAndAccessKey(String text, AccessKey accessKey, String width) {
		super(new MyCheckbox(text), accessKey, width);
	}

	// ------------------------------ public methods
	public void setValue(boolean value) {

		((MyCheckbox) super.getWidgetWithAccessKey()).setValue(value);
	}

	public boolean getValue() {
		return ((MyCheckbox) super.getWidgetWithAccessKey()).getValue();
	}

	// ============== INNER CLASS ===============
	private static class MyCheckbox extends CheckBox implements
			WidgetWithAccessKey {

		private MyCheckbox(String text) {
			super(text);
		}

	}

}
