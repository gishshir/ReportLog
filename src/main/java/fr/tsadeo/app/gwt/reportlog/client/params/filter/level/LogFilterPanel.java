package fr.tsadeo.app.gwt.reportlog.client.params.filter.level;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.Level;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean.LogLevelToFilter;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;

public class LogFilterPanel extends Composite {
	
    private final static String GROUP_LOG_LEVEL = "logLevels";
	
	 private final Panel _detailPanelLogLevel = new HorizontalPanel();
	    
	    private final CheckBox _cbLogLevel = new CheckBox("log level");
	    
	    private final ListBox _lbLogLevels = new ListBox();
	    private final RadioButton _rbEquals = new RadioButton(GROUP_LOG_LEVEL, "equals");
	    private final RadioButton _rbInfOrEquals = new RadioButton(GROUP_LOG_LEVEL, "inf. or equals");
	    private final RadioButton _rbSupOrEquals = new RadioButton(GROUP_LOG_LEVEL, "sup. or equals");
	    
	    //---------------------------------------- constructor
	    public LogFilterPanel() {
			this.initComposants();
			this.initHandler();
			this.reinit();
			this.initWidget(this.builMainPanel());
		}
	    
	    //----------------------------------------- public methods
		public void reinit() {

			this._cbLogLevel.setValue(false);
			
			this._lbLogLevels.setSelectedIndex(2);
			this._rbSupOrEquals.setValue(true);
			
			this.showDetailPanel();
		}
		/**
		 * Get datas from beans and apply to widget
		 */
		public void applyDatas (final ParamsFilterBean paramFilter) {
			
			this._cbLogLevel.setValue(paramFilter.isLogLevel());
			
			String logLevelValue = paramFilter.getLogLevelValue();
			if (logLevelValue != null) {
			for (int i = 0; i <this._lbLogLevels.getItemCount(); i++) {
				if (this._lbLogLevels.getItemText(i).equals(logLevelValue)) {
					this._lbLogLevels.setSelectedIndex(i);
					break;
				}	
			}
			}
			
			if (paramFilter.getLogLevelToFilter() != null) {
			switch (paramFilter.getLogLevelToFilter()) {
			case EQUALS: this._rbEquals.setValue(true);
				break;
			case INF_OR_EQUALS: this._rbInfOrEquals.setValue(true);
				break;
			case SUP_OR_EQUALS: this._rbSupOrEquals.setValue(true);
				break;
			}
			}
			this.showDetailPanel();
			
		}
		/**
		 * Get datas from widget and populate Beans
		 */
		public void populate (final ParamsFilterBean paramFilter) {
			

			// log level
			paramFilter.setLogLevel(this._cbLogLevel.getValue());
			paramFilter.setLogLevelValue(this._lbLogLevels.getItemText(this._lbLogLevels.getSelectedIndex()));
			final LogLevelToFilter logLevelToFilter;
			if (this._rbEquals.getValue()) {
			    	logLevelToFilter = LogLevelToFilter.EQUALS;
			} else if (this._rbInfOrEquals.getValue()) {
			    	logLevelToFilter = LogLevelToFilter.INF_OR_EQUALS;
			} else {
			    	logLevelToFilter = LogLevelToFilter.SUP_OR_EQUALS;
			}
			paramFilter.setLogLevelToFilter(logLevelToFilter);
			
		}
		//------------------------------------------------------- private methods
		private void initHandler() {
			
			// Log level	
			this._cbLogLevel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					showDetailPanel();
				}
			});
			
		}
		private Panel builMainPanel() {
			
			final String WIDTH = "100px";
			
		  // log level
		  final HorizontalPanel panelLogLevel = new HorizontalPanel();
		  panelLogLevel.setSpacing(5);
			
		  panelLogLevel.add(WidgetUtils.buildSimplePlanel(this._cbLogLevel, WIDTH ));
		  panelLogLevel.add(this.buildFilterDetailPanelLogLevel());
			
		  return panelLogLevel;	
		} 
		
		private void initComposants() {

			// init list of log levels
	        for (int i = 0; i < Level.values().length; i++) {
				   this._lbLogLevels.addItem(Level.values()[i].toString());
			   }
            this._cbLogLevel.setTitle("filter lines with log levels");
		}

		private void showDetailPanel() {

		       this._detailPanelLogLevel.setVisible(this._cbLogLevel.getValue());
			}
		private Panel buildFilterDetailPanelLogLevel() {
			
			this._lbLogLevels.setWidth("100px");
			this._detailPanelLogLevel.add(this._lbLogLevels);
			
			final Panel vpanelRadioButton = new VerticalPanel();
			vpanelRadioButton.add(this._rbEquals);
			vpanelRadioButton.add(this._rbInfOrEquals);
			vpanelRadioButton.add(this._rbSupOrEquals);
			
			this._detailPanelLogLevel.add(vpanelRadioButton);
			
			return this._detailPanelLogLevel;
		}

}
