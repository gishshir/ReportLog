package fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.DeckPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ViewKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IItemListener;

/**
 * Pile des ViewKeyValuePanel dans un DeckPanel
 * @author sylvie
 *
 */
public class StackViewKeyValuePanel extends DeckPanel implements IViewListener {
	
	 private final static Logger log = Logger.getLogger("StackViewKeyValuePanel");

	private final Map<Integer, ViewKeyValuePanel> _mapOfViewKeyValuePanels = new HashMap<Integer, ViewKeyValuePanel>();
	private final IViewListener _viewListener;
	private final IItemListener _itemListener;
	
	//-------------------------------------------------- constructor
	public StackViewKeyValuePanel (IViewListener viewListener, final IItemListener itemListener) {
		this._viewListener = viewListener;
		this._itemListener = itemListener;
	}

	//-------------------------------------------------- overriding IViewListener
	/**
	 * Deplacement par drag and drop d'une keyValue d'une vue à une autre
	 */
	@Override
	public void dragAndDropItem(String keyName, int fromViewId, int toViewId) {
		
		log.config("dragAndDropItem() keyname: " + keyName + " from viewId: " + fromViewId + " to viewId: " + toViewId);
		final ViewKeyValuePanel fromView = this._mapOfViewKeyValuePanels.get(fromViewId);
		final ViewKeyValuePanel toView = this._mapOfViewKeyValuePanels.get(toViewId);
		if (fromView == null || toView == null) {
			return;
		}
		
		final KeyValueBean keyValueBean = fromView.dragKeyValueWidgetAndReturnBean(keyName);
		if (keyValueBean == null) {
			log.warning("cannot find keyvalue " + keyName + " in view " + fromViewId + " !!");
			return;
		}

		toView.dropKeyValueWidgetFromBean(keyValueBean);
	}


	@Override
	public void editingView(int viewId, boolean editing) {
		// transferer l'info au container
		this._viewListener.editingView(viewId, editing);
		
	}
	@Override
	public void addView(int viewId, final String viewName, final Boolean active) {
		
		log.config("Stack addView - " + viewId);
		// create a new panel view
		final ViewKeyValuePanel viewKeyValuePanel = new ViewKeyValuePanel(viewId, this, this._itemListener);
		if (viewName != null || active != null) {
			viewKeyValuePanel.applyDatas(new ViewKeyValueBean(viewId, viewName, active), null, false);
		}

		this._mapOfViewKeyValuePanels.put(viewId, viewKeyValuePanel);
		
		//DeckPanel
		this.add(viewKeyValuePanel);
		this.showWidget(this.getWidgetCount() - 1);
		viewKeyValuePanel.setFocus();
		
		
	}

	@Override
	public void removeView(int viewId) {

         final ViewKeyValuePanel viewKeyValuePanelToRemove = this._mapOfViewKeyValuePanels.get(viewId);
         if (viewKeyValuePanelToRemove == null || viewId == ViewKeyValueBean.DEFAULT_ID) {
        	 return;
         }
         
         // recuperer toutes les cles/values et les transférer dans la vue par defaut
         final List<String> listKeyNameToTransfert = viewKeyValuePanelToRemove.getKeyNameList();
         for (String keyName : listKeyNameToTransfert) {
			this.dragAndDropItem(keyName, viewId, ViewKeyValueBean.DEFAULT_ID);
		}
        
        // supprimer la vue
		this._mapOfViewKeyValuePanels.remove(viewKeyValuePanelToRemove);
		//DeckPanel
		this.remove(viewKeyValuePanelToRemove);
		
        // transmettre au container
        this._viewListener.removeView(viewId);
        
        
	}

	@Override
	public void changeViewValues(int viewId, String viewName, Boolean activeView) {
		
		// si changement de nom transférer vers le container principal
		if (viewName != null) {
		  this._viewListener.changeViewValues(viewId, viewName, activeView);
		}
		
		// si changement property active transférer l'info vers la bonne vue
		if (activeView != null) {
			 final ViewKeyValuePanel viewKeyValuePanel = this._mapOfViewKeyValuePanels.get(viewId);
	         if (viewKeyValuePanel != null) {
	        	viewKeyValuePanel.changeViewValues(viewId, viewName, activeView);
	         }
		}
		
	}	
	@Override
	public void selectView (Integer viewId, final Boolean showDetail) {

		if (viewId == null) {
			return;
		}
        final ViewKeyValuePanel viewKeyValuePanel = this._mapOfViewKeyValuePanels.get(viewId);
        if (viewKeyValuePanel == null) {
       	 return;
        }
        
        this.showWidget( this.getWidgetIndex(viewKeyValuePanel));
	}
	//---------------------------------------------- public methods
	
//	void setItemListener (final IItemListener listener)  {
//		for (ViewKeyValuePanel viewKeyValuePanel : this._mapOfViewKeyValuePanels.values()) {
//			viewKeyValuePanel.setItemListener(listener);
//		}
//	}
	
	void applyDatas (ParamsFilterBean paramFilter) {

		// attention ne pas effacer contenus des list et map... du stack...
		
		log.config("applyDatas()");
		for (Integer viewId : this._mapOfViewKeyValuePanels.keySet()) {
			final ViewKeyValuePanel viewKeyValuePanel = this._mapOfViewKeyValuePanels.get(viewId);
			viewKeyValuePanel.reinit();
			final ViewKeyValueBean viewKeyValueBean = paramFilter.getViewKeyValueBean(viewId);
			viewKeyValuePanel.applyDatas(viewKeyValueBean, paramFilter, true);
		}
		
	}
	void populate (final ParamsFilterBean paramFilter) {
		for (ViewKeyValuePanel viewKeyValuePanel :  this._mapOfViewKeyValuePanels.values()) {
			viewKeyValuePanel.populate(paramFilter);
		}	
	}
	
	void reinit() {
		this._mapOfViewKeyValuePanels.clear();
		// Deck panel
		this.clear();
	}
	/**
	 * Initialisation avec un panel default contenant une cle
	 */
	public void initWithAtLeastOneItem() {

		if (this._mapOfViewKeyValuePanels.isEmpty()) {
			this.createDefaultPanel();
		}
		
	}
	//---------------------------------------------- private methods

	private void createDefaultPanel() {
		this.addView(ViewKeyValueBean.DEFAULT_ID, "default view", true);
	}



	
}
