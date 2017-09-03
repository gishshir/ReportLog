package fr.tsadeo.app.gwt.reportlog.client.view;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean.TrimLineBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.AbstractWidgetAndAccessKey.AccessKey;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.CheckboxAndAccessKey;

/**
 * Panel d'action pour couper les logs et garder from >> to
 * 
 * @author sylvie
 * 
 */
public class ActionTrimLinePanel extends HorizontalPanel implements IConstants {

	private final LogsView _logsView;

	private final CheckboxAndAccessKey _cbTrimLines = new CheckboxAndAccessKey(
			"Trim lines", new AccessKey('T', SHORTCUT_TRIM_ACTION), "90px");

	private final ChangeHandler _lineChangeHandler = new LineChangeClickHandler();

	private final Label _labelFrom = new Label("from:");
	private final Label _labelTo = new Label("to:");
	private final TextBox _tbFromLineNumber = new LineTextBox();
	private final TextBox _tbToLineNumber = new LineTextBox();
	private final Label _wrongValue = new Label("wrong value");

	private boolean _enabled = false;

	// ------------------------ constructor
	public ActionTrimLinePanel(LogsView logsView) {
		this._logsView = logsView;
		this.buildMainPanel();
		this.initHandlers();
	}

	// --------------------------------------- public methods
	public void showAccessKey(boolean show) {
		this._cbTrimLines.showAccessKey(show);
	}

	public void doClickOnCbTrim() {
		this._cbTrimLines.setValue(!this._cbTrimLines.getValue());
		this.doTrimAction();
	}

	public void setValues(boolean trimLines, int fromValue, int toValue) {

		this._cbTrimLines.setValue(trimLines);
		this._tbFromLineNumber.setValue((fromValue < 0) ? "" : "" + fromValue);
		this._tbToLineNumber.setValue((toValue < 0) ? "" : "" + toValue);
		this.setItemsVisible(true);
		this.manageActivationTexBox();
	}

	public void setEnabled(boolean enabled) {
		this._enabled = enabled;
		this._cbTrimLines.setEnabled(enabled);
		this.enableTextBox(enabled);
		this.manageActivationTexBox();

	}

	public boolean isEnabled() {
		return this._enabled;
	}

	// -------------------------------------- private methods
	private void doTrimAction() {
		manageActivationTexBox();
		launchProcess();
	}

	private void buildMainPanel() {

		this.addStyleName(MARGIN_BOTTOM_3PX);
		this.setSpacing(IConstants.SPACING_MIN);
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this._cbTrimLines.addStyleName(MARGIN_BOTTOM_3PX);
		this.add(this._cbTrimLines);

		this.add(this._labelFrom);
		this.add(this._tbFromLineNumber);
		this.add(this._labelTo);
		this.add(this._tbToLineNumber);
		this._wrongValue.setVisible(false);
		this.add(this._wrongValue);

		this.setItemsVisible(false);

	}

	private void setItemsVisible(boolean visible) {
		this._cbTrimLines.setVisible(visible);
		this._tbFromLineNumber.setVisible(visible);
		this._tbToLineNumber.setVisible(visible);

		this._labelFrom.setVisible(visible);
		this._labelTo.setVisible(visible);
	}

	private TrimLineBean buildTrimLineBean() {

		final String fromLineNumber = this._tbFromLineNumber.getText();
		final String toLineNumber = this._tbToLineNumber.getText();

		int from = WidgetUtils.controlNumeric(fromLineNumber) ? Integer
				.parseInt(fromLineNumber) : -1;
		int to = WidgetUtils.controlNumeric(toLineNumber) ? Integer
				.parseInt(toLineNumber) : -1;

		return new TrimLineBean(_cbTrimLines.getValue(), from, to);
	}

	private void launchProcess() {

		TrimLineBean trimLineBean = this.buildTrimLineBean();
		if (!trimLineBean.isTrimLineOn()
				|| (trimLineBean.isTrimLineOn()
						&& trimLineBean.getFromLine() >= 0 && trimLineBean
						.getToLine() > 0)) {
			_logsView.launchProcess(null, trimLineBean);
		}

	}

	private void initHandlers() {

		this._cbTrimLines.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doTrimAction();
			}
		});
	}

	private void enableTextBox(boolean enabled) {
		this._tbFromLineNumber.setEnabled(enabled);
		this._tbToLineNumber.setEnabled(enabled);
	}

	private void manageActivationTexBox() {
		boolean trimOn = this._cbTrimLines.getValue();

		this.removeAllStyles(this._tbFromLineNumber);
		this.removeAllStyles(this._tbToLineNumber);
		if (!trimOn) {

			this._tbFromLineNumber
					.addStyleName(STYLE_TEXTBOX_NUMBER_LINE_DISABLED);
			this._tbToLineNumber
					.addStyleName(STYLE_TEXTBOX_NUMBER_LINE_DISABLED);
		} else {
			this.defineStyle(this._tbFromLineNumber);
			this.defineStyle(this._tbToLineNumber);
		}
	}

	private void removeAllStyles(TextBox textBox) {
		textBox.removeStyleName(STYLE_TEXTBOX_NUMBER_LINE_DISABLED);
		textBox.removeStyleName(STYLE_TEXTBOX_NUMBER_LINE_IN_ERROR);
		textBox.removeStyleName(STYLE_TEXTBOX_NUMBER_LINE_OK);
	}

	private void defineStyle(TextBox textBox) {
		final boolean controlOk = WidgetUtils.controlNumeric(textBox.getText());
		_wrongValue.setVisible(!controlOk);
		textBox.addStyleName((controlOk) ? IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK
				: STYLE_TEXTBOX_NUMBER_LINE_IN_ERROR);
	}

	// ======================= INNER CLASS =========
	private class LineTextBox extends TextBox {

		public LineTextBox() {
			this.setStyleName(STYLE_TEXTBOX_NUMBER_LINE_OK);
			this.addChangeHandler(_lineChangeHandler);
		}
	}

	private class LineChangeClickHandler implements ChangeHandler {

		@Override
		public void onChange(ChangeEvent event) {
			TextBox textBox = (TextBox) event.getSource();
			removeAllStyles(textBox);
			defineStyle(textBox);

			if (_cbTrimLines.getValue()) {
				launchProcess();
			}
		}
	}

}
