package fr.tsadeo.app.gwt.reportlog.client.params.rules;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.params.KeyValuePanel;
import fr.tsadeo.app.gwt.reportlog.client.service.Level;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsLineRuleBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsLineRuleCutBeginBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsLineRuleLineWidthMinBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

/**
 * Container principal des paramètres de type rules
 * @author sylvie
 *
 */
public  final class LineRulesContainer extends SimplePanel {
	
	 private final static Logger log = Logger.getLogger("LineRulesPanel");
	
	private final String MAX_WIDTH = "100%";
	private final String WIDTH = "100px";
    
    private final CheckBox _cbCutBegin = new CheckBox("cut the beginning");
    private final TextBox _tbCutBegin = new TextBox();
    private final CheckBox _cbCutBeginRegex = new CheckBox("regex");
    
	private final CheckBox _cbBeginWithLogLevel = new CheckBox("begins with [log levels...]");
	private final CheckBox _cbRebuildCompleteLine = new CheckBox("rebuild completes lines...");
	private final CheckBox _cbLineLengthMin = new CheckBox("line length");
	private final TextBox _tbLineLengthMinValue = new TextBox();
	 private final Label _wrongValue = new Label("wrong value");
	private final CheckBox _cbBeginWithKeys = new CheckBox("begins with...");
	
	private final VerticalPanel _detailPanelLogLevel = new VerticalPanel();
	private final HorizontalPanel _detailPanelLineLengthMin = new HorizontalPanel();
	
	private final KeyValuePanel _detailPanelKeysToBegin = new KeyValuePanel(false);
    private final Button _btAddKeyToFind = new MyButton("Add");
    private final Button _btRemoveAllKeys = new MyButton("Remove all keys");
	

	//------------------------------------------- constructor
	public LineRulesContainer() {
		
		this.addStyleName("linePanel");
		this.initComposants();
		this.initHandler();
		this.displayWidgetCutBegin();
		this.add(this.builMainPanel());
	}
	
	  //----------------------------------------- public methods
		public void reinit() {
			
			this._cbCutBegin.setValue(false);
			this._cbCutBeginRegex.setValue(false);
			this._tbCutBegin.setText(null);
			
			this._cbBeginWithLogLevel.setValue(true);
			this._cbRebuildCompleteLine.setValue(false);
			this._cbLineLengthMin.setValue(true);
			this._cbBeginWithKeys.setValue(false);
			this._tbLineLengthMinValue.setText("80");
			
			this._detailPanelKeysToBegin.reinit();
			
			this.displayCompleteLineWidgets();
		}
		/**
		 * Get datas from beans and apply to widget
		 * @param paramsLineRuleBean
		 */
		public void applyDatas (final ParamsLineRuleBean paramsLineRuleBean) {
			 log.config("LineRulesPanel > applyDatas()");
			
			final ParamsLineRuleCutBeginBean paramCutBean = paramsLineRuleBean.getParamsLineRuleCutBeginBean();
			this._cbCutBegin.setValue(paramCutBean.isCutBeginning());
			this._tbCutBegin.setValue(paramCutBean.getValue());
			this._cbCutBeginRegex.setValue(paramCutBean.isRegex());
			this.displayWidgetCutBegin();
			
			final ParamsLineRuleLineWidthMinBean paramsLineWidthMin = paramsLineRuleBean.getParamsLineRuleLineWidthMinBean();
			this._cbLineLengthMin.setValue(paramsLineWidthMin.isLineWidthMin());
			this._tbLineLengthMinValue.setValue(paramsLineWidthMin.getValue());
			
			// logLevels
			this._cbBeginWithLogLevel.setValue(paramsLineRuleBean.isBeginWithLogLevel());
			int levelCount = this._detailPanelLogLevel.getWidgetCount();
			for (int i = 0; i < levelCount; i++) {
				LevelValueWidget levelValueWidget = (LevelValueWidget)this._detailPanelLogLevel.getWidget(i);
				levelValueWidget.refreshValue();
			}
			
			this._cbRebuildCompleteLine.setValue(paramsLineRuleBean.isRebuildCompleteLine());
			this._cbBeginWithKeys.setValue(paramsLineRuleBean.isBeginWithKeys());
	        this._detailPanelKeysToBegin.applyDatas(paramsLineRuleBean.getListKeyValues(), true);
	        this.displayCompleteLineWidgets();
		}
		/**
		 * Get datas from widget and populate Beans
		 * @param paramsLineRuleBean
		 */
		public void populate (final ParamsLineRuleBean paramsLineRuleBean) {

			 log.config("LineRulesPanel > populate()");
			
			final ParamsLineRuleCutBeginBean paramCutBean = paramsLineRuleBean.getParamsLineRuleCutBeginBean();
			paramCutBean.seCutBeginning(this._cbCutBegin.getValue());
			paramCutBean.setValue(this._tbCutBegin.getText());
			paramCutBean.setRegex(this._cbCutBeginRegex.getValue());
			
			final ParamsLineRuleLineWidthMinBean paramsLineWidthMin = paramsLineRuleBean.getParamsLineRuleLineWidthMinBean();
			paramsLineWidthMin.setLineWidthMin(this._cbLineLengthMin.getValue());
			paramsLineWidthMin.setValue(this._tbLineLengthMinValue.getText());
						
           paramsLineRuleBean.setBeginWithLogLevel(this._cbBeginWithLogLevel.getValue());
           paramsLineRuleBean.setBeginWithKeys(this._cbBeginWithKeys.getValue());
           paramsLineRuleBean.setRebuildCompleteLine(this._cbRebuildCompleteLine.getValue());
           
         this._detailPanelKeysToBegin.populate(paramsLineRuleBean, null, true);
		}
	//----------------------------------------- privates methods
		private Panel builMainPanel() {
			
			// cut beginning
			final HorizontalPanel hpanelCutBegin = new HorizontalPanel();
			hpanelCutBegin.setSpacing(IConstants.SPACING_MIN);
			hpanelCutBegin.add(WidgetUtils.buildSimplePlanel(this._cbCutBegin, "200px"));
			hpanelCutBegin.add(this._tbCutBegin);
			hpanelCutBegin.add(WidgetUtils.buildSimplePlanel(this._cbCutBeginRegex, WIDTH));
			

			
			// log levels
			final HorizontalPanel hpanelLoglevel = new HorizontalPanel();
			hpanelLoglevel.setSpacing(IConstants.SPACING_MIN);
			hpanelLoglevel.add(WidgetUtils.buildSimplePlanel(this._cbBeginWithLogLevel, "200px"));
			hpanelLoglevel.add(this.buildLogLevelPanel());

			// rebuild completes lines
			final HorizontalPanel hpanelRebuildLine = new HorizontalPanel();
			hpanelRebuildLine.setSpacing(IConstants.SPACING_MIN);
			hpanelRebuildLine.add(WidgetUtils.buildSimplePlanel(this._cbRebuildCompleteLine, "200px"));
			hpanelRebuildLine.add(this.buildLineLengthMinPanel());
			  
			  //--- assemblage --
			  final VerticalPanel panelLine = new VerticalPanel();
			  panelLine.setWidth(MAX_WIDTH);
			  panelLine.setSpacing(IConstants.SPACING_MIN);
			  
			  panelLine.add(hpanelCutBegin);
			  panelLine.add(hpanelLoglevel);
			  panelLine.add(hpanelRebuildLine);
			  panelLine.add(this.buildKeyBeginWithPanel());
				
			  return panelLine;	
		}
		private Panel buildKeyBeginWithPanel() {
			
			 // line begin with...
			  final VerticalPanel panelKeyBeginWith = new VerticalPanel();
			  panelKeyBeginWith.setSpacing(0);
			  
			  final HorizontalPanel hPanel = new HorizontalPanel();
			  hPanel.setSpacing(IConstants.SPACING_MIN);
			  hPanel.add(WidgetUtils.buildSimplePlanel(this._cbBeginWithKeys, WIDTH ));
			  hPanel.add(this._btAddKeyToFind);
			  hPanel.add(this._btRemoveAllKeys);
			  
			  panelKeyBeginWith.add(hPanel);
			  panelKeyBeginWith.add(this._detailPanelKeysToBegin);
			  panelKeyBeginWith.setCellWidth(this._detailPanelKeysToBegin, "300px");
			  panelKeyBeginWith.setCellHorizontalAlignment(this._detailPanelKeysToBegin, HasHorizontalAlignment.ALIGN_CENTER);
			  return panelKeyBeginWith;
		}
		private Panel buildLogLevelPanel() {
						
			this._detailPanelLogLevel.setSpacing(IConstants.SPACING_MIN);
			final Level[] levelValues = Level.values();
			for (int i = 0; i < levelValues.length; i++) {
				this._detailPanelLogLevel.add(new LevelValueWidget(levelValues[i]));
			}
			return this._detailPanelLogLevel;
		}
		private Panel buildLineLengthMinPanel() {
			
			this._detailPanelLineLengthMin.setSpacing(IConstants.SPACING_MIN);
			this._detailPanelLineLengthMin.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			this._detailPanelLineLengthMin.add(this._cbLineLengthMin);
			this._detailPanelLineLengthMin.add(this._tbLineLengthMinValue);
			this._detailPanelLineLengthMin.add(new Label("characters."));
			this._detailPanelLineLengthMin.add(this._wrongValue);
			return this._detailPanelLineLengthMin;
		}
		private void initComposants() {
						
			this._tbCutBegin.addStyleName(IConstants.STYLE_TEXTBOX_TO_FIND);
			this._tbLineLengthMinValue.addStyleName(IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK);
			this._tbLineLengthMinValue.setWidth("50px");
			this._wrongValue.addStyleName(IConstants.STYLE_WRONG_VALUE);
			this._wrongValue.setVisible(false);

			this._cbCutBegin.setTitle("Cut the beginning of all line before filtering...");
			this._cbCutBeginRegex.setTitle("cut the begining with regular expression");
			this._cbBeginWithLogLevel.setTitle("Each line much begin with any of the log level ...");
			this._cbBeginWithKeys.setTitle("Each line much begin with any of the key...");
			this._cbRebuildCompleteLine.setTitle("Cutted lines are rebuilded in complete lines");
			this._cbLineLengthMin.setTitle("minimal length of complete line");
		}
		private void displayCompleteLineWidgets() {

			  final boolean showLogLevel = this._cbBeginWithLogLevel.getValue();
			  this._detailPanelLogLevel.setVisible(showLogLevel);
			  
			  final boolean rebuildCompleteLine = this._cbRebuildCompleteLine.getValue();
			  
			  // determine la visibilité de line length min
              this.displayStyleTbLineWidthMin();
			  this._detailPanelLineLengthMin.setVisible(rebuildCompleteLine);
			  
			  // determine la visibilité de beginWith...
			  this._cbBeginWithKeys.setEnabled(rebuildCompleteLine);

		       final boolean showDetailKeyToFind = rebuildCompleteLine &&
		    		   this._cbBeginWithKeys.getValue();
		       this._detailPanelKeysToBegin.setVisible(showDetailKeyToFind);
		       this._btAddKeyToFind.setVisible(showDetailKeyToFind);
		       this._btRemoveAllKeys.setVisible(showDetailKeyToFind);
			}
		private void displayWidgetCutBegin() {
			
			this._tbCutBegin.removeStyleName(IConstants.STYLE_TEXTBOX_REGEX);
			this._tbCutBegin.removeStyleName(IConstants.STYLE_TEXTBOX_UNSELECTED);
			
			// cutBegin selected
			if (this._cbCutBegin.getValue()) {
		
					// regex only
					if (this._cbCutBeginRegex.getValue()) {
						this._tbCutBegin.addStyleName(IConstants.STYLE_TEXTBOX_REGEX);
					}	
			}
			// cutBegin not selected
			else  {
				this._tbCutBegin.addStyleName(IConstants.STYLE_TEXTBOX_UNSELECTED);
			}
		}
		
		private void displayStyleTbLineWidthMin() {
			
			this._tbLineLengthMinValue.removeStyleName(IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK);
			this._tbLineLengthMinValue.removeStyleName(IConstants.STYLE_TEXTBOX_NUMBER_LINE_IN_ERROR);
			this._tbLineLengthMinValue.removeStyleName(IConstants.STYLE_TEXTBOX_UNSELECTED);
			
			
			final boolean isSelected = this._cbLineLengthMin.getValue();
			
			if (!isSelected) {
				this._tbLineLengthMinValue.addStyleName(IConstants.STYLE_TEXTBOX_UNSELECTED);
				this._wrongValue.setVisible(false);
			} else {
				final boolean controlOk = WidgetUtils.controlNumeric(this._tbLineLengthMinValue.getText());
				this._tbLineLengthMinValue.addStyleName((controlOk)?IConstants.STYLE_TEXTBOX_NUMBER_LINE_OK:IConstants.STYLE_TEXTBOX_NUMBER_LINE_IN_ERROR);
				this. _wrongValue.setVisible(!controlOk);
			}
			
		}
		private void initHandler() {
			
			//control de saisie		
            this._tbLineLengthMinValue.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					displayStyleTbLineWidthMin();
				}
			});
			
			// check box select and regex cut beginning
			final ClickHandler cbCutBeginClickHandler = new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					displayWidgetCutBegin();
				}
			};
			this._cbCutBegin.addClickHandler(cbCutBeginClickHandler);
			this._cbCutBeginRegex.addClickHandler(cbCutBeginClickHandler);
			
			// show detail logLevel and begin with key
			final ClickHandler showDetailClickHandler = new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					displayCompleteLineWidgets();
				}
			};
			
			// rebuild complete line
			this._cbRebuildCompleteLine.addClickHandler(showDetailClickHandler);
			
			// set min line length
			this._cbLineLengthMin.addClickHandler(showDetailClickHandler);
			
			// logLevel
			this._cbBeginWithLogLevel.addClickHandler(showDetailClickHandler);
			
			// key to find
			this._cbBeginWithKeys.addClickHandler(showDetailClickHandler);
			
			// Button add key to find
			this._btAddKeyToFind.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					_detailPanelKeysToBegin.addKeyValue();

				}
			});
			
			// Button remove all keys to find
			this._btRemoveAllKeys.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					_detailPanelKeysToBegin.removeAllKeyValueWidget();
				}
			});
		}
		

}
