package fr.tsadeo.app.gwt.reportlog.client.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;

import fr.tsadeo.app.gwt.reportlog.client.params.admin.OptionContainer;
import fr.tsadeo.app.gwt.reportlog.client.params.display.DisplayContainer;
import fr.tsadeo.app.gwt.reportlog.client.params.filter.FilterContainer;
import fr.tsadeo.app.gwt.reportlog.client.params.rules.LineRulesContainer;
import fr.tsadeo.app.gwt.reportlog.client.service.IAnimationCallback;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean;

public class ParamsView extends Composite {
	
	private final ReportLogView _container;

	private final Panel _main = new SimplePanel();
    private final TabPanel _tabPanel = new TabPanel();
    

    // filter
    private final FilterContainer _filterContainer = new FilterContainer();
 
    // display
    private final DisplayContainer _displayContainer = new DisplayContainer();
    
    // line rules
    private final LineRulesContainer _lineRulesContainer = new LineRulesContainer();
    
    // admin 
    private final OptionContainer _optionContainer = new OptionContainer(this);
	
	ParamsView(final ReportLogView container) {
		
		this._container = container;
		this.initComposants();
		this.initHandler();
		this.initWidget(this.buildMainPanel());
        this.reinit();
	}
	
	//---------------------------------------------- public methods
	 public FilterContainer getFilterPanel() {
	    	return this._filterContainer;
	    }
		public void reinit() {
			
			this._tabPanel.getTabBar().selectTab(0);

			// filter
			this._filterContainer.reinit();
			this._displayContainer.reinit();
			this._lineRulesContainer.reinit();
			this._optionContainer.reinit();
		}

	public void saveParamsToLocalStorage (final IAnimationCallback<ParamsBean> callback) {
		this._container.saveParamsToLocalStorage(callback);
	}
	public void restoreParamsFromJson(final String params, final IAnimationCallback<Boolean> callback) {
		this._container.askAndRestoreParamsFromJson(params, callback);	
	}
	//----------------------------------------- package methods

		/**
		 * Get datas from beans and apply to widget
		 */
		void applyDatas (final ParamsBean paramsBean) {
			this._filterContainer.applyDatas(paramsBean.getParamsFilterBean());
			this._displayContainer.applyDatas(paramsBean);
			this._lineRulesContainer.applyDatas(paramsBean.getParamsLineRuleBean());
			this._optionContainer.applyDatas(paramsBean.getParamsOptionsBean());
		}
		/**
		 * Get datas from widget and populate beans
		 */
		void populate (final ParamsBean paramsBean) {
			this._filterContainer.populate(paramsBean.getParamsFilterBean());
			this._displayContainer.populate(paramsBean);
			this._lineRulesContainer.populate(paramsBean.getParamsLineRuleBean());
			this._optionContainer.populate(paramsBean.getParamsOptionsBean());
		}
		//---------------------------------------------- private methods
		private Panel buildMainPanel() {
			
	       // tab panel
		   this._tabPanel.setWidth("100%");
		   this._tabPanel.setHeight("100%");
		   
		   this._tabPanel.add(this._filterContainer, "filter");
		   this._tabPanel.add(this._displayContainer, "display");
		   this._tabPanel.add(this._lineRulesContainer, "line rules");
		   this._tabPanel.add(this._optionContainer, "options");
			
            this._main.add(this._tabPanel);	
			return this._main;
		}
		
		private void initHandler() {
	
		}

		private void initComposants() {
			this._filterContainer.setItemListener(this._displayContainer);
		}
		

}
