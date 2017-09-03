package fr.tsadeo.app.gwt.reportlog.client.wigdet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import fr.tsadeo.app.gwt.reportlog.client.service.ReportLogService.NumberedLine;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.view.LogsView;

/**
 * Div permettant l'affichage de lignes avec des couleurs différentes
 * 
 * @author NDMZ2720
 * 
 */
public class LogAreaWidget extends Composite implements IConstants {

	private final LogsView _logsView;

	private final Panel _main = new FlowPanel();
	private boolean _cutArmed = false;
	private boolean cutEnabled = true;
	private SimplePanel _firstLinePanelToCut = null;
	private SimplePanel _lastLinePanelToCut = null;
	private int _firstLineToCut = -1;
	private int _lastLineToCut = -1;

	private ClickHandler _iconClickHandler;

	public LogAreaWidget(LogsView logsView) {

		this._logsView = logsView;

		this._main.addStyleName(STYLE_TEXT_AREA_LOG_TARGET);
		this.initComposant();
		this.initWidget(this._main);
	}

	public void clear() {

		this._main.clear();
		this.cancelCutArmed();
	}

	public void addLine(final NumberedLine numberedLine,
			final boolean activeLevelColor) {

		final SimplePanel lineWidget = this.buildLineElement(numberedLine);
		if (activeLevelColor) {
			final String className = numberedLine.getStyleName();
			if (className != null) {
				lineWidget.addStyleName(className);
			}
		}

		lineWidget.getElement().setInnerHTML(numberedLine.toString());
		this._main.add(lineWidget);
	}

	public void cancelCutArmed() {
		this._cutArmed = false;
		this._firstLineToCut = -1;
		this._lastLineToCut = -1;
		if (this._firstLinePanelToCut != null) {
			this._firstLinePanelToCut.removeStyleName(STYLE_IMG_CUT);
			this._firstLinePanelToCut = null;
		}
		if (this._lastLinePanelToCut != null) {
			this._lastLinePanelToCut.removeStyleName(STYLE_IMG_CUT);
			this._lastLinePanelToCut = null;
		}
	}

	public void enableCutLine(boolean enabled) {
		this.cutEnabled = enabled;
		if (!enabled) {
			this.cancelCutArmed();
		}
	}

	private void initComposant() {
		this._main.setTitle("Result of logs after applying params");
	}

	// ------------ manage cut line ---------------------
	private boolean manageCutLine(boolean controlKeyDown,
			NumberedLine numberedLine, SimplePanel linePanel) {

		if (!this.cutEnabled) {
			return false;
		}
		if (controlKeyDown) {

			if (!_cutArmed) {
				_cutArmed = true;
				this._firstLineToCut = numberedLine.getNumber();
				this._firstLinePanelToCut = linePanel;
				linePanel.addStyleName(STYLE_IMG_CUT);
			} else {

				if (numberedLine.getNumber() != this._firstLineToCut) {
					this._lastLineToCut = numberedLine.getNumber();
					this._lastLinePanelToCut = linePanel;
					linePanel.addStyleName(STYLE_IMG_CUT);
					// do cut action...
					boolean ordered = _firstLineToCut < _lastLineToCut;
					_logsView.showCutLineDialog((ordered) ? _firstLineToCut
							: _lastLineToCut, (ordered) ? _lastLineToCut
							: _firstLineToCut);
				}
			}
			return true;
		}

		this.cancelCutArmed();
		return false;
	}

	// -----------------------------------------------------------
	private ClickHandler getIconClickHandler() {
		if (this._iconClickHandler == null) {

			this._iconClickHandler = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					SimplePanel linePanel = (SimplePanel) event.getSource();
					NumberedLine numberedLine = (NumberedLine) linePanel
							.getLayoutData();
					int x = event.getX();
					if (x < 20) {

						if (manageCutLine(event.isControlKeyDown(),
								numberedLine, linePanel)) {
							return;
						}

						if (!numberedLine.isPickByUser()) {
							numberedLine.setPickByUser(true);
							linePanel.addStyleName(STYLE_IMG_PICK);
						} else {
							numberedLine.setPickByUser(false);
							linePanel.removeStyleName(STYLE_IMG_PICK);
						}
					} else {
						cancelCutArmed();
					}
				}
			};
		}
		return this._iconClickHandler;
	}

	private SimplePanel buildLineElement(final NumberedLine numberedLine) {

		final SimplePanel linePanel = new SimplePanel();
		linePanel.setLayoutData(numberedLine);
		linePanel.addDomHandler(this.getIconClickHandler(),
				ClickEvent.getType());
		return linePanel;
	}
}
