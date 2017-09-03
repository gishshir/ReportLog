package fr.tsadeo.app.gwt.reportlog.client.service.bean;


/**
 * List of KeyValueBean with convenient methods
 * @author sylvie
 *
 */
public abstract class AbstractListKeyValuesBean extends AbstractBean
		implements IListKeyValuesBean {
	
	
	private final ListKeyValueBean _listKeyValues = new ListKeyValueBean();
	
	// calculé
	private ListKeyValueBean _listSelectedKeyValues;
	
	@Override
	public  final ListKeyValueBean getListKeyValues() {
		return this._listKeyValues;
	}
	
	/**
	 * Construit la liste des cles selectionnées
	 * - keyValue.selected = true 
	 * @return
	 */
	public  ListKeyValueBean getListSelectedKeyValues() {
		
		if (this._listSelectedKeyValues == null) {
			this._listSelectedKeyValues = new ListKeyValueBean();
			for (KeyValueBean keyValueBean : this._listKeyValues) {
				if (keyValueBean.isSelected()) {
					this._listSelectedKeyValues.add(keyValueBean);
				}
			}
		}


		return this._listSelectedKeyValues;
	}
	//--------------------------------------- overriding IReportLogKeyValuesBean

	@Override
	public void clearList() {
		this._listKeyValues.clear();
		this._listSelectedKeyValues = null;
	}

	@Override
	public void addKeyValue(KeyValueBean keyValueBean) {
		this._listKeyValues.add(keyValueBean);
		this.setHasChanged();
	}

	@Override
	public void setHasBeenProcessed() {
		super.setHasBeenProcessed();
		for (int i = 0; i < this._listKeyValues.size(); i++) {
			this._listKeyValues.get(i).setHasBeenProcessed();
		}
	}
	@Override
	public void clean() {
		super.clean();
		this.clearList();
	}
}
