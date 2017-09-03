package fr.tsadeo.app.gwt.reportlog.client.service.bean;


public interface IListKeyValuesBean extends IReportLogBean {

	
	public void clearList();
	public void addKeyValue (final KeyValueBean keyValueBean);
	public  ListKeyValueBean getListKeyValues();
}
