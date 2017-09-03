package fr.tsadeo.app.gwt.reportlog.client.params.display;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyToDisplayBlockBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsDisplayBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.IItemListener;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;

/**
 * Container principal des paramètres de dispay
 * Liste de DisplayKeyPanel
 * @author sylvie
 *
 */
public final class DisplayContainer extends SimplePanel implements IItemListener {

	 private Panel _pDescription;
	private final VerticalPanel _main = new VerticalPanel();
	private final VerticalPanel _keysPanel = new VerticalPanel();
	
	private Map<String, DisplayKeyPanel> _mapDisplayKeyPanels =
			new TreeMap<String, DisplayKeyPanel>();
	private CheckBox _cbLineNumber = new CheckBox("line numbers");
	private CheckBox _cbDisplayLevelColor = new CheckBox("level color");
	
	public DisplayContainer() {
		this.setStyleName(IConstants.STYLE_PANEL_DISPLAY);
		this.initComposants();
		this.add(this.buildMainPanel());
	}
	
	public void reinit() {
		
		this._cbLineNumber.setValue(true);
		this._cbDisplayLevelColor.setValue(true);
		final Iterator<String> iter = this._mapDisplayKeyPanels.keySet().iterator();
		while(iter.hasNext()) {
			this._mapDisplayKeyPanels.get(iter.next()).reinit();
		}
	}
	//------------------------------------------ implementing IItemListener
	@Override
	public void addItem(String itemName) {
		this._createDisplayKeyPanel(itemName);
	}

	@Override
	public void removeItem(String itemName) {
		this._removeDisplayKeyPanel(itemName);
	}

	@Override
	public void changeItemValue(final String itemName, final String itemValue,
			final boolean selected,
			final boolean regex, final boolean removeKey) {
	   this._changeKeyValue(itemName, itemValue);
	   
	  this.selectItem(itemName, selected, removeKey);
	}
	
	//------------------------------------------------- public methods
	/**
	 * Get datas from beans and apply to widget
	 * @param paramsBean
	 */
	public void applyDatas (final ParamsBean paramsBean) {
		
		final ParamsDisplayBean paramsDisplayBean = paramsBean.getParamsDisplayBean();
		
		this._cbLineNumber.setValue(paramsDisplayBean.isLineNumbers());
		this._cbDisplayLevelColor.setValue(paramsDisplayBean.isLevelColor());
		
		this._mapDisplayKeyPanels.clear();
		this._keysPanel.clear();
		
		final List<KeyValueBean> listKeyToFind = paramsBean.getParamsFilterBean().getListKeyValues();
		
		// for each keyToFind
        for (int i = 0; i < listKeyToFind.size(); i++) {

			final KeyValueBean keyToFindBean = listKeyToFind.get(i);
			final DisplayKeyPanel displayKeyPanel = this._createDisplayKeyPanel(keyToFindBean.getKeyName());
			final KeyToDisplayBlockBean keyToDisplayBlockBean = paramsDisplayBean.getKeyToDisplayBlockBean(keyToFindBean.getKeyName());
			if (keyToDisplayBlockBean != null) {
			  displayKeyPanel.applyDatas(keyToFindBean, keyToDisplayBlockBean);
			}
			this.selectItem(keyToFindBean.getKeyName(), keyToFindBean.isSelected(), keyToFindBean.isRemove());
        }
		
	}
	public void populate (final ParamsBean paramsBean) {

		final List<KeyValueBean> listKeyToFind = paramsBean.getParamsFilterBean().getListKeyValues();
		final ParamsDisplayBean paramsDisplayBean = paramsBean.getParamsDisplayBean();
        paramsDisplayBean.clean();
        
        paramsDisplayBean.setLineNumbers(this._cbLineNumber.getValue());
        paramsDisplayBean.setLevelColor(this._cbDisplayLevelColor.getValue());
        
        // for each keyToFind
        for (int i = 0; i < listKeyToFind.size(); i++) {

			KeyValueBean keyToFindBean = listKeyToFind.get(i);
			
        	if (!keyToFindBean.isSelected()) continue;

			final DisplayKeyPanel displayKeyPanel = this._mapDisplayKeyPanels.get(keyToFindBean.getKeyName());
			if (displayKeyPanel == null) continue;
			
			final KeyToDisplayBlockBean keyToDisplayBlock = 
				 paramsDisplayBean.createKeyToDisplayBlock(keyToFindBean.getKeyName());
			displayKeyPanel.populate(keyToDisplayBlock);
		}

	}
	
	//------------------------------------------------- private methods

	private Panel buildMainPanel() {
		this._main.setWidth("80%");
		this._main.setSpacing(5);
		this._main.add(this._pDescription);
		
		final HorizontalPanel panelCheckboxes = new HorizontalPanel();
		panelCheckboxes.setSpacing(IConstants.SPACING_MIN);
		panelCheckboxes.add(this._cbLineNumber);
		panelCheckboxes.add(this._cbDisplayLevelColor);
		
		this._main.add(panelCheckboxes);
		
		Panel panelList = new SimplePanel();
		panelList.addStyleName(IConstants.STYLE_PANEL_DISPAY_KEY_LIST);
		panelList.add(this._keysPanel);
		this._main.add(panelList);
		return this._main;
	}
	
	private void initComposants() {
		
	      // description
      final String description ="The display params allow to choose how must be displayed each line.";
		
		final HTML labelDescription = new HTML(description);

		
		this._pDescription = WidgetUtils.buildSimplePlanel(labelDescription, null);	
		this._pDescription.setStyleName("panelDescription");
		
		this._cbLineNumber.setTitle("display the line number");
		this._cbDisplayLevelColor.setTitle("display the log levels with different colors");
	}
	
	private void selectItem(final String itemName, final boolean selected, final boolean removeKey) {
		if (!this._mapDisplayKeyPanels.containsKey(itemName)) return;
		final DisplayKeyPanel item = this._mapDisplayKeyPanels.get(itemName);
		item.setVisible(selected && !removeKey);
	}
	private void _changeKeyValue(final String keyName, final String keyValue) {
		if (!this._mapDisplayKeyPanels.containsKey(keyName)) return;
		final DisplayKeyPanel item = this._mapDisplayKeyPanels.get(keyName);
		item.changeKeyValue(keyValue);
	}
	
	private DisplayKeyPanel _createDisplayKeyPanel (final String keyName) {
		final DisplayKeyPanel item = new DisplayKeyPanel(keyName);
	    this._mapDisplayKeyPanels.put(keyName, item);
	    this._keysPanel.add(item);
	    return item;
	}
	private void _removeDisplayKeyPanel(final String keyName) {
		if (!this._mapDisplayKeyPanels.containsKey(keyName)) return;
		final DisplayKeyPanel item = this._mapDisplayKeyPanels.get(keyName);
		this._keysPanel.remove(item);
		this._mapDisplayKeyPanels.remove(keyName);
	}

	
}
