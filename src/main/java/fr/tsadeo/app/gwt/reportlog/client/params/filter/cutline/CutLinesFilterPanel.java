package fr.tsadeo.app.gwt.reportlog.client.params.filter.cutline;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;

public class CutLinesFilterPanel extends Composite {

	private final HorizontalPanel _detailPanelCutLines = new HorizontalPanel();
	private final CheckBox _cbCutLines = new CheckBox("cut lines");

	private final TextBox _tbFromLineNumber = new TextBox();
	private final TextBox _tbToLineNumber = new TextBox();
	private final Label _wrongValue = new Label("wrong value");

	// ---------------------------------------- constructor
	public CutLinesFilterPanel() {
		this.initComposants();
		this.initHandler();
		this.initWidget(this.buildMainPanel());
	}

	// ----------------------------------------- public methods
	public void reinit() {

		this._cbCutLines.setValue(false);
		this._tbFromLineNumber.setText(null);
		this._tbToLineNumber.setText(null);

		this.showDetailPanel();
	}

	/**
	 * Get datas from beans and apply to widget
	 */
	public void applyDatas(final ParamsFilterBean paramFilter) {

		this._cbCutLines.setValue(paramFilter.isCutLines());
		if (paramFilter.getFromLineNumber() > 0) {
			this._tbFromLineNumber
					.setText("" + paramFilter.getFromLineNumber());
		}
		if (paramFilter.getToLineNumber() > 0) {
			this._tbToLineNumber.setText("" + paramFilter.getToLineNumber());
		}
		this.showDetailPanel();
	}

	/**
	 * Get datas from widget and populate Beans
	 */
	public void populate(final ParamsFilterBean paramFilter) {

		// active param
		paramFilter.setCutLines(this._cbCutLines.getValue());

		final String fromLineNumber = this._tbFromLineNumber.getText();
		final String toLineNumber = this._tbToLineNumber.getText();

		paramFilter.setFromLineNumber(WidgetUtils
				.controlNumeric(fromLineNumber) ? Integer
				.parseInt(fromLineNumber) : -1);
		paramFilter
				.setToLineNumber(WidgetUtils.controlNumeric(toLineNumber) ? Integer
						.parseInt(toLineNumber) : -1);

	}

	// -------------------------------------- privates methods

	private Panel buildMainPanel() {

		final HorizontalPanel main = new HorizontalPanel();
		main.setSpacing(IConstants.SPACING_MIN);
		main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		main.add(WidgetUtils.buildSimplePlanel(this._cbCutLines, "100px"));

		// panel detail
		this._detailPanelCutLines
				.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this._detailPanelCutLines.setSpacing(IConstants.SPACING_MIN);
		this._detailPanelCutLines.add(new Label("from:"));
		this._detailPanelCutLines.add(this._tbFromLineNumber);
		this._detailPanelCutLines.add(new Label("to:"));
		this._detailPanelCutLines.add(this._tbToLineNumber);
		this._detailPanelCutLines.add(this._wrongValue);
		main.add(this._detailPanelCutLines);

		return main;
	}

	private void showDetailPanel() {

		this._detailPanelCutLines.setVisible(this._cbCutLines.getValue());
	}

	private void initComposants() {

		this._tbFromLineNumber
				.addStyleName(IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK);
		this._tbToLineNumber
				.addStyleName(IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK);

		this._wrongValue.addStyleName(IConstants.STYLE_WRONG_VALUE);
		this._wrongValue.setVisible(false);

		this._cbCutLines.setTitle("remove line from x to y");
	}

	private void initHandler() {

		// control de saisie
		final ChangeHandler tbChangeHandler = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				final TextBox textBox = (TextBox) event.getSource();
				final boolean controlOk = WidgetUtils.controlNumeric(textBox
						.getText());
				_wrongValue.setVisible(!controlOk);
				textBox.removeStyleName((controlOk) ? IConstants.STYLE_TEXTBOX_NUMBER_LINE_IN_ERROR
						: IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK);
				textBox.addStyleName((controlOk) ? IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK
						: IConstants.STYLE_TEXTBOX_NUMBER_LINE_IN_ERROR);
			}
		};

		this._tbFromLineNumber.addChangeHandler(tbChangeHandler);
		this._tbToLineNumber.addChangeHandler(tbChangeHandler);

		// Log level
		this._cbCutLines.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showDetailPanel();
			}
		});

	}

}
