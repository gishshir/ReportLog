package fr.tsadeo.app.gwt.reportlog.client.view;

import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.tsadeo.app.gwt.reportlog.client.service.ICallback;
import fr.tsadeo.app.gwt.reportlog.client.service.Level;
import fr.tsadeo.app.gwt.reportlog.client.service.ReportLogService.ListNumberedLine;
import fr.tsadeo.app.gwt.reportlog.client.service.ReportLogService.NumberedLine;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.LogsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean.LogLevelBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean.TrimLineBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IActionCallback;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.AbstractWidgetAndAccessKey.AccessKey;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.ButtonAndAccessKey;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.LogAreaWidget;

public class LogsView extends Composite implements IConstants {

	private final static Logger log = Logger.getLogger("LogsView");

	private static final String WIDTH_BUTTON = "90px";

	private final ReportLogView _container;

	private final FlowPanel _main = new FlowPanel();
	private final Label _labelSourceLog = new Label("source logs:");
	private final TextArea _taLogSource = new TextArea();
	private LogAreaWidget _logAreaWidget;

	private final ButtonAndAccessKey _btProcess = new ButtonAndAccessKey(
			"Process", new AccessKey('P', SHORTCUT_PROCESS),
			"Apply all the params upon source logs", WIDTH_BUTTON);
	private final ButtonAndAccessKey _btClear = new ButtonAndAccessKey("Clear",
			new AccessKey('C', SHORTCUT_CLEAR), "clear source and target logs",
			WIDTH_BUTTON);
	private final ButtonAndAccessKey _btFindKey = new ButtonAndAccessKey(
			"Find Keys", new AccessKey('K', SHORTCUT_FIND_KEY),
			"choose keys to filter", WIDTH_BUTTON);

	private ActionLevelPanel _actionLevelPanel;
	private ActionTrimLinePanel _actionTrimPanel;
	private ActionWithFilterPanel _actionWithFilterPanel;

	private boolean _showKeyToFindDialog = false;
	private boolean _shortCutEnabled = true;

	private ClickHandler _processClickHandler;

	private LogsBean _logBean;

	private boolean _hasSourceLogChanged = true;

	LogsView(final ReportLogView container) {
		this._container = container;
		this.initComposants();
		this.initHandler();
		this.initWidget(this.buildMainPanel());
		this.reinit();
	}

	void waitUntil(boolean wait) {
		this._btClear.setEnabled(!wait);
		this._btFindKey.setEnabled(!wait && !_showKeyToFindDialog);
		this._btProcess.setEnabled(!wait);

		if (wait && this._logAreaWidget != null) {
			this._logAreaWidget.addStyleName(STYLE_IMG_ANIMATION);
		} else {
			this._logAreaWidget.removeStyleName(STYLE_IMG_ANIMATION);
		}
		this._btProcess.setFocus(true);
	}

	void clearLogArea() {
		if (this._logAreaWidget != null) {
			this._logAreaWidget.clear();
		}
	}

	// ---------------------------------------------- public methods
	public void reinit() {
		this._taLogSource.setVisible(true);
		this._taLogSource.setText(null);
		this._taLogSource.setFocus(true);
		this._labelSourceLog.setVisible(true);

		if (this._logAreaWidget != null) {
			this._logAreaWidget.clear();
		}
		this._hasSourceLogChanged = true;
		this._actionWithFilterPanel.reinit();
	}

	public void showCutLineDialog(final int firstLineToCut,
			final int lastLineToCut) {

		final ButtonAndAccessKey cutButton = new ButtonAndAccessKey("Trim",
				new AccessKey('T', SHORTCUT_TRIM_ACTION), "Trim lines", null);
		final DialogBox dialogBox = WidgetUtils.buildModalDialogBox(
				"Trim lines", null, new Label("Do you want to trim between "
						+ firstLineToCut + " and " + lastLineToCut + " ?"),
				cutButton, true, false, new IActionCallback() {

					@Override
					public void onClose() {
						_logAreaWidget.cancelCutArmed();
					}

					@Override
					public void onCancel() {
						_logAreaWidget.cancelCutArmed();
					}
				});
		cutButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				_actionTrimPanel.setValues(true, firstLineToCut, lastLineToCut);
				launchProcess(null, new TrimLineBean(true, firstLineToCut,
						lastLineToCut));
				dialogBox.hide();
			}
		});
		dialogBox.center();
	}

	// ---------------------------------------------- private methods
	private Panel buildMainPanel() {

		this._main.setWidth(MAX_WIDTH);

		final VerticalPanel vPanelSource = new VerticalPanel();
		vPanelSource.setWidth(MAX_WIDTH);
		vPanelSource.setSpacing(SPACING_MIN);
		vPanelSource.add(this._labelSourceLog);
		vPanelSource.add(this._taLogSource);

		this._main.add(vPanelSource);

		final VerticalPanel vPanelTarget = new VerticalPanel();
		vPanelTarget.setWidth(MAX_WIDTH);
		vPanelTarget.setSpacing(SPACING_MIN);
		vPanelTarget.add(this.buildButtonPanel());

		this._logAreaWidget = new LogAreaWidget(this);
		vPanelTarget.add(this._logAreaWidget);

		this._main.add(vPanelTarget);
		return this._main;
	}

	private Panel buildButtonPanel() {

		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setWidth(MAX_WIDTH);
		panelButton.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

		final HorizontalPanel panelAction = new HorizontalPanel();
		panelAction.setSpacing(SPACING_MIN);

		panelAction.add(this._btClear);
		panelAction.add(this._btProcess);
		panelAction.add(this._btFindKey);

		panelButton.add(panelAction);
		panelButton.setCellHorizontalAlignment(panelAction,
				HasHorizontalAlignment.ALIGN_LEFT);

		// Action Trim Panel
		this._actionTrimPanel = new ActionTrimLinePanel(this);
		panelButton.add(_actionTrimPanel);
		panelButton.setCellHorizontalAlignment(_actionTrimPanel,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Action Level Panel
		this._actionLevelPanel = new ActionLevelPanel(this);
		panelButton.add(_actionLevelPanel);
		panelButton.setCellHorizontalAlignment(_actionLevelPanel,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Action With Filters panel
		this._actionWithFilterPanel = new ActionWithFilterPanel(this);
		panelButton.add(this._actionWithFilterPanel);
		panelButton.setCellHorizontalAlignment(this._actionWithFilterPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);

		return panelButton;
	}

	ClickHandler getProcessClickHandler() {

		if (this._processClickHandler == null) {
			this._processClickHandler = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					launchProcess();
				}
			};
		}
		return this._processClickHandler;
	}

	// launch process from button
	public void launchProcess() {
		this.launchProcess(null, null);
		this.enableActions(true);
	}

	// launch Process from Action Panel
	void launchProcess(LogLevelBean logLevelBean, TrimLineBean trimLineBean) {

		_btProcess.setEnabled(false);
		_taLogSource.setVisible(false);
		_labelSourceLog.setVisible(false);

		final ICallback<LogsBean> callback = new ICallback<LogsBean>() {

			@Override
			public void onSuccess(LogsBean result) {
				_btProcess.setEnabled(true);
				setResult(result);
			}

			@Override
			public void onError(Throwable ex) {
				log.config("ERROR: " + ex.toString());
				_btProcess.setEnabled(true);
			}
		};
		if (logLevelBean != null || trimLineBean != null) {
			_container.changeParamsAndProcess(logLevelBean, trimLineBean,
					callback);
		} else {
			_container.process(true, callback);
		}
	}

	void enableActions(boolean enabled) {

		if (!enabled) {
			this._logAreaWidget.addStyleName(STYLE_TEXT_AREA_LOG_NO_FILTER);
		} else {
			this._logAreaWidget.removeStyleName(STYLE_TEXT_AREA_LOG_NO_FILTER);
		}

		this._actionLevelPanel.setEnabled(enabled);
		this._actionTrimPanel.setEnabled(enabled);
		this._logAreaWidget.enableCutLine(enabled);
	}

	private void initHandler() {

		// ALT-KEY: shortcuts
		this._main.addDomHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ALT) {
					manageALtKey(false);
				}
			}
		}, KeyUpEvent.getType());

		this._main.addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (!event.isAltKeyDown() || !_shortCutEnabled) {
					return;
				}

				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ALT:
					manageKeyDownEvent(event);
					manageALtKey(true);
					return;
				case SHORTCUT_CLEAR:
					manageKeyDownEvent(event);
					_btClear.click();
					manageALtKey(false);
					break;

				case SHORTCUT_PROCESS:
					manageKeyDownEvent(event);
					_btProcess.click();
					manageALtKey(false);
					break;

				case SHORTCUT_FIND_KEY:
					manageKeyDownEvent(event);
					_btFindKey.click();
					manageALtKey(false);
					break;

				case SHORTCUT_LEVEL_ACTION:

					if (_actionLevelPanel.isEnabled()) {
						manageKeyDownEvent(event);
						manageShortLevelAction();
						manageALtKey(false);
					}
					break;

				case SHORTCUT_TRIM_ACTION:

					if (_actionTrimPanel.isEnabled()) {
						manageKeyDownEvent(event);
						manageShortTrimAction();
						manageALtKey(false);
					}
					break;

				}
			}
		}, KeyDownEvent.getType());

		// textarea
		this._taLogSource.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				_hasSourceLogChanged = true;
			}
		});

		// button clear
		this._btClear.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				reinit();
			}
		});

		// button process
		this._btProcess.addClickHandler(this.getProcessClickHandler());

		// button keyToFind
		this._btFindKey.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				manageShowFindKeysDialog();
			}
		});

	}

	void closingKeyToFindDialog() {
		this._showKeyToFindDialog = false;
		this._btFindKey.setEnabled(true);
		this._shortCutEnabled = true;
	}

	void populate(final LogsBean logsBean) {
		logsBean.setSourceLog(this._taLogSource.getText());
		logsBean.setChanged(this._hasSourceLogChanged);

		this._hasSourceLogChanged = false;
	}

	void setResult(final LogsBean result) {

		this._logBean = result;
		this.applyResult();

	}

	void setParams(ParamsBean paramsBean) {

		final ParamsFilterBean paramsFilterBean = paramsBean
				.getParamsFilterBean();
		String level = (paramsFilterBean.isLogLevel()) ? paramsFilterBean
				.getLogLevelValue() : null;

		this._actionLevelPanel.setSelectedLevel(level != null,
				(level == null) ? null : Level.valueOf(level));

		this._actionTrimPanel.setValues(paramsFilterBean.isCutLines(),
				paramsFilterBean.getFromLineNumber(),
				paramsFilterBean.getToLineNumber());

	}

	void applyResult() {

		if (this._logBean == null)
			return;
		final boolean isActiveLevelColor = this._logBean.isActiveLevelColor();
		final ListNumberedLine targetLog = (!this._actionWithFilterPanel
				.isWithFilter()) ? this._logBean.getNumeredLines()
				: this._logBean.getTargetLog();
		if (targetLog == null || targetLog.isEmpty()) {
			return;
		}
		if (this._logAreaWidget != null) {
			this._logAreaWidget.clear();
			for (NumberedLine numberedLine : targetLog) {
				this._logAreaWidget.addLine(numberedLine, isActiveLevelColor);

			}
		}

	}

	private void initComposants() {

		this._taLogSource.addStyleName(STYLE_TEXT_AREA_LOG_SOURCE);
		this._taLogSource.setFocus(true);

		this._taLogSource.setTitle("Copy here the initial logs");

	}

	// =============== Touches de raccourcis pour les actions ============
	private void manageKeyDownEvent(KeyDownEvent event) {
		event.preventDefault();
		event.stopPropagation();
	}

	private void manageShortLevelAction() {

		this._actionLevelPanel.doClickOnCbLevel();

	}

	private void manageALtKey(boolean active) {
		this._btProcess.showAccessKey(active);
		this._btClear.showAccessKey(active);
		this._btFindKey.showAccessKey(active);
		this._actionLevelPanel.showAccessKey(active);
		this._actionTrimPanel.showAccessKey(active);
	}

	private void manageShortTrimAction() {

		this._actionTrimPanel.doClickOnCbTrim();
	}

	private void manageShowFindKeysDialog() {
		this._showKeyToFindDialog = true;
		this._container.showKeyToFindFilterDialog(_btFindKey);
		this._btFindKey.setEnabled(false);
		this._shortCutEnabled = false;
	}

}
