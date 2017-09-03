package fr.tsadeo.app.gwt.reportlog.client.params;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.IListKeyValuesBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ListKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.IItemListener;

/**
 * Container de KeyValueWidget
 * @author sylvie
 *
 */
public class KeyValuePanel extends SimplePanel {
	
	 private final static Logger log = Logger.getLogger("KeyValuePanel");
	
	private final VerticalPanel _main = new VerticalPanel();
	private final boolean _showOption;
	private final boolean _withAnchor;
	
	private  IItemListener _itemListener;
	private final List<KeyValueWidget> _listKeyValue = new ArrayList<KeyValueWidget>();
	
	//-------------------------------------- constructor
	public KeyValuePanel (final boolean showOption) {
		this(showOption, false);
	}
	public KeyValuePanel (final boolean showOption, final boolean withAnchor) {
		this._showOption = showOption;
		this._withAnchor = withAnchor;
		this.setWidget(this._main);
		this.addStyleName(IConstants.STYLE_PANEL_KEY_VALUE);
	}

	//----------------------------------------- public method
	
	public void setItemListener (final IItemListener itemListener) {
		this._itemListener = itemListener;
	}
	public void addKeyValue() {
		final KeyValueWidget item = this.createKeyValue(null, true);
		item.setFocus(true);
	}
	
	public void reinit() {
		
		this._main.clear();
		for (int i = 0; i < this._listKeyValue.size(); i++) {
			this._listKeyValue.get(i).reinit();
		}
	}
	/**
	 * Get datas from beans and apply to widget
	 * @param paramsLineRuleBean
	 */
	public void applyDatas (final ListKeyValueBean listKeyValueBean, boolean clearWidget) {
		
		if (clearWidget) {
		  this._listKeyValue.clear();
		  this._main.clear();
		}
		
		for (KeyValueBean keyValueBean : listKeyValueBean) {
			log.config("create widget");
			final KeyValueWidget keyValueWidget = this.createKeyValue(keyValueBean.getKeyName(), true);
			keyValueWidget.applyDatas(keyValueBean);
		}
		
	}

	/**
	 * Get datas from widget and populate Beans
	 */
	public void populate(final IListKeyValuesBean paramKeyValuesBean, final Integer viewId, boolean clearList) {

		if (clearList) {
			paramKeyValuesBean.clearList();
		}
		for (int i = 0; i < _listKeyValue.size(); i++) {
			final KeyValueWidget keyValueWidget = this._listKeyValue.get(i);

			final KeyValueBean keyValueBean = new KeyValueBean(
					keyValueWidget.getKeyName());
			if (viewId != null) {
			  keyValueBean.setViewId(viewId);
			}
			
			keyValueWidget.populate(keyValueBean);
			log.config("KeyValuePanel > add KeyValueBean : "
					+ keyValueBean.getKeyValue());
			paramKeyValuesBean.addKeyValue(keyValueBean);

		}
	}
	/**
	 * Si aucune cle n'existe ajoute une cle vide et lui donne le focus
	 * @return
	 */
	public boolean initWithAtLeastOneItem() {
		if (this._listKeyValue.isEmpty()) {
			this.addKeyValue();
			return true;
		}
		return false;
	}
	//------------------------------------- protected methods
	/**
	 * A surcharger si le main panel contient des KeyValueWidget encapsulé dans un Widger container
	 * @param keyValueWidget
	 * @return
	 */
	protected Widget getWrapper(final KeyValueWidget keyValueWidget) {
		return keyValueWidget;
	}
	protected KeyValueWidget getKeyValueWidget(final String keyName) {
		for (KeyValueWidget keyValueWidget : this._listKeyValue) {
			if (keyValueWidget.getKeyName().equals(keyName)) {
				return keyValueWidget;
			}
		}
		return null;
	}
	//------------------------------------- package methods
	public void removeAllKeyValueWidget() {

		final Iterator<KeyValueWidget> iter = this._listKeyValue.iterator();
		while (iter.hasNext()) {
			final KeyValueWidget item = iter.next();
			this._main.remove(this.getWrapper(item));
			iter.remove();
		
          if (this._itemListener != null) {
			this._itemListener.removeItem(item.getKeyName());
          }
			
		}
	}
	protected void removeKeyValueWidget(KeyValueWidget item, boolean informListener) {
		this._main.remove(this.getWrapper(item));
		this._listKeyValue.remove(item);
		
		if (informListener && this._itemListener != null) {
		  this._itemListener.removeItem(item.getKeyName());
		}
		
	}
	protected void changeValueOfKey(KeyValueWidget item) {

		if (this._itemListener != null) {
			this._itemListener.changeItemValue(item.getKeyName(),
					item.getKeyValue(), item.isSelected(),
					item.isRegex(), item.isRemove());
		}
		
	}
	
	//--------------------------------------------- privates methods
	protected KeyValueWidget createKeyValue(final String keyName, boolean informListener) {
		
		final KeyValueWidget item = new KeyValueWidget(keyName, this, this._showOption, this._withAnchor);
		this._listKeyValue.add(item);
		this._main.add(this.getWrapper(item));

		if (informListener && this._itemListener != null) {
          this._itemListener.addItem(item.getKeyName());
		}
		
        return item;
	}

}
