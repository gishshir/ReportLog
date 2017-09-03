package fr.tsadeo.app.gwt.reportlog.client.view;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue.ViewKeyToFindFilterPanel;
import fr.tsadeo.app.gwt.reportlog.client.service.IAnimationCallback;
import fr.tsadeo.app.gwt.reportlog.client.service.ICallback;
import fr.tsadeo.app.gwt.reportlog.client.service.ReportLogService;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.LogsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean.LogLevelBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean.TrimLineBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IActionCallback;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.IRefreshListener;
import fr.tsadeo.app.gwt.reportlog.client.util.LocalStorageManager;
import fr.tsadeo.app.gwt.reportlog.client.util.WidgetUtils;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.AbstractWidgetAndAccessKey.AccessKey;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.ButtonAndAccessKey;

public class ReportLogView extends SimplePanel implements IConstants {

	private final static Logger log = Logger.getLogger("ReportLogView");

	private final TabPanel _tabPanel = new TabPanel();

	private final LogsBean _logsBean = new LogsBean();
	private final ParamsBean _paramsBean = new ParamsBean();

	private LogsView _logView;
	private ParamsView _paramsView;

	LogsBean getLogsBean() {
		return this._logsBean;
	}

	ParamsBean getParamsBean() {
		return this._paramsBean;
	}

	public ReportLogView() {

		this.setWidget(this.buildMainPanel());
		this.setStylePrimaryName("reportLogView");
		this.reinit();
		this.askAndRestoreAutomaticallyFromLocalStorage();
	}

	void showKeyToFindFilterDialog(final Widget widget) {

		final ViewKeyToFindFilterPanel viewKeyToFindFilterPanel = this._paramsView
				.getFilterPanel().cutKeyToFindFilterPanel();

		final ButtonAndAccessKey actionProcessButton = new ButtonAndAccessKey(
				"process", new AccessKey('P', SHORTCUT_PROCESS),
				"Apply key filters", null, true);
		actionProcessButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				process(true, new ICallback<LogsBean>() {

					@Override
					public void onSuccess(LogsBean result) {
						_logView.setResult(result);
						actionProcessButton.setEnabled(false);
					}

					@Override
					public void onError(Throwable ex) {
						log.config("ERROR: " + ex.toString());

					}
				});
			}
		});
		actionProcessButton.setEnabled(false);

		viewKeyToFindFilterPanel.setRefreshListener(new IRefreshListener() {

			@Override
			public void refresh() {
				actionProcessButton.setEnabled(true);
			}
		});

		viewKeyToFindFilterPanel.initWithAtLeastOneItem();

		final DialogBox dialogBox = WidgetUtils.buildNoModalDialogBox(
				"Key to find", null, viewKeyToFindFilterPanel,
				actionProcessButton, false, true, new IActionCallback() {

					private void resetPanel() {
						_paramsView.getFilterPanel()
								.pasteKeyToFindFilterPanel();
						_logView.closingKeyToFindDialog();
					}

					@Override
					public void onClose() {
						resetPanel();
					}

					@Override
					public void onCancel() {
						resetPanel();
					}
				});
		dialogBox.showRelativeTo(widget);
	}

	/**
	 * Sauvegarde des paramètres dans le local storage du browser
	 * 
	 * @param callback
	 */
	void saveParamsToLocalStorage(final IAnimationCallback<ParamsBean> callback) {

		callback.showAnimation(true);
		final Command commandSave = new Command() {

			@Override
			public void execute() {
				_paramsView.populate(_paramsBean);

				final ICallback<Boolean> internalCallback = new ICallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							callback.onSuccess(_paramsBean);
						} else {
							callback.onError(new Exception(
									"Failure in save params..."));
						}
						callback.showAnimation(false);
					}

					@Override
					public void onError(Throwable ex) {
						callback.onError(ex);
						callback.showAnimation(false);
					}
				};

				ReportLogService.getInstance().asyncSaveToLocalStorage(
						_paramsBean, internalCallback);
			}
		};
		Scheduler.get().scheduleDeferred(commandSave);
	}

	/**
	 * Restauration des paramètres à partir d'un Json fourni pas l'utilisateur
	 * 
	 * @param paramsToRestore
	 * @param callback
	 */
	void askAndRestoreParamsFromJson(final String paramsToRestore,
			final IAnimationCallback<Boolean> callback) {

		if (this._paramsBean.getParamsOptionsBean().isAskBeforeRestoreParams()) {
			this.showRestoreFromJsonDialog(paramsToRestore, callback);
		} else {
			this.restoreFromJson(paramsToRestore, callback);
		}
	}

	void changeParamsAndProcess(LogLevelBean logLevelBean,
			TrimLineBean trimLineBean, final ICallback<LogsBean> callback) {

		ParamsFilterBean paramFilterBean = this._paramsBean
				.getParamsFilterBean();
		// on change le level
		paramFilterBean.updateFromAction(logLevelBean);
		// on change trim lines
		paramFilterBean.updateFromAction(trimLineBean);

		// on l'applique dans la vue
		this._paramsView.applyDatas(this._paramsBean);

		// process sans recuperation datas
		this.process(false, callback);
	}

	void process(boolean prepare, final ICallback<LogsBean> callback) {

		this.showAnimation(true);

		this._logView.clearLogArea();

		final Command commandPrepare = (prepare) ? new Command() {

			@Override
			public void execute() {
				// Get datas from widgets
				_logView.populate(_logsBean);
				_paramsView.populate(_paramsBean);
			}
		} : null;

		final Command commandProcess = new Command() {

			@Override
			public void execute() {
				final ICallback<LogsBean> animationCallback = new ICallback<LogsBean>() {

					@Override
					public void onSuccess(LogsBean result) {
						callback.onSuccess(result);
						showAnimation(false);
					}

					@Override
					public void onError(Throwable ex) {
						callback.onError(ex);
						showAnimation(false);
					}
				};

				_logView.setParams(_paramsBean);
				ReportLogService.getInstance().asyncProcess(_logsBean,
						_paramsBean, animationCallback);
			}
		};
		if (prepare) {
			Scheduler.get().scheduleDeferred(commandPrepare);
		}
		Scheduler.get().scheduleDeferred(commandProcess);

	}

	/**
	 * Restauration des paramètres à partir du local storage du browser
	 * 
	 * @param callback
	 */
	private void restoreFromLocalStorage(final ICallback<Boolean> callback) {
		this.showAnimation(true);

		final ICallback<Boolean> animationCallback = new ICallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				// apply the saved datas to the params widgets
				_paramsView.applyDatas(_paramsBean);
				if (callback != null) {
					callback.onSuccess(result);
				}
				showAnimation(false);
			}

			@Override
			public void onError(Throwable ex) {
				if (callback != null) {
					callback.onError(ex);
				}
				showAnimation(false);
			}
		};
		// populate paramsBean with saved datas
		ReportLogService.getInstance().asyncRestoreFromLocalStorage(
				this._paramsBean, animationCallback);
	}

	/**
	 * Restauration des paramètres à partir d'une structure json sous forme de
	 * String
	 * 
	 * @param callback
	 */
	private void restoreFromJson(final String paramsToRestore,
			final IAnimationCallback<Boolean> callback) {
		callback.showAnimation(true);
		final ICallback<Boolean> animationCallback = new ICallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				// apply the restored datas to the params widgets
				_paramsView.applyDatas(_paramsBean);
				if (callback != null) {
					callback.onSuccess(result);
				}
				callback.showAnimation(false);
			}

			@Override
			public void onError(Throwable ex) {
				if (callback != null) {
					callback.onError(ex);
				}
				callback.showAnimation(false);
			}
		};
		// populate paramsBean with json value
		ReportLogService.getInstance().asyncRestoreFromJson(paramsToRestore,
				this._paramsBean, animationCallback);
	}

	void clean() {

	}

	private void showAnimation(final boolean show) {

		log.fine("showAnimation() : " + show);
		if (show) {
			this.addStyleName(IConstants.STYLE_CURSOR_IN_PROGRESS);
			this._logView.waitUntil(true);
		} else {
			this.removeStyleName(IConstants.STYLE_CURSOR_IN_PROGRESS);
			this._logView.waitUntil(false);
		}
	}

	/**
	 * Restauration des paramètres à partir de la sauvegarde locale du browser -
	 * demande confirmation à l'utilisateur
	 */
	private void askAndRestoreAutomaticallyFromLocalStorage() {

		if (LocalStorageManager.get().getParams() == null) {
			return;
		}

		if (this._paramsBean.getParamsOptionsBean().isAskBeforeRestoreParams()) {
			this.showRestoreFromLocalStorageDialog();
		} else {
			this.restoreFromLocalStorage(null);
		}
	}

	private void showRestoreFromLocalStorageDialog() {

		final ButtonAndAccessKey restoreButton = new ButtonAndAccessKey(
				"Restore", new AccessKey('R', SHORTCUT_RESTORE),
				"Restore from local storage", null, true);
		final DialogBox dialogBox = WidgetUtils.buildModalDialogBox(
				"Restore params", null, new Label(
						"Do you want to restore the previous params ?"),
				restoreButton, true, false, null);
		restoreButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				restoreFromLocalStorage(new ICallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						dialogBox.hide();
					}

					@Override
					public void onError(Throwable ex) {
						dialogBox.hide();
					}
				});
			}
		});
		dialogBox.center();
	}

	private void showRestoreFromJsonDialog(final String paramsToRestore,
			final IAnimationCallback<Boolean> callback) {

		final ButtonAndAccessKey restoreButton = new ButtonAndAccessKey(
				"Restore", new AccessKey('R', SHORTCUT_RESTORE),
				"Restore from json values", null, true);
		final DialogBox dialogBox = WidgetUtils
				.buildModalDialogBox(
						"Restore params",
						null,
						new Label(
								"Do you want to restore the params from the json value ?"),
						restoreButton, true, false, null);
		restoreButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				restoreFromJson(paramsToRestore,
						new IAnimationCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								dialogBox.hide();
								callback.onSuccess(result);
							}

							@Override
							public void onError(Throwable ex) {
								dialogBox.hide();
								callback.onError(ex);
							}

							@Override
							public void showAnimation(boolean show) {
								callback.showAnimation(show);
							}
						});
			}
		});
		dialogBox.center();
	}

	private Panel buildMainPanel() {

		final SimplePanel main = new SimplePanel();

		this._tabPanel.setStyleName(IConstants.STYLE_PANEL_PRINCIPAL);

		this._logView = new LogsView(this);
		this._paramsView = new ParamsView(this);

		this._tabPanel.add(this._logView, "logs");
		this._tabPanel.add(this._paramsView, "params");

		main.add(this._tabPanel);
		this.showAnimation(true);
		return main;
	}

	public void reinit() {
		this._tabPanel.getTabBar().selectTab(0);
		this.showAnimation(false);
	}
}
