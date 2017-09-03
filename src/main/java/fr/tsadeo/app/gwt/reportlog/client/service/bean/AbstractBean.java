package fr.tsadeo.app.gwt.reportlog.client.service.bean;

public abstract class AbstractBean implements IReportLogBean {

	/**
	 * if true, les valeurs du bean ont ete prises en compte et non modifiées depuis (hasChanged is false)
	 * if false, le bean a été modifié depuis le dernier process
	 */
    private boolean _hasBeenProcessed;

    //------------------------------------------ overriding IReportLogBean
	@Override
	public void setHasBeenProcessed() {
		this._hasBeenProcessed = true;
	}
	@Override
	public boolean hasChanged() {
		return !this._hasBeenProcessed;
	}

	@Override
	public void clean() {
		this.setHasChanged();
	}

	//----------------------------------------------------- protected methods
	protected void setHasChanged() {
		if (!this._hasBeenProcessed) return;
		this._hasBeenProcessed = false;
	}

	protected void hasValueChanged(final boolean oldValue, final boolean newValue) {
		if (!this._hasBeenProcessed) return;
		
		if (oldValue != newValue) {
			this.setHasChanged();
		}
	}
	protected void hasValueChanged(final int oldValue, final int newValue) {
		if (!this._hasBeenProcessed) return;
		
		if (oldValue != newValue) {
			this.setHasChanged();
		}
	}
	protected void hasValueChanged(final Object oldValue, final Object newValue) {
		if (!this._hasBeenProcessed) return;
		
		if (oldValue == null && newValue == null) {
			return;
		}
		if (oldValue == null || newValue == null) {
			this.setHasChanged();
			return;
		}
		this.hasValueChanged(oldValue.toString(), newValue.toString());
	}
	protected void hasValueChanged (final String oldValue, final String newValue) {
		
		if (!this._hasBeenProcessed) return;
		
		if (oldValue == null && newValue == null) {
			return;
		}
		if (oldValue == null || newValue == null) {
			this.setHasChanged();
			return;
		}
		
		if (!oldValue.equals(newValue)) {
			this.setHasChanged();
			return;
		}
	}

}
