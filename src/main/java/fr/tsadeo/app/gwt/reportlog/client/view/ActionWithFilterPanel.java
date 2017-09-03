package fr.tsadeo.app.gwt.reportlog.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;

import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;

/**
 * Panel avec une checkbox pour activer ou desactiver globalement le filtrage
 * 
 * @author sylvie
 * 
 */
public class ActionWithFilterPanel extends SimplePanel implements IConstants {

	private final LogsView _logsView;
	private final ToggleButton _tbtApplyParam = new ToggleButton(
			"Filters disabled", "Filters enabled");

	// --------------------------------------- constructor
	public ActionWithFilterPanel(LogsView logsView) {
		this._logsView = logsView;
		this.buildMainPanel();
		this.initHandlers();
	}

	// ------------------------------------------- public methods
	public void reinit() {
		this._tbtApplyParam.setValue(true);
	}

	public boolean isWithFilter() {
		return this._tbtApplyParam.getValue();
	}

	// ------------------------------------------ private methods
	private void initHandlers() {

		this._tbtApplyParam.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				_logsView.enableActions(_tbtApplyParam.getValue());
				_logsView.applyResult();
			}
		});
	}

	private void buildMainPanel() {
		this._tbtApplyParam.setTitle("display logs with or without filters");
		this._tbtApplyParam.addStyleName(STYLE_BUTTON_APPLY_PARAMS);
		this.add(this._tbtApplyParam);
	}
}
