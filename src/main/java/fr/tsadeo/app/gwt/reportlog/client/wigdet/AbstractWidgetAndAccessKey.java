package fr.tsadeo.app.gwt.reportlog.client.wigdet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;

/**
 * Widget pouvant afficher son accessKey sur l'appui de la touche alt
 * 
 * @author sylvie
 * 
 */
public abstract class AbstractWidgetAndAccessKey extends Composite implements
		IConstants {

	private final FlowPanel main = new FlowPanel();
	private final WidgetWithAccessKey _widget;
	private final AccessKey _accessKey;
	private Element accessKeyElement;

	protected AbstractWidgetAndAccessKey(WidgetWithAccessKey widget,
			AccessKey accessKey) {
		this(widget, accessKey, null);
	}

	protected AbstractWidgetAndAccessKey(WidgetWithAccessKey widget,
			AccessKey accessKey, String width) {
		this(widget, accessKey, width, false);
	}

	protected AbstractWidgetAndAccessKey(WidgetWithAccessKey widget,
			AccessKey accessKey, String width, boolean dialog) {
		this._widget = widget;
		this._accessKey = accessKey;
		this.initWidget(this.buildMainPanel(width, dialog));
	}

	// ------------------------------------- public methods
	public void addClickHandler(ClickHandler clickHandler) {
		this._widget.addClickHandler(clickHandler);
	}

	public void setEnabled(boolean enabled) {
		this._widget.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return this._widget.isEnabled();
	}

	public void showAccessKey(boolean show) {
		if (show) {
			this.accessKeyElement.removeClassName(STYLE_HIDDEN);

		} else {
			this.accessKeyElement.addClassName(STYLE_HIDDEN);
		}
	}

	public AccessKey getAccessKey() {
		return this._accessKey;
	}

	// --------------------------- protected method
	protected WidgetWithAccessKey getWidgetWithAccessKey() {
		return this._widget;
	}

	// --------------------------- private methods

	private Widget buildMainPanel(String width, boolean dialog) {

		if (width != null) {
			this.main.setWidth(width);
		}
		this.main.add((Widget) this._widget);

		this.accessKeyElement = DOM.createSpan();
		this.accessKeyElement.setInnerHTML(this._accessKey.getKey() + "");
		this.accessKeyElement
				.addClassName((dialog) ? STYLE_BUTTON_ACCESS_KEY_DIALOG
						: STYLE_BUTTON_ACCESS_KEY);
		this.accessKeyElement.addClassName(STYLE_HIDDEN);
		this.main.getElement().appendChild(this.accessKeyElement);

		return this.main;
	}

	// ====================== INNER CLASS
	public static class AccessKey {
		private final Character _key;
		private final int _keyboardCode;

		public Character getKey() {
			return this._key;
		}

		public int getKeyboardCode() {
			return this._keyboardCode;
		}

		public AccessKey(Character key, int keyboardCode) {
			this._key = key;
			this._keyboardCode = keyboardCode;
		}

	}

	// ====================== INNER INTERFACE
	protected interface WidgetWithAccessKey extends HasClickHandlers, IsWidget {

		public void setEnabled(boolean enabled);

		public boolean isEnabled();

	}
}
