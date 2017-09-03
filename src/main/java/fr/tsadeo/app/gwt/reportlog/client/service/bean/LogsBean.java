package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import fr.tsadeo.app.gwt.reportlog.client.service.ReportLogService.ListNumberedLine;

public final class LogsBean extends AbstractBean {
	
	// etat initial des logs
	private String _sourceLog;
	
	//toutes les lignes numérotées
	private ListNumberedLine _numeredLines;
	// lignes après filtrage et application règles display
	private ListNumberedLine _targetLog;
	private boolean _activeLevelColor = true;


	//----------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._sourceLog = null;
		this._numeredLines = null;
		this._targetLog = null;
		this._activeLevelColor = true;
	}

	//----------------------------- accessors
	public String getSourceLog() {
		return _sourceLog;
	}
	public void setSourceLog(final String _sourceLog) {
		this._sourceLog = _sourceLog;
	}
	public void setActiveLevelColor(final boolean activeLevelColor) {
		this._activeLevelColor = activeLevelColor;
	}
	public boolean isActiveLevelColor() {
		return this._activeLevelColor;
	}

	public ListNumberedLine getTargetLog() {
		return this._targetLog;
	}

	public void setTargetLog(final ListNumberedLine targetLog) {
		this._targetLog = targetLog;
	}
	public ListNumberedLine getNumeredLines() {
		return this._numeredLines;
	}
	public void setNumeredLines(ListNumberedLine numeredLines) {
		this._numeredLines = numeredLines;
	}

	//-------------------------------------- public methods
	public void setChanged(boolean changed) {
		if (changed) {
			this.setHasChanged();
		}
	}
	

}
