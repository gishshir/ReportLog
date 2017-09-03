package fr.tsadeo.app.gwt.reportlog.client.params.display;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.AttributToDisplayBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyToDisplayBlockBean;

/**
 * Panel de display pour une KeyValue, contenant la liste des attributs Json
 * @author sylvie
 *
 */
public final  class AttributToDisplayPanel extends SimplePanel {

	private final VerticalPanel _main = new VerticalPanel();
	private final Map<String, AttributToDisplayWidget> _mapAttributToDisplay = new TreeMap<String, AttributToDisplayWidget>();

	//------------------------------------- constructor
	public AttributToDisplayPanel() {
		this.addStyleName("attributToDisplayPanel");
		this.add(this._main);
		this.reinit();
	}
	
	//-------------------------------------------
	public AttributToDisplayWidget createAttributToDisplay() {
		final AttributToDisplayWidget item = new AttributToDisplayWidget(this);
		this._mapAttributToDisplay.put(item.getAttributName(), item);
		this._main.add(item);
		return item;
	}
	//------------------------------------------------- public methods
	/**
	 * Get datas from beans and apply to widget
	 * @param keyToDisplayBlockBean
	 */
	public void applyDatas (final KeyToDisplayBlockBean keyToDisplayBlockBean) {
		
		this._mapAttributToDisplay.clear();
		this._main.clear();
		
		final List<AttributToDisplayBean> attributToDisplayBeans = keyToDisplayBlockBean.getListAttributToDisplay();
		for (AttributToDisplayBean attributToDisplayBean : attributToDisplayBeans) {
			
			final AttributToDisplayWidget attribToDisplayWidget = this.createAttributToDisplay();
			attribToDisplayWidget.applyDatas(attributToDisplayBean);
		}
	}
	public void populate (final KeyToDisplayBlockBean keyToDisplayBlockBean) {
		keyToDisplayBlockBean.getListAttributToDisplay().clear();
		
		final Iterator<String> iter = this._mapAttributToDisplay.keySet().iterator();
		while (iter.hasNext()) {
			final AttributToDisplayWidget attributToDisplayWidget = this._mapAttributToDisplay.get(iter.next());
			if (attributToDisplayWidget != null && attributToDisplayWidget.isSelected()) {
				final String attributValue = attributToDisplayWidget.getAttributValue().trim();
				if (attributValue != null && attributValue.length() > 0) {
					keyToDisplayBlockBean.createAttributToDisplayBean(
							attributToDisplayWidget.getAttributName()).setAttributValue(attributToDisplayWidget.getAttributValue());
				}
			}
		}
	}
	//------------------------------------- package methods
	void reinit() {
		final Iterator<String> iter = this._mapAttributToDisplay.keySet().iterator();
		while(iter.hasNext()) {
			final AttributToDisplayWidget item = this._mapAttributToDisplay.get(iter.next());
			item.reinit();
		}
	}
	void removeItem(final AttributToDisplayWidget item) {
		this._removeAttributToDisplay(item.getAttributName());
	}
	void changeValue(final AttributToDisplayWidget item) {
		// TODO
	}
	void selectItem(final AttributToDisplayWidget item) {
		// TODO
	}
	
	//----------------------------------------- private methods
	void _removeAttributToDisplay(final String attributName) {
		if (!this._mapAttributToDisplay.containsKey(attributName)) return;
		final AttributToDisplayWidget item = this._mapAttributToDisplay.get(attributName);
		this._main.remove(item);
		this._mapAttributToDisplay.remove(attributName);
	}
}
