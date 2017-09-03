package fr.tsadeo.app.gwt.reportlog.client.params.display;

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

import fr.tsadeo.app.gwt.reportlog.client.service.bean.AttributToDisplayBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

public final class AttributToDisplayWidget extends SimplePanel {
	
	private static int compteur = 1;

	private final AttributToDisplayPanel _container;
	
	private final String _attributName;
	private final CheckBox _cbAttribut= new CheckBox("");
	private final TextBox _tbAttributValue = new TextBox();
	private final Button _bDelete = new MyButton();
	
	//-------------------------------------- public methods
	public String getAttributValue() {
		return this._tbAttributValue.getValue();
	}
	public String getAttributName() {
			return this._attributName;
	}
	void reinit() {
		this._cbAttribut.setValue(true);
		this._tbAttributValue.setValue(null);
	}
	public boolean isSelected() {
		return this._cbAttribut.getValue();
	}
	//---------------------------------------------- constructor
	public AttributToDisplayWidget(final AttributToDisplayPanel container) {

        this._container = container;
        this._attributName = "key" + AttributToDisplayWidget.compteur++;
          
   		this.setStyleName(IConstants.STYLE_ATTRIBUT_TO_DISPLAY);
   		
   		this.initComposants();
		this.add(this.buildMainPanel());
		
		this.initHandler();
		this.reinit();
	}
	
	private Panel buildMainPanel() {
		final HorizontalPanel panel = new HorizontalPanel();		
		panel.setSpacing(5);
		panel.add(this._cbAttribut);
		
		this._tbAttributValue.addStyleName(IConstants.STYLE_TEXTBOX_TO_DISPLAY);
		panel.add(this._tbAttributValue);
		panel.add(this._bDelete);
		return panel;
		
	}
	private void initComposants()  {
		this._bDelete.setTitle("remove this attribut");
		this._bDelete.addStyleName(IConstants.STYLE_IMG_DELETE);
		this._bDelete.setPixelSize(16, 16);
	}
	
	/**
	 * Get datas from beans and apply to widget
	 * @param attributToDisplayBean
	 */
	public void applyDatas (final AttributToDisplayBean attributToDisplayBean) {
		
		this._tbAttributValue.setText(attributToDisplayBean.getAttributValue());
		this._cbAttribut.setValue(attributToDisplayBean.isSelected());
	}
	private void initHandler() {
		
		// check box
		this._cbAttribut.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_container.selectItem(AttributToDisplayWidget.this);
			}
		});
		
		// key value
		this._tbAttributValue.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				_container.changeValue(AttributToDisplayWidget.this);
				
				if (WidgetUtils.getTextBoxValueLength(_tbAttributValue) > 0) {
					_cbAttribut.setValue(true);
					_container.selectItem(AttributToDisplayWidget.this);
				}
			}
		});
		
		// button delete
		this._bDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_container.removeItem(AttributToDisplayWidget.this);
			}
		});
	}
}
