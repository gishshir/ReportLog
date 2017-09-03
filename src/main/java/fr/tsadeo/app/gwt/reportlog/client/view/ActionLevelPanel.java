package fr.tsadeo.app.gwt.reportlog.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.tsadeo.app.gwt.reportlog.client.service.Level;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean.LogLevelBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.AbstractWidgetAndAccessKey.AccessKey;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.CheckboxAndAccessKey;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.MyButton;

/**
 * Action button permettant de modifier le level et de lancer le process en un
 * seul click. Non visible au lancement, seulement après un premier Process
 * 
 * @author sylvie
 * 
 */
public class ActionLevelPanel extends HorizontalPanel implements IConstants {

	private final LogsView _logsView;
	private Level _lastLevel = null;
	private boolean _enabled = false;

	private final CheckboxAndAccessKey _cbLevel = new CheckboxAndAccessKey(
			"Level:", new AccessKey('L', SHORTCUT_LEVEL_ACTION), "70px");
	private final Button _btLevelError = new LevelButton(Level.ERROR,
			STYLE_LEVEL_ERROR);
	private final Button _btLevelWarn = new LevelButton(Level.WARN,
			STYLE_LEVEL_WARNING);
	private final Button _btLevelInfo = new LevelButton(Level.INFO,
			STYLE_LEVEL_INFO);
	private final Button _btLevelDebug = new LevelButton(Level.DEBUG,
			STYLE_LEVEL_DEBUG);
	private final Button _btLevelTrace = new LevelButton(Level.TRACE,
			STYLE_LEVEL_TRACE);

	// --------------------------------------- constructor
	public ActionLevelPanel(LogsView logsView) {
		this._logsView = logsView;
		this.buildMainPanel();
		this.initHandlers();
	}

	// ------------------------------------- public methods
	public void showAccessKey(boolean show) {
		this._cbLevel.showAccessKey(show);
	}

	public void doClickOnCbLevel() {
		this._cbLevel.setValue(!this._cbLevel.getValue());
		this.doLevelAction();
	}

	public void setSelectedLevel(boolean levelOn, Level selectedLevel) {

		this.setItemsVisible(true);
		this._cbLevel.setEnabled(true);
		if (selectedLevel != null) {
			this._lastLevel = selectedLevel;
		}
		this._cbLevel.setValue(levelOn);
		this.manageActivationButton();

	}

	public void setEnabled(boolean enabled) {

		this._enabled = enabled;
		this._cbLevel.setEnabled(enabled);
		if (!enabled) {
			this.enableButtons(false);
		} else {
			this.manageActivationButton();
		}
	}

	public boolean isEnabled() {
		return this._enabled;
	}

	// ------------------------------------ private methods
	private void doLevelAction() {
		manageActivationButton();
		_logsView.launchProcess(new LogLevelBean(_cbLevel.getValue(),
				_lastLevel), null);
	}

	private void enableButtons(boolean enabled) {
		this._btLevelDebug.setEnabled(enabled);
		this._btLevelError.setEnabled(enabled);
		this._btLevelInfo.setEnabled(enabled);
		this._btLevelTrace.setEnabled(enabled);
		this._btLevelWarn.setEnabled(enabled);
	}

	private void manageActivationButton() {
		boolean levelOn = this._cbLevel.getValue();
		this.enableButtons(levelOn);

		for (int i = 0; i < this.getWidgetCount(); i++) {

			Widget widget = this.getWidget(i);
			if (widget instanceof LevelButton) {

				LevelButton button = (LevelButton) widget;
				if (levelOn && button._level == this._lastLevel) {
					button.addStyleName(STYLE_LEVEL_SELECTED);
				} else {
					button.removeStyleName(STYLE_LEVEL_SELECTED);
				}
			}

		}
	}

	private void initHandlers() {
		this._cbLevel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doLevelAction();
			}
		});
	}

	private void buildMainPanel() {

		this.setSpacing(IConstants.SPACING_MIN);

		this.add(this._cbLevel);
		this.setCellVerticalAlignment(this._cbLevel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		this.add(this._btLevelTrace);
		this.add(this._btLevelDebug);
		this.add(this._btLevelInfo);
		this.add(this._btLevelWarn);
		this.add(this._btLevelError);

		this.setItemsVisible(false);
	}

	private void setItemsVisible(boolean visible) {
		this._cbLevel.setVisible(visible);
		this._btLevelDebug.setVisible(visible);
		this._btLevelError.setVisible(visible);
		this._btLevelInfo.setVisible(visible);
		this._btLevelTrace.setVisible(visible);
		this._btLevelWarn.setVisible(visible);

	}

	// ====================================== INNER CLASS
	private class LevelButton extends MyButton {

		private final Level _level;

		public LevelButton(Level level, String styleName) {
			super("" + level.name().charAt(0));
			this._level = level;
			this.addStyleName(styleName);
			this.setTitle("level " + level.name());
			this.addClickHandler(new LevelClickHandler(level));

		}
	}

	private class LevelClickHandler implements ClickHandler {

		private final Level levelToProcess;

		public LevelClickHandler(Level levelToProcess) {
			this.levelToProcess = levelToProcess;
		}

		@Override
		public void onClick(ClickEvent event) {
			_logsView.launchProcess(new LogLevelBean(_cbLevel.getValue(),
					levelToProcess), null);
		}

	}
}
