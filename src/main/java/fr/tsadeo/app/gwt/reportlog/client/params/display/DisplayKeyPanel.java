package fr.tsadeo.app.gwt.reportlog.client.params.display;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyToDisplayBlockBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean.DisplayMode;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

/**
 * Panel des règles d'affichage pour une KeyValue
 * @author sylvie
 *
 */
public final class DisplayKeyPanel extends SimplePanel {
	
	private final static String GROUP_BUTTON = "displayKeyPanel.group";
	
	private final Label _labelKeyName = new Label();
	private final TextBox _tbKeyValue = new TextBox();
	
	private RadioButton _rbAllLine;
	private RadioButton _rbNextJson;
	private RadioButton _rbAttributJson;
	
	private final Button _bAdd = new MyButton("add");
	
	private final AttributToDisplayPanel _detailPanelAttributToDisplay = new AttributToDisplayPanel();
	
	
	void changeKeyValue (final String keyValue) {
		this._tbKeyValue.setText(keyValue);
	}
		
	DisplayKeyPanel(final String keyName) {
		
		this.setStyleName(IConstants.STYLE_PANEL_DISPAY_KEY);
		this.initComposants(keyName);
		this.initHandlers();
		this.add(this.buildMainPanel());
		this.reinit();
	}
	
	void reinit() {
		
		this._rbAllLine.setValue(true);
		this._detailPanelAttributToDisplay.reinit();
		this.showDetailPanelAttributs();
		
	}
	//------------------------------------------------- public methods
	/**
	 * Get datas from beans and apply to widget
	 * @param keyToDisplayBlockBean
	 */
	void applyDatas (final KeyValueBean keyValueBean,
			final KeyToDisplayBlockBean keyToDisplayBlockBean) {
		
		this._labelKeyName.setText(keyToDisplayBlockBean.getKeyToFindName());
		this._tbKeyValue.setText(keyValueBean.getKeyValue());
		
		switch (keyToDisplayBlockBean.getDisplayMode()) {
		  case ALL_LINE: this._rbAllLine.setValue(true);
			break;
		  case ATTRIBUTS_JSON: this._rbAttributJson.setValue(true);
			  break;
		  case NEXT_JSON: this._rbNextJson.setValue(true);
			  break;
		}
		this._detailPanelAttributToDisplay.applyDatas(keyToDisplayBlockBean);
		this.showDetailPanelAttributs();
	}
	void populate (final KeyToDisplayBlockBean keyToDisplayBlockBean) {
		
		DisplayMode displayMode;
		if (this._rbAllLine.getValue()) {
			displayMode = DisplayMode.ALL_LINE;
		}
		else if (this._rbAttributJson.getValue()) {
			displayMode = DisplayMode.ATTRIBUTS_JSON;
		}
		else {
			displayMode = DisplayMode.NEXT_JSON;
		}
		
		keyToDisplayBlockBean.setDisplayMode(displayMode);
		this._detailPanelAttributToDisplay.populate(keyToDisplayBlockBean);
           

	}
	//------------------------------------------------- private methods
	private Panel buildMainPanel() {
		
		final HorizontalPanel main = new HorizontalPanel();
		main.setSpacing(5);
		
		final VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(5);
		vPanel.add(this._rbAllLine);
		vPanel.add(this._rbNextJson);
		
		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.add(this._rbAttributJson);
		panelButton.add(this._bAdd);
		
		vPanel.add(panelButton);
		vPanel.add(this._detailPanelAttributToDisplay);

		//main.add(this._labelKeyName);
		main.add(this._tbKeyValue);
		main.add(vPanel);
		return main;
	}
	
	private void initHandlers () {
		
		// RadioButton
		final ClickHandler rbClickHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showDetailPanelAttributs();
			}
		};
		this._rbAttributJson.addClickHandler(rbClickHandler);
		this._rbAllLine.addClickHandler(rbClickHandler);
		this._rbNextJson.addClickHandler(rbClickHandler);
		
		
		// button Add
		final ClickHandler addClicHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_detailPanelAttributToDisplay.createAttributToDisplay();
			}
		};
		this._bAdd.addClickHandler(addClicHandler);
	}

	private void showDetailPanelAttributs() {
		final boolean rbAttributJson = this._rbAttributJson.getValue();
		this._detailPanelAttributToDisplay.setVisible(rbAttributJson);
		this._bAdd.setVisible(rbAttributJson);
	}
	private void initComposants(final String keyName) {
		
		this._labelKeyName.setText(keyName + ": ");
		
		final String group = GROUP_BUTTON + "." + keyName;
		this._rbAllLine = new RadioButton(group, "all line");
		this._rbNextJson = new RadioButton(group, "next Json block");
		this._rbAttributJson = new RadioButton(group, "attributs Json");
		
		this._labelKeyName.setWidth("50px");
		this._tbKeyValue.addStyleName(IConstants.STYLE_TEXTBOX_TO_DISPLAY);
		this._tbKeyValue.setEnabled(false);
		
		this._bAdd.addStyleName("marginLeft20");
		
		this._rbAllLine.setTitle("display the entire line");
		this._rbNextJson.setTitle("display the next json block following the key");
		this._rbAttributJson.setTitle("display the values of next json attributes");
		
	}

}
