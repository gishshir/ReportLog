package fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.IListKeyValuesBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ListKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ViewKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.IItemListener;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

/**
 * View thématique d'un ensemble de cle/values
 * sous-ensemble des cle/value to find
 * @author sylvie
 *
 */
public class ViewKeyValuePanel extends Composite implements IViewListener {

	 private final static Logger log = Logger.getLogger("ViewKeyValuePanel");

	private final int _viewKeyValueId;
	private final DraggableKeyValuePanel _detailPanelKeysToFind = new DraggableKeyValuePanel();
	
	private final VerticalPanel _main = new VerticalPanel();
	private final HorizontalPanel _hPanelTitle = new HorizontalPanel();
	
	private final Label _labelViewName = new Label("saisir un nom...");
	private final TextBox _tbViewName = new TextBox();
	
	// gestion des cles
    private final Button _btAddKeyToFind = new MyButton("Add key");
    private final Button _btRemoveAllKeys = new MyButton("Remove all keys");
    
    // gestion de la vue
    private final Button _btEditOrSaveViewName = new MyButton("save");
    private final Button _btDeleteView = new MyButton("delete");
    private final Button _btCancelView = new MyButton("cancel");
    
    // listener pour les modification de view
    private final IViewListener _viewListener;
    
    private boolean _editingMode = true;
    
    //----------------------------------------- constructor
    public ViewKeyValuePanel(int viewKeyValueId, 
    		final IViewListener viewListener, final IItemListener itemListener) {
    	this._viewKeyValueId = viewKeyValueId;
    	this._viewListener = viewListener;
    	this._detailPanelKeysToFind.setItemListener(itemListener);
    	this.initHandler();
    	this.initComposants();
    	this.initWidget(this.builMainPanel());
    }
    //-------------------------------------------- overriding IViewListener
	@Override
	public void dragAndDropItem(String keyName, int fromViewId, int toViewId) {
		// do nothing
	}
	@Override
	public void editingView(int viewId, boolean editing) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addView(int viewId, final String viewName, Boolean active) {
		// nothing to do
	}

	@Override
	public void removeView(int viewId) {
		// nothing to do
	}

	@Override
	public void changeViewValues(int viewId, String viewName, Boolean activeView) {

        this.displayTitleMode(activeView);
	}

	@Override
	public void selectView(Integer viewId, final Boolean showDetail) {
		// nothing to do
	}

    //----------------------------------------- public method
	/**
	 * Supprime le widget et retourne le KeyValueBean correspondant (drag and drop process)
	 * @param keyName
	 * @return
	 */
	KeyValueBean dragKeyValueWidgetAndReturnBean(final String keyName) {
		
		return this._detailPanelKeysToFind.dragKeyValueWidgetAndReturnBean(keyName);
	}
	/**
	 * Ajoute le widget à partir du bean (drag and drop process)
	 * @param keyValueBean
	 */
	void dropKeyValueWidgetFromBean(final KeyValueBean keyValueBean) {
		// change viewId
		keyValueBean.setViewId(this._viewKeyValueId);	
		this._detailPanelKeysToFind.dropKeyValueWidgetFromBean(keyValueBean);
	}
	
	/**
	 * Return the list of KeyToFind.name
	 * @return
	 */
	List<String> getKeyNameList() {
		return this._detailPanelKeysToFind.getKeyNameList();
	}
	
	void setFocus() {
		if (this._editingMode) {
		  this._tbViewName.setFocus(true);
		}
	}

	 
	void reinit() {

		this._editingMode = false;		
		this._detailPanelKeysToFind.reinit();
			
		this.displayTitleMode(true);
			
	}
	/**
	* Get datas from beans and apply to widget
	 */
	void applyDatas (final ViewKeyValueBean viewKeyValueBean, 
			final IListKeyValuesBean  paramFilter, boolean clearList) {
		
		log.config("applyDatas() - clearList: " + clearList + " - viewId: " + this._viewKeyValueId);
		
		// nom de la vue et etat actif ou non
		if (viewKeyValueBean != null) {
			this._labelViewName.setText(viewKeyValueBean.getName());
			this._tbViewName.setText(viewKeyValueBean.getName());
		}
        if (this._editingMode) {
        	this._tbViewName.setFocus(true);
        }

        // construire la liste de cle pour cette view -----------------------------
	   final ListKeyValueBean listKeyValueForThisView = new ListKeyValueBean();
	   
	  if(paramFilter != null && paramFilter.getListKeyValues() != null) {
		  for (KeyValueBean keyValueBean : paramFilter.getListKeyValues()) {
			  
			// on retient uniquement les cles ayant une vue de meme id
			if (keyValueBean.getViewId() == this._viewKeyValueId) {
				listKeyValueForThisView.add(keyValueBean);
			}
		  }
	  }
	   if (!listKeyValueForThisView.isEmpty()) {
         this._detailPanelKeysToFind.applyDatas(listKeyValueForThisView, clearList);
	   }
	   
       this.displayTitleMode((viewKeyValueBean == null)?null:viewKeyValueBean.isActif());
	}


	/**
	* Get datas from widget and populate Beans
	*/
	void populate (final IListKeyValuesBean paramFilter) {
		
		// keys to find panel
		this._detailPanelKeysToFind.populate(paramFilter, this._viewKeyValueId, false);
				
	}
    //---------------------------------------------- private methods
	boolean isDefaultViewPanel() {
		return this._viewKeyValueId == ViewKeyValueBean.DEFAULT_ID;
	}
	private void displayTitleMode(Boolean activeView) {

		// ------------ vue en edition ou non -------------
        this._labelViewName.setVisible(!this._editingMode);
        this._tbViewName.setVisible(this._editingMode);
        
        
        this._btEditOrSaveViewName.setText((this._editingMode)?"save":"edit");
        this._btEditOrSaveViewName.setTitle((this._editingMode)?"save the name":"edit the name");
        
        this._btDeleteView.setEnabled(!this._editingMode);
        this._btCancelView.setEnabled(this._editingMode);
        
        this._btAddKeyToFind.setEnabled(!this._editingMode);
        this._btRemoveAllKeys.setEnabled(!this._editingMode);
        
        //--------------- vue active ou inactive ------------------------
        if (activeView != null) {
        	
        	 // couleur du titre
        	 this._hPanelTitle.removeStyleName(IConstants.STYLE_PANEL_VIEW_KV_TITLE_UNACTIF);
        	 this._hPanelTitle.removeStyleName(IConstants.STYLE_PANEL_VIEW_KV_TITLE_ACTIF);
        	 this._hPanelTitle.addStyleName(activeView?IConstants.STYLE_PANEL_VIEW_KV_TITLE_ACTIF:
        		  IConstants.STYLE_PANEL_VIEW_KV_TITLE_UNACTIF);
        	 
        	 // bordure du panel
        	 this._main.removeStyleName(IConstants.STYLE_VIEW_SELECTED_ACTIF);
        	 this._main.removeStyleName(IConstants.STYLE_VIEW_SELECTED_UNACTIF);
        	 this._main.addStyleName(activeView?IConstants.STYLE_VIEW_SELECTED_ACTIF:
        		 IConstants.STYLE_VIEW_SELECTED_UNACTIF );

        }
        
        this._viewListener.editingView(_viewKeyValueId, this._editingMode);
		
	}
	private Panel builMainPanel() {
		  
		  _main.setSpacing(0);
		  _main.addStyleName(IConstants.STYLE_VIEW_SELECTED_ACTIF);
		  
		  // title text
		  this._hPanelTitle.setSpacing(IConstants.SPACING_MIN);
		  this._hPanelTitle.addStyleName(IConstants.STYLE_PANEL_VIEW_KV_TITLE_ACTIF);
		  this._hPanelTitle.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		  
		  this._hPanelTitle.add(this._labelViewName);
		  this._tbViewName.setWidth(IConstants.MAX_WIDTH);
		  this._hPanelTitle.add(this._tbViewName);	
		  
		  // title buttons
		  final HorizontalPanel hPanelButtonView = new HorizontalPanel();
		  hPanelButtonView.setSpacing(IConstants.SPACING_MIN);
		  hPanelButtonView.add(this._btEditOrSaveViewName);
		  hPanelButtonView.add(this._btCancelView);
		  hPanelButtonView.add(this._btDeleteView);
		  this._hPanelTitle.add(hPanelButtonView);
		  this._hPanelTitle.setCellHorizontalAlignment(hPanelButtonView, HasHorizontalAlignment.ALIGN_RIGHT);
		  
		  _main.add(this._hPanelTitle);

		  // key to find buttons
		  final HorizontalPanel hPanelButton = new HorizontalPanel();
		  hPanelButton.setSpacing(IConstants.SPACING_MIN);
		  hPanelButton.add(this._btAddKeyToFind);
		  hPanelButton.add(this._btRemoveAllKeys);
		  
		  _main.add(hPanelButton);
		  
		  // liste des key to find
		  _main.add(this._detailPanelKeysToFind);
		  
		  		
		  return _main;	
	}

	private void initComposants() {
		
        this._btAddKeyToFind.setTitle("create a new key to find");
        this._btRemoveAllKeys.setTitle("delete all the keys");
        
        this._btDeleteView.setTitle("delete the view and transfert all the key in default view");
        this._btCancelView.setTitle("cancel the change of the name");
        
		if (this.isDefaultViewPanel()) {
			this._editingMode = false;
			this._btDeleteView.setVisible(false);
			
		}
		this.displayTitleMode(null);
	}

	private void initHandler() {
		
		
		//---------------------------------- manage vue

		// save or edit view name
		// label <----> textBox
        this._btEditOrSaveViewName.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (_editingMode) {
					final String viewName = _tbViewName.getValue();
					if (viewName.length() > 1) {
					  _labelViewName.setText(viewName);
					  _viewListener.changeViewValues(_viewKeyValueId, viewName, null);
					  _editingMode = false;
					} 
					else {
						Window.alert("view name cannot be empty!");
					}
				} else {
				  _editingMode = true;
				}
				displayTitleMode(null);
			}
		});
        
        // cancel editing mode
        this._btCancelView.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_editingMode = false;
				displayTitleMode(null);
			}
		});
        
        // delete view
        this._btDeleteView.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_viewListener.removeView(_viewKeyValueId);
			}
		});
        
        //---------------------------------- keys to find
        
		// Button add key to find
		this._btAddKeyToFind.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_detailPanelKeysToFind.addKeyValue();

			}
		});
		
		// Button remove all keys to find
		this._btRemoveAllKeys.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_detailPanelKeysToFind.removeAllKeyValueWidget();
			}
		});
	}


    
}
