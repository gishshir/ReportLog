package fr.tsadeo.app.gwt.reportlog.client.service;

public interface ICallback<T> {
	
	public void onSuccess(T result);
	
    public void onError(Throwable ex);
}
