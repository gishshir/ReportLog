package fr.tsadeo.app.gwt.reportlog.client.params.filter;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.params.filter.cutline.CutLinesFilterPanel;
import fr.tsadeo.app.gwt.reportlog.client.params.filter.level.LogFilterPanel;
import fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue.ViewKeyToFindFilterPanel;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.IItemListener;

/**
 * Container principal des paramètres de type filter
 * @author sylvie
 *
 */
public final class FilterContainer extends SimplePanel {
	

	
	private VerticalPanel main;
    
	private final ViewKeyToFindFilterPanel _viewKeyToFindFilterPanel = new ViewKeyToFindFilterPanel();
    private final LogFilterPanel _logFilterPanel = new LogFilterPanel();
    private final CutLinesFilterPanel _cutLinesFilterPanel = new CutLinesFilterPanel();

    
    public FilterContainer() {
    	
    	this.addStyleName("filterPanel");
    	this.add(this.builMainPanel());
    }
  //----------------------------------------- public methods
    public ViewKeyToFindFilterPanel cutKeyToFindFilterPanel() {
    	this.main.remove(this._viewKeyToFindFilterPanel);
    	return this._viewKeyToFindFilterPanel;
    }
    public void pasteKeyToFindFilterPanel() {
    	this.main.add(this._viewKeyToFindFilterPanel);
    }
	public void reinit() {

		this._logFilterPanel.reinit();
        this._viewKeyToFindFilterPanel.reinit();
        this._cutLinesFilterPanel.reinit();
	}
	public void setItemListener (final IItemListener listener)  {
       this._viewKeyToFindFilterPanel.setItemListener(listener);
	}
	
	/**
	 * Get datas from beans and apply to widget
	 */
	public void applyDatas (final ParamsFilterBean paramFilter) {
		this._logFilterPanel.applyDatas(paramFilter);
	    this._viewKeyToFindFilterPanel.applyDatas(paramFilter);
	    this._cutLinesFilterPanel.applyDatas(paramFilter);
	}
	/**
	 * Get datas from widget and populate Beans
	 */
	public void populate (final ParamsFilterBean paramFilter) {
		paramFilter.clean();
	   this._logFilterPanel.populate(paramFilter);
       this._viewKeyToFindFilterPanel.populate(paramFilter);
       this._cutLinesFilterPanel.populate(paramFilter);
	}
	//----------------------------------------- privates methods
	private Panel builMainPanel() {
	  
	  this.main = new VerticalPanel();
	  this.main.setSpacing(IConstants.SPACING_MIN);
	  this.main.add(this._cutLinesFilterPanel);
	  this.main.add(this._logFilterPanel);
	  this.main.add(new Label("Key to find :"));
	  this.main.add(this._viewKeyToFindFilterPanel);
		
	  return this.main;	
	}

	
	

}
