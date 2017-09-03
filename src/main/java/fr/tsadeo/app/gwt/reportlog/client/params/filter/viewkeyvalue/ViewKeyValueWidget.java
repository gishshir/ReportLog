package fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.ViewKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

/**
 * Element de view dans la ListViewKeyValuePanel
 * composé d'une image green/yellow et d'un label
 * @author sylvie
 *
 */
public class ViewKeyValueWidget extends Composite {
	
	 private final static Logger log = Logger.getLogger("ViewKeyValueWidget");

	private final int _viewId;
	
	private HorizontalPanel _main = new HorizontalPanel();
	
	private final CheckBox _cbActive = new CheckBox("saisir un nom...");
	private final Button _btDisplay  = new Button();
	
	
	public ViewKeyValueWidget(final int viewId, final IViewListener viewListener) {
		
		 
		this._viewId = viewId;
		this.initComposants();
		this.initHandler(viewListener);
		this.initWidget(this.buildMainPanel());
	}
	
    void setEnabled(boolean enabled) {
    	this._cbActive.setEnabled(enabled);
    	this._btDisplay.setEnabled(enabled);
    }
	

	void changeName(final String viewName) {
    	this._cbActive.setText(viewName);
    }
	/**
	 * Get datas from beans and apply to widget
	 * @param keyValueBean
	 */
	void applyDatas (final ViewKeyValueBean viewKeyValueBean) {

		log.config("applyDatas() - viewName: " + viewKeyValueBean.getName());
        this._cbActive.setValue(viewKeyValueBean.isActif());
        this._cbActive.setText(viewKeyValueBean.getName());
	}
	/**
	 * Get datas from widget and apply to bean
	 * @param viewKeyValueBean
	 */
	void populate (final ViewKeyValueBean viewKeyValueBean) {
		viewKeyValueBean.setActif(this._cbActive.getValue());
		viewKeyValueBean.setName(this._cbActive.getText());
	}

	//---------------------------------------------- private methods
    private void initComposants() {
		this._cbActive.setValue(true);
		this._cbActive.setTitle("active or desactive the keys of the view");
		this._btDisplay.setTitle("show the details of the view");
		this._btDisplay.addStyleName(IConstants.STYLE_IMG_DETAIL);
		this._btDisplay.setPixelSize(16, 16);
	}

	private Panel buildMainPanel() {
		
		
		this._main.setSpacing(IConstants.SPACING_MIN);
		this._main.setWidth("250px");
		
		this._main.add(WidgetUtils.buildSimplePlanel(this._cbActive, "100px"));
		this._main.add(this._btDisplay);
		
		return this._main;
	}
	void displayBorder(final boolean selected) {
		
		this._main.removeStyleName(IConstants.STYLE_VIEW_SELECTED_ACTIF);
		this._main.removeStyleName(IConstants.STYLE_VIEW_SELECTED_UNACTIF);

		if (selected) {
			this._main.addStyleName(this._cbActive.getValue()?IConstants.STYLE_VIEW_SELECTED_ACTIF:
				                                             IConstants.STYLE_VIEW_SELECTED_UNACTIF);
		} 
	}
	private void initHandler(final IViewListener viewListener) {
		
		// button display
		this._btDisplay.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				viewListener.selectView(_viewId,true);
			}
		});
		
		// checkbox active
		this._cbActive.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				viewListener.changeViewValues(_viewId, null, _cbActive.getValue());
			}
		});
		
	}
}
