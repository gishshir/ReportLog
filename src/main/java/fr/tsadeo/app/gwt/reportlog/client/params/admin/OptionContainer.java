package fr.tsadeo.app.gwt.reportlog.client.params.admin;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.IAnimationCallback;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.OptionsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
import fr.tsadeo.app.gwt.reportlog.client.view.ParamsView;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

/**
 * Panel d'administration contenant les opération de sauvegarde et restauration
 * @author sylvie
 *
 */
public class OptionContainer extends SimplePanel {
	
	private final ParamsView _mainContainer;
	
	private final CheckBox _cbOptionSaveAutomatically = new CheckBox("save automatically");
	
	private final Button _btSave = new MyButton("Save params");
	private final Button _btRestore = new MyButton("Restore params from json value");
	
	private final TextArea _taJsonParams = new TextArea();
	private final Label _errorMessage = new Label("");
	private final Label _informationMessage = new Label("");
	
	//------------------------------------------ constructor
	public OptionContainer(final ParamsView mainContainer) {
		
		this._mainContainer = mainContainer;
		this.initComposants();
		this.initHandlers();
		this.add(this.buildMainPanel());
	}

	 //----------------------------------------- public methods
	public void reinit() {
		this._taJsonParams.setValue(null);
		this._errorMessage.setText(null);
		this._errorMessage.setVisible(false);
		this._informationMessage.setVisible(false);
		this._cbOptionSaveAutomatically.setValue(true);
	}
	

	/**
	 * Get datas from beans and apply to widget
	 * @param paramsBean
	 */
	public void applyDatas ( final OptionsBean optionBean) {
		this._cbOptionSaveAutomatically.setValue(optionBean.isSaveParamAutomatically());
	}
	/**
	 * Get datas from widget and populate bean
	 * @param optionBean
	 */
	public void populate ( final OptionsBean optionBean) {
		optionBean.setSaveParamAutomatically(this._cbOptionSaveAutomatically.getValue());
	}
	//--------------------------------------------- private methods
	void waitUntil (boolean wait) {

		this._btRestore.setEnabled(!wait);
		this._btSave.setEnabled(!wait);
		this._cbOptionSaveAutomatically.setEnabled(!wait);
		
		if (wait) {
			this.addStyleName(IConstants.STYLE_CURSOR_IN_PROGRESS);
		   this._taJsonParams.addStyleName(IConstants.STYLE_IMG_ANIMATION);
		}
		else  {
			this._taJsonParams.removeStyleName(IConstants.STYLE_IMG_ANIMATION);
			this.removeStyleName(IConstants.STYLE_CURSOR_IN_PROGRESS);
		}
	}
	
	public void initHandlers() {
		
		// save params
		this._btSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				hideMessages();
				_taJsonParams.setValue(null);
				_mainContainer.saveParamsToLocalStorage(new IAnimationCallback<ParamsBean>() {
					
					@Override
					public void onSuccess(ParamsBean paramsBean) {
						 final JsonNode jsonNode = paramsBean.toJsonNode();
						_taJsonParams.setText(jsonNode.toJson(5));
						
						_informationMessage.setText("Save of params successfull !!");
						_informationMessage.setVisible(true);
					}
					
					@Override
					public void onError(Throwable ex) {
						_errorMessage.setText(ex.getMessage());
						_errorMessage.setVisible(true);
					}

					@Override
					public void showAnimation(boolean show) {
						waitUntil(show);
					}
				});
			}
		});
		
		
		// restore params
		this._btRestore.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				hideMessages();
               _mainContainer.restoreParamsFromJson(_taJsonParams.getValue(), 
            		   new IAnimationCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {

							_informationMessage.setText("Restauration of params successfull !!");
							_informationMessage.setVisible(true);
							_taJsonParams.setValue(null);
						}

						@Override
						public void onError(Throwable ex) {
							_errorMessage.setText(ex.getMessage());
							_errorMessage.setVisible(true);
						}
						@Override
						public void showAnimation(boolean show) {
							waitUntil(show);
						}
			});
				
			}
		});
	}
	
	private void hideMessages() {
		this._errorMessage.setText(null);
		this._errorMessage.setVisible(false);
		this._informationMessage.setText(null);
		this._informationMessage.setVisible(false);
	}
	private Panel buildMainPanel() {
		
		final VerticalPanel main = new VerticalPanel();
		main.setSpacing(IConstants.SPACING_MIN);
		main.setWidth("1000px");
		
		main.add(this._cbOptionSaveAutomatically);
		
		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(IConstants.SPACING_MIN);
		panelButton.add(this._btSave);
		panelButton.add(this._btRestore);
		
		main.add(panelButton);
		main.add(this._taJsonParams);
		main.add(this._errorMessage);
		main.add(this._informationMessage);
		
		return main;
	}
	
	private void initComposants() {
		
		this._taJsonParams.addStyleName(IConstants.STYLE_TEXT_AREA_LOG_SOURCE);
		
		this._btSave.setTitle("save params and show json values");
		this._btRestore.setTitle("restore params from json values");
		this._cbOptionSaveAutomatically.setTitle("save automatically when processing");
		
		this._errorMessage.addStyleName(IConstants.STYLE_WRONG_VALUE);
		this._errorMessage.setVisible(false);
		this._informationMessage.setVisible(false);
	}
}
