package fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue;

import fr.tsadeo.app.gwt.reportlog.client.params.KeyValueWidget;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ViewKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel représentant la liste des ViewKeyValuePanel
 * @author sylvie
 *
 */
public class ListViewKeyValuePanel extends Composite  implements IViewListener{
	
	 private final static Logger log = Logger.getLogger("ListViewKeyValuePanel");

	// numérotation des views [0 default view]
	private int compteurId = 0;
	
	private final IViewListener _viewListener;
	
	private final Button _btAddView = new MyButton("add view");
	private final Button _btShowHideDetail = new MyButton();
	
	private final Map<Integer, DroppableWidget<ViewKeyValueWidget>> _mapViewKeyValueWidget
	                  = new HashMap<Integer, DroppableWidget<ViewKeyValueWidget>>();
	private final VerticalPanel _panelWidget = new VerticalPanel();
	
	private boolean _showDetailMode = true;
	// vue courante
	private int _currentViewId = 0;
	
	//------------------------------------ constructor
	public ListViewKeyValuePanel(final IViewListener viewListener) {
		this._viewListener = viewListener;
		
		this.initComposant();
		this.initHandler();
		this.initWidget(this.buildMainPanel());
	}
	

	//----------------------------------- overriding IViewListener
	@Override
	public void dragAndDropItem(String keyName, int fromViewId, int toViewId) {
		// do nothing
	}
	@Override
	public void editingView(int viewId,  boolean editing) {

      for ( DroppableWidget<ViewKeyValueWidget> droppableviewKeyValueWidget : _mapViewKeyValueWidget.values()) {
    	  droppableviewKeyValueWidget.getOriginalWidget().setEnabled(!editing);
	   }
	   this._btAddView.setEnabled(!editing);
	   this._btShowHideDetail.setEnabled(!editing);
	}
	@Override
	public void addView(int viewId, final String viewName, final Boolean active) {
		log.config("List addView - " + viewId + " active: " + active);
		
		this.createView(viewId, viewName, active);
		
		// transférer l'info au container principal
		this._viewListener.addView(viewId, viewName, active);
	}


	@Override
	public void removeView(int viewId) {
		log.config("removeView() " + viewId);
		final DroppableWidget<ViewKeyValueWidget> droppableviewKeyValueWidget = this._mapViewKeyValueWidget.get(viewId);
		if (droppableviewKeyValueWidget == null) {
			return;
		}
		this._panelWidget.remove(droppableviewKeyValueWidget);
		this._mapViewKeyValueWidget.remove(viewId);
		
		// select the default view
		this.selectView(ViewKeyValueBean.DEFAULT_ID, true);
		
	}

	/**
	 *  from Widget >> List : change actif
	 *  from Container >> List : change name
	 */
	@Override
	public void changeViewValues(int viewId, String viewName, Boolean activeView) {

		// si changement de nom, transférer au bon widget
		if (viewName != null) {
		   final DroppableWidget<ViewKeyValueWidget> droppableviewKeyValueWidget = this._mapViewKeyValueWidget.get(viewId);
		   if (droppableviewKeyValueWidget != null) {
			   droppableviewKeyValueWidget.getOriginalWidget().changeName(viewName);
		   }
		}
		
		// si changement property active, transférer au container
		if (activeView != null) {
			
			// on informe tous les autres widgets pour la modification des border
			this.manageWidgetBorder();
			
			this._viewListener.changeViewValues(viewId, viewName, activeView);
		}
		
	}
	/**
	 * from Widget >> List
	 */
	@Override
	public void selectView(Integer viewId, final Boolean showDetail) {
		
		this._currentViewId = (viewId == null)?this._currentViewId:viewId;
		
		// on informe tous les autres widgets pour la modification des border
		this.manageWidgetBorder();
		
		this.displayViewDetail(viewId, showDetail);	
	}
	
	
	//------------------------------------ public methods
	/**
	 * Get datas from bean and apply to widget
	 * @param paramFilter
	 */
	void applyDatas (final List<ViewKeyValueBean> listViewKeyValueBean) {
		this.reinit();

		log.config("applyDatas()");
        if (listViewKeyValueBean.isEmpty()) {
        	this.createDefaultView();
        	return;
        }
        
        for (ViewKeyValueBean viewKeyValueBean : listViewKeyValueBean) {
        	final int viewId = viewKeyValueBean.getId();
        	
        	// Create widget
        	log.config("create widget - viewId: " + viewId);
			this.addView(viewId, viewKeyValueBean.getName(), viewKeyValueBean.isActif());
			compteurId = Math.max(viewId, compteurId)+1;
			this._currentViewId = viewId;
			
			// apply datas in widget
			final DroppableWidget<ViewKeyValueWidget> droppableviewKeyValueWidget = this._mapViewKeyValueWidget.get(viewId);
			droppableviewKeyValueWidget.getOriginalWidget().applyDatas(viewKeyValueBean);
			
		}
        this.manageWidgetBorder();
	}
	
	/**
	 * Get datas from widget and apply to bean
	 * @param paramFilter
	 */
	void populate (final ParamsFilterBean paramFilter) {

		for (Integer viewId : this._mapViewKeyValueWidget.keySet()) {
			
		   final DroppableWidget<ViewKeyValueWidget> droppableviewKeyValueWidget = this._mapViewKeyValueWidget.get(viewId);					
		   final ViewKeyValueBean viewKeyValueBean = new ViewKeyValueBean(viewId);
		   droppableviewKeyValueWidget.getOriginalWidget().populate(viewKeyValueBean);
           
           if (viewKeyValueBean.isActif()) {
        	   paramFilter.setKeyToFind(true);
           }
		   
	       paramFilter.addViewKeyValueBean(viewKeyValueBean);
		}
		

	}
	

	void reinit() {
		this._mapViewKeyValueWidget.clear();
		this._panelWidget.clear();
		this._showDetailMode = true;
		this.displayMode();
	}
	
	/**
	 * Initialisation avec un panel default contenant une cle
	 */
	public void initWithAtLeastOneItem() {
    
		if (this._mapViewKeyValueWidget.isEmpty()) {
			this.createDefaultView();
		}
		
	}
	
    //------------------------------------- private methods
	
	private void createView(int viewId, final String viewName, final Boolean active) {
		// create and add a widget
		final ViewKeyValueWidget viewKeyValueWidget = new ViewKeyValueWidget(viewId, this);
		if (viewName != null || active != null) {
			viewKeyValueWidget.applyDatas(new ViewKeyValueBean(viewId, viewName, active));
		}
		
		// encapsule in an droppable widget
		final DroppableWidget<ViewKeyValueWidget> droppableWidget = new DroppableWidget<ViewKeyValueWidget>(viewKeyValueWidget);
		this.configureDroppableBehaviour(droppableWidget, viewId);
		this._panelWidget.add(droppableWidget);
		this._mapViewKeyValueWidget.put(viewId, droppableWidget);
				
		this._currentViewId = viewId;
		this.manageWidgetBorder();
	}
	private void configureDroppableBehaviour (DroppableWidget<ViewKeyValueWidget> droppableWidget, final int toViewId) {
		
		droppableWidget.setTolerance(DroppableTolerance.POINTER);
		droppableWidget.setActiveClass("orange-background");
		droppableWidget.setDroppableHoverClass("yellow-background");
		droppableWidget.addDropHandler(new DropEvent.DropEventHandler() {

			@Override
			public void onDrop(DropEvent event) {
				log.config("onDrop() in viewId : " + toViewId);
				KeyValueWidget widget =  (KeyValueWidget)event.getDraggableWidget().getOriginalWidget();
				log.config("... keyName: " + widget.getKeyName() + " from viewId " + _currentViewId);
				
				if (toViewId != _currentViewId) {
					_viewListener.dragAndDropItem(widget.getKeyName(), _currentViewId, toViewId);
				}
			}
			
		});
	}
	private void manageWidgetBorder() {
		
	  for (Integer viewId : this._mapViewKeyValueWidget.keySet()) {
		  final DroppableWidget<ViewKeyValueWidget> droppableWidget = this._mapViewKeyValueWidget.get(viewId);
		  droppableWidget.getOriginalWidget().displayBorder(this._currentViewId == viewId);
	   }
			
	}
	private void displayViewDetail (final Integer viewId, final boolean show) {
		this._showDetailMode = show;
		// propager vers container
		this._viewListener.selectView(viewId, show);
		this.displayMode();
	}
	private void displayMode() {
		this._btShowHideDetail.setText(this._showDetailMode?"hide details":"show details");
		this._btShowHideDetail.setTitle(this._showDetailMode?"hide the keys of the current view":
			"show the keys of the current view");
	}
	private void createDefaultView() {
		compteurId++;
		this.addView(ViewKeyValueBean.DEFAULT_ID, "default view", true);
	}
	private Panel buildMainPanel() {
		

		VerticalPanel main = new VerticalPanel();
		//main.setSpacing(IConstants.SPACING_MIN);
		
		// list of widget
		Panel list = new SimplePanel();
		list.addStyleName(IConstants.STYLE_PANEL_VIEW_KV_LIST);
		list.add(this._panelWidget);
		main.add(list);
		
		// buttons
		final HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setSpacing(IConstants.SPACING_MIN);
		buttonPanel.add(this._btAddView);
		buttonPanel.add(this._btShowHideDetail);	
		main.add(buttonPanel);
		
		return main;
		
	}
	
	private void initComposant() {
		
		//this._panelWidget.setSpacing(IConstants.SPACING_MIN);
		this._btAddView.setTitle("Create a new view for key to find");
		this.displayMode();
	}
	
	private void initHandler() {
		
		// button show / hide view detail
		this._btShowHideDetail.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayViewDetail(null, !_showDetailMode);
			}
		});

       // button add view
		this._btAddView.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayViewDetail(null, true);
				if (compteurId == ViewKeyValueBean.DEFAULT_ID) {
					createDefaultView();
				} else {
				addView(compteurId++, "set a name...", true);
				}
			}
		});
	}


}
