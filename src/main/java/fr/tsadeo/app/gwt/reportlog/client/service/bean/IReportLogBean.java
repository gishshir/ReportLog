package fr.tsadeo.app.gwt.reportlog.client.service.bean;


public interface IReportLogBean {
	
	public void clean();
	
	/**
	 * Determine si au moins une valeur du bean a été modifié
	 * @return
	 */
	public boolean hasChanged();
	
	/**
	 * Reinitialise le flag de changement à false
	 */
	public void setHasBeenProcessed();

}
