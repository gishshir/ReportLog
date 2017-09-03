package fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ViewKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.IItemListener;
import fr.tsadeo.app.gwt.reportlog.client.util.IRefreshListener;

/**
 * Container principal pour représenter la liste des ViewKeyValuePanel
 * @author sylvie
 *
 */
public class ViewKeyToFindFilterPanel extends Composite implements IViewListener, IItemListener {
	
	public static final String CSS_ID = "dragZone";

	// panneau de droite : ensemble des panels de vue empilés dans un DeckPanel
	// le panel visible est celui choisi dans la liste.
	private final StackViewKeyValuePanel _stackViewKeyValuePanel = new StackViewKeyValuePanel(this, this);
	// paneau de gauche : liste des noms des vues
	private ListViewKeyValuePanel _listKeyValuePanel = new ListViewKeyValuePanel(this);
	
	//  listener extene pour propager de bas en haut
	private IItemListener _itemListener;
	
	private IRefreshListener _refreshListener;

	//---------------------------------- public methods
	public void setRefreshListener(IRefreshListener refreshListener) {
		this._refreshListener = refreshListener;
	}
	public void setItemListener (final IItemListener listener)  {
		this._itemListener  = listener;
	}
	/**
	 * Get datas from beans and apply to widget
	 * @param keyValueBean
	 */
	public void applyDatas (ParamsFilterBean paramsFilter) {
		this.reinit();
		// creation des vues (aussi dans la stack par ricochet!)
		this._listKeyValuePanel.applyDatas(paramsFilter.getListViewKeyValueBean());
		// remplissage des cles
		this._stackViewKeyValuePanel.applyDatas(paramsFilter);
		
	}
	/**
	 * Get datas from widget and populate beans
	 * @param paramFilter
	 */
	public void populate (final ParamsFilterBean paramFilter) {
		this._listKeyValuePanel.populate(paramFilter);
		this._stackViewKeyValuePanel.populate(paramFilter);
	}
	public void reinit() {
		this._listKeyValuePanel.reinit();
		this._stackViewKeyValuePanel.reinit();
	}
	
	/**
	 * Initialisation avec un panel default contenant une cle
	 */
	public void initWithAtLeastOneItem() {
   
		this._listKeyValuePanel.initWithAtLeastOneItem();
		this._stackViewKeyValuePanel.initWithAtLeastOneItem();
		
	}
	//-------------------------------------- overriding IItemListener	
	// propagation vers la liste de listeners
	//-------------------------------------------------------------
	@Override
	public void dragAndDropItem(String keyName, int fromViewId, int toViewId) {
		// propage to stack
		this._stackViewKeyValuePanel.dragAndDropItem(keyName, fromViewId, toViewId);
	}
	@Override
	public void addItem(String itemName) {
		
		this._itemListener.addItem(itemName);
		this.informRefresh();
	}
	@Override
	public void removeItem(String itemName) {

		this._itemListener.removeItem(itemName);
		this.informRefresh();
	}
	@Override
	public void changeItemValue(String itemName, String itemValue,
			boolean selected, boolean regex, boolean removeKey) {
		
		this._itemListener.changeItemValue(itemName, itemValue, selected, regex, removeKey);
		this.informRefresh();
	}
	//---------------------------------------------- overriding IViewListener
	@Override
	public void editingView(int viewId, boolean editing) {
		// transferer l'info à la liste
		this._listKeyValuePanel.editingView(viewId, editing);
	}
	@Override
	public void addView(int viewId, final String viewName, final Boolean active) {

       // transferer l'info au stack
		this._stackViewKeyValuePanel.addView(viewId, viewName, active);
		this.informRefresh();
	}

	@Override
	public void removeView(int viewId) {
		
		// ne pas supprimer vue par defaut
		if (viewId == ViewKeyValueBean.DEFAULT_ID) {
			return;
		}
			
		// transferer l'info à la liste
		this._listKeyValuePanel.removeView(viewId);
		this.informRefresh();


	}

	@Override
	public void changeViewValues(int viewId, String viewName, Boolean activeView) {

		// si changement de nom transférer vers la liste
		if (viewName != null) {
			this._listKeyValuePanel.changeViewValues(viewId, viewName, activeView);
		}
		
		// si changement property active transférer l'info vers le stack
		if (activeView != null) {
			this._stackViewKeyValuePanel.changeViewValues(viewId, viewName, activeView);
			this.informRefresh();
		}
		
	}
	
	@Override
	public void selectView (Integer viewId, final Boolean showDetail) {
		
		this._stackViewKeyValuePanel.setVisible(showDetail);
	    // transferer l'info au stack
	    this._stackViewKeyValuePanel.selectView(viewId, showDetail);
	}
    //----------------------------------------- constructor
    public ViewKeyToFindFilterPanel() {

    	this.initComposants();
    	this.initWidget(this.builMainPanel());

    }
    //---------------------------------------- private method
    private void informRefresh() {
    	if (this._refreshListener != null) {
    		this._refreshListener.refresh();
    	}
    }
    private void initComposants() {
    	//this._stackViewKeyValuePanel.setItemListener(this);
    }
	private Widget builMainPanel() {
		
		final HorizontalPanel main = new HorizontalPanel();
		main.setSpacing(IConstants.SPACING_MIN);
		main.addStyleName(IConstants.STYLE_PANEL_VIEW_KV_FILTER);
		main.getElement().setId(CSS_ID);
		
		main.add(this._listKeyValuePanel);
		main.setCellWidth(this._listKeyValuePanel, "30%");
		main.add(this._stackViewKeyValuePanel);
		main.setCellWidth(this._stackViewKeyValuePanel, "70%");
		
		return main;
	}


}
