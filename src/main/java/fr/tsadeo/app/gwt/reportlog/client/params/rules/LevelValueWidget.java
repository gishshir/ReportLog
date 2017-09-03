package fr.tsadeo.app.gwt.reportlog.client.params.rules;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

import fr.tsadeo.app.gwt.reportlog.client.service.Level;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;

public class LevelValueWidget extends Composite{
	
	private final Level _level;
	private final TextBox _tbLevelValue = new TextBox();

	//-------------------------------------- accessors
	public Level getLevel() {
		return this._level;
	}
	public String getValue() {
		return this._tbLevelValue.getText();
	}
	public void refreshValue() {
		this._tbLevelValue.setText(this._level.getValue());
	}
	//------------------------------------- constructors
	public LevelValueWidget() {
		this(Level.DEBUG);
	}
	public LevelValueWidget(final Level level) {
		this._level = level;
		this._tbLevelValue.addStyleName(IConstants.STYLE_TEXTBOX_TO_DISPLAY);
		this._tbLevelValue.setText(level.getValue());
		this.initHandler();
		this.initWidget(this.buildMainPanel());
	}
	
	//------------------------------------ private methods
	private Panel buildMainPanel() {
		final HorizontalPanel main = new HorizontalPanel();
		final Label label = new Label(this._level.toString(), true);
		main.add(label);
		main.setCellWidth(label, "70px;");
		main.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
		main.add(this._tbLevelValue);
		return main;
	}
	private void initHandler() {
		this._tbLevelValue.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				final String value = _tbLevelValue.getText().trim();
				if (value.length() > 0) {
					_level.setValue(value);
				}
			}
		});
	}

}
