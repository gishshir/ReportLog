package fr.tsadeo.app.gwt.reportlog.client.util;

public interface IItemListener {
	
	public void addItem(final String itemName);
	
	public void removeItem(final String itemName);
	
	public void changeItemValue (final String itemName, 
			final String itemValue, final boolean selected,
			final boolean regex, final boolean removeKey);
	

}
