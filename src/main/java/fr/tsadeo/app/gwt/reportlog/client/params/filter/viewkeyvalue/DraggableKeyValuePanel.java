package fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.ui.Widget;

import fr.tsadeo.app.gwt.reportlog.client.params.KeyValuePanel;
import fr.tsadeo.app.gwt.reportlog.client.params.KeyValueWidget;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

/**
 * A panel of KeyValue that can be dragged and dropped
 * @author sylvie
 *
 */
public class DraggableKeyValuePanel extends KeyValuePanel {

	 private final static Logger log = Logger.getLogger("DraggableKeyValuePanel");
	 
	 // map KeyValueWidget.keyName - DraggableKeyValueWidget
	private  final Map<String, Widget> _mapWrapperKeyValueWidget = new HashMap<String, Widget>();
	 
	public DraggableKeyValuePanel() {
		super(true, true);
	}
	
	/**
	 * Return the list of KeyToFind.name
	 * @return
	 */
	List<String> getKeyNameList() {
		return new ArrayList<String>(this._mapWrapperKeyValueWidget.keySet());
	}
	
	/**
	 * A surcharger si le main panel contient des KeyValueWidget encapsulé dans un Widger container
	 * @param keyValueWidget
	 * @return
	 */
	@Override
	protected Widget getWrapper(final KeyValueWidget keyValueWidget) {
		
		final Widget wrapper = this._mapWrapperKeyValueWidget.get(keyValueWidget.getKeyName());
		if (wrapper != null){
			return wrapper;
		}
		
		// Creation du DraggableWidget
		DraggableWidget<KeyValueWidget> draggableWidget = new DraggableWidget<KeyValueWidget>(keyValueWidget);
		this.configureDragBehavior(draggableWidget);
		this.configureDragHandlers(draggableWidget);
		this._mapWrapperKeyValueWidget.put(keyValueWidget.getKeyName(), draggableWidget);
		
		return draggableWidget;
	}
	
	@Override
	public void removeAllKeyValueWidget() {
		super.removeAllKeyValueWidget();
		this._mapWrapperKeyValueWidget.clear();
	}
	@Override
	public void removeKeyValueWidget(KeyValueWidget item, boolean informListener) {
		super.removeKeyValueWidget(item, informListener);
		this._mapWrapperKeyValueWidget.remove(item.getKeyName());
	}
	@Override
	public void reinit() {
		super.reinit();
		this._mapWrapperKeyValueWidget.clear();
	}
	//------------------------------------------------------ public methods
	/**
	 * Supprime le widget et retourne le KeyValueBean correspondant (drag and drop process)
	 * @param keyName
	 * @return
	 */
	public KeyValueBean dragKeyValueWidgetAndReturnBean(final String keyName) {
		
		final KeyValueWidget keyValueWidget = this.getKeyValueWidget(keyName);
		if (keyValueWidget != null) {

			KeyValueBean keyValueBean = new KeyValueBean(keyName);
			keyValueWidget.populate(keyValueBean);
			this.removeKeyValueWidget(keyValueWidget, false);
			return keyValueBean;
		}
		
		return null;	
	}
	/**
	 * Ajoute le widget à partir du bean (drag and drop process)
	 * @param keyValueBean
	 */
	public void dropKeyValueWidgetFromBean(final KeyValueBean keyValueBean) {
		final KeyValueWidget keyValueWidget = this.createKeyValue(keyValueBean.getKeyName(), false);
		keyValueWidget.applyDatas(keyValueBean);
		this.changeValueOfKey(keyValueWidget);
	}
	
	
	//-------------------------------------------------- private methods
	private void configureDragBehavior (DraggableWidget<KeyValueWidget> dragWidget) {
		//configure the drag behavior (see next paragraph)
		dragWidget.setDraggingCursor(Cursor.MOVE);
		dragWidget.setDraggingOpacity((float)0.8);
	    //revert the dragging display on its original position is not drop occured
		dragWidget.setRevert(RevertOption.ON_INVALID_DROP);
		dragWidget.setHandle(IConstants.STYLE_IMG_ANCHOR);
		dragWidget.setContainment(ViewKeyToFindFilterPanel.CSS_ID);
		
		//dragWidget.useCloneAsHelper();

	}
	
	private void configureDragHandlers (DraggableWidget<KeyValueWidget> dragWidget) {

        dragWidget.addDragStartHandler(new DragStartEvent.DragStartEventHandler() {
			
			@Override
			public void onDragStart(DragStartEvent event) {
				log.config("onDragStart");
				KeyValueWidget widget =  (KeyValueWidget)event.getDraggableWidget().getOriginalWidget();
				log.config("... keyName: " + widget.getKeyName());
			}
		});
	}

}
