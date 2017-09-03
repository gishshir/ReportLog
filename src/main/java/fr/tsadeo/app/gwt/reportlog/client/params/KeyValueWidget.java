package fr.tsadeo.app.gwt.reportlog.client.params;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

/**
 * Widget représentant une cle / value avec options [regex, remove] et bouton delete
 * @author sylvie
 *
 */
public final class KeyValueWidget extends SimplePanel {
	
	 private final static Logger log = Logger.getLogger("KeyValueWidget");
	
	private static final String PREFIX = "key";
	private static int compteur = 1;
	
	private final KeyValuePanel _container;
	
	private final String _keyName;
	private final CheckBox _cbKeySelect;
	private final CheckBox _cbRegex;
	private final CheckBox _cbRemove;
	private final TextBox _tbKeyValue = new TextBox();
	private final Button _bDelete = new Button();
	private SimplePanel _anchorPanel;
	
	private final boolean _showOption;
	private final boolean _withAnchor;
	
	//-------------------------------------- public methods
	public String getKeyValue() {
		return this._tbKeyValue.getValue();
	}
	public String getKeyName() {
		return this._keyName;
	}
	public boolean isSelected() {
		return this._cbKeySelect.getValue();
	}
	public boolean isRegex() {
		return (this._showOption)?this._cbRegex.getValue():false;
	}
	public boolean isRemove() {
		return (this._showOption)?this._cbRemove.getValue():false;
	}
	
	//-------------------------------------- constructor
	public KeyValueWidget(final KeyValuePanel container) {
		this(null, container, false);
	}
	public KeyValueWidget(final String keyName,
			final KeyValuePanel container, final boolean showOption) {
		this(keyName, container, showOption, false);
	}
	public KeyValueWidget(final String keyName,
			final KeyValuePanel container, final boolean showOption, final boolean withAnchor) {
		
		log.config("new KeyValueWidget() - keyName: " +keyName);
		this._container = container;
		this._showOption = showOption;
		this._withAnchor = withAnchor;
		
		
		if (keyName != null) {
			Integer index = this.extractIndexFromKeyName(keyName);
			if (index != null) {
				compteur = Math.max(compteur, index+1);
			}
		}
		this._keyName = (keyName == null)?PREFIX + KeyValueWidget.compteur++:keyName;
		log.config("final keyName: " + this._keyName);
		
		this._cbKeySelect  = new CheckBox();
		this._cbRegex = (this._showOption)?new CheckBox("regex"):null;
		this._cbRemove = (this._showOption)?new CheckBox("remove"):null;
		
		this.setStyleName(IConstants.STYLE_WIDGET_KEY_VALUE);
		this.add(this.buildMainPanel());
		
		this.initHandler();
		this.initComposants();
		this.reinit();
	}

	
	//-------------------------------------- package methods
	void setFocus(final boolean focus) {
		this._tbKeyValue.setFocus(focus);
	}
	void reinit() {
		this._cbKeySelect.setValue(true);
		if (this._showOption) {
		  this._cbRegex.setValue(false);
		  this._cbRemove.setValue(false);
		}
		this._tbKeyValue.setText(null);
	}
	/**
	 * Get datas from widget and populate Beans
	 */
	public void populate(final KeyValueBean keyValueBean) {
		
		keyValueBean.setSelected(this.isSelected());
		keyValueBean.setKeyValue(this.getKeyValue());
		keyValueBean.setRegex(this.isRegex());
		keyValueBean.setRemove(this.isRemove());
	}
	/**
	 * Get datas from beans and apply to widget
	 * @param keyValueBean
	 */
	public void applyDatas (final KeyValueBean keyValueBean) {
		this._cbKeySelect.setValue(keyValueBean.isSelected());
		if (this._showOption){
		  this._cbRegex.setValue(keyValueBean.isRegex());
		  this._cbRemove.setValue(keyValueBean.isRemove());
		}  
		this._tbKeyValue.setValue(keyValueBean.getKeyValue());
		this.displayWidget();
	}
	//-------------------------------------- private methods
	private Panel buildMainPanel() {
		
		
		final HorizontalPanel panel = new HorizontalPanel();		
		panel.setSpacing(5);
		if (this._withAnchor) {
			this._anchorPanel = new SimplePanel();
			this._anchorPanel.addStyleName(IConstants.STYLE_IMG_ANCHOR);
			panel.add(this._anchorPanel);
			this._anchorPanel.setTitle("drag and drop...");
		}
		panel.add(this._cbKeySelect);
		
		this._tbKeyValue.addStyleName(IConstants.STYLE_TEXTBOX_TO_FIND);
		panel.add(this._tbKeyValue);
		panel.add(this._bDelete);
		
		if (this._showOption){
		  panel.add(this._cbRegex);
		  panel.add(this._cbRemove);
		}
		return panel;
	}
	// extract the number from the keyName
	// key + number
	private Integer extractIndexFromKeyName(String keyName) {
		if (keyName == null) {
			return null;
		}
		if (keyName.startsWith(PREFIX)) {
			String numberStr = keyName.substring(3);
			return new Integer(numberStr);
		}
		return null;
	}
	private void initComposants() {
		if (this._showOption) {
			this._cbRegex.setTitle("find key with regular expression");
			this._cbRemove.setTitle("remove the lines that contains this key");
		}
		this._bDelete.setTitle("remove definitively this key");
		this._bDelete.addStyleName(IConstants.STYLE_IMG_DELETE);
		this._bDelete.setPixelSize(16, 16);
	}
	private void displayWidget() {
		
		
		this._tbKeyValue.removeStyleName(IConstants.STYLE_TEXTBOX_REGEX);
		this._tbKeyValue.removeStyleName(IConstants.STYLE_TEXTBOX_TO_REMOVE);
		this._tbKeyValue.removeStyleName(IConstants.STYLE_TEXTBOX_REGEXANDREMOVE);
		this._tbKeyValue.removeStyleName(IConstants.STYLE_TEXTBOX_UNSELECTED);
		
		// key selected
		if (this._cbKeySelect.getValue()) {
			
			if (this._showOption) {
				// regex & remove
				if (this._cbRegex.getValue() && this._cbRemove.getValue()) {
					this._tbKeyValue
							.addStyleName(IConstants.STYLE_TEXTBOX_REGEXANDREMOVE);
				}
				// regex only
				else if (this._cbRegex.getValue()) {
					this._tbKeyValue
							.addStyleName(IConstants.STYLE_TEXTBOX_REGEX);
				}
				// remove only
				else if (this._cbRemove.getValue()) {
					this._tbKeyValue
							.addStyleName(IConstants.STYLE_TEXTBOX_TO_REMOVE);
				}
			}
		}
		// key not selected
		else  {
			this._tbKeyValue.addStyleName(IConstants.STYLE_TEXTBOX_UNSELECTED);
		}


	}
	private void initHandler() {
				
		// check box select
		this._cbKeySelect.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_container.changeValueOfKey(KeyValueWidget.this);
				displayWidget();
			}
		});
		
		// key value - change name
		this._tbKeyValue.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				if (WidgetUtils.getTextBoxValueLength(_tbKeyValue) > 0) {
					_cbKeySelect.setValue(true);
					_container.changeValueOfKey(KeyValueWidget.this);
				}
			}
		});
		
		// button delete
		this._bDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_container.removeKeyValueWidget(KeyValueWidget.this, true);
			}
		});
		
		// check box option : remove / regex
		if (this._showOption) {
			
		     final ClickHandler optionClickHandlerChangeValue = (new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						_cbKeySelect.setValue(true);
						_container.changeValueOfKey(KeyValueWidget.this);
						displayWidget();
					}
				});
			
			// check box regex
			this._cbRegex.addClickHandler(optionClickHandlerChangeValue);		
			//cbRemove
		   this._cbRemove.addClickHandler(optionClickHandlerChangeValue);
		
		}
	

	}

}
