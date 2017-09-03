package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.service.AppApplication;
import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;

/**
 * Container regroupant graphiquement les cles/values
 * La desactivation du container entraine la non prise en compte des cles contenues
 * sans que l'attribut, cle.selected soit modifié
 */
public class ViewKeyValueBean extends AbstractBean implements IJsonNode {
	
	public static final int DEFAULT_ID = 0;

	private final int _id;
	private boolean _actif = true;
	private final boolean _default;
	private String _name;
	
	//------------------------------------------------ static
	private static ViewKeyValueBean buildDefaultViewKeyValueBean() {
		
		ViewKeyValueBean defaultKeyValueViewBean =  new ViewKeyValueBean(DEFAULT_ID);
		defaultKeyValueViewBean.setName("default view");

		return defaultKeyValueViewBean;
	}
	
	public static ViewKeyValueBean buildViewKeyValueBeanFromJsObject(final JavaScriptObject jsObject) {
		
		if (jsObject == null){
			return buildDefaultViewKeyValueBean();
		}
		
		final int viewId = JsObjectHelper.getKeyNumber(jsObject, "viewId");
		ViewKeyValueBean keyValueViewBean =  new ViewKeyValueBean(viewId);
		keyValueViewBean.populateFromJavaScriptObject(jsObject);

		return keyValueViewBean;
	}
	
	//------------------------------------------ constructor
	public ViewKeyValueBean (final int id) {
		this._id = id;
		this._default = (id == DEFAULT_ID);
	}
	public ViewKeyValueBean (final int id, final String name, final boolean active) {
		this(id);
		this._name = name;
		this._actif = active;
	}
	
	//------------------------------------------ accessor
	public boolean isDefault() {
		return this._default;
	}
	public int getId() {
		return this._id;
	}
	public String getName() {
		return this._name;
	}
	public void setName(String name) {
		this.hasValueChanged(this._name, name);
		this._name = name;
	}
	public boolean isActif() {
		return this._actif;
	}
	public void setActif(boolean actif) {
		this.hasValueChanged(this._actif, actif);
		this._actif = actif;
	}
	
	//------------------------------------------ overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		 final JsonNode jsonNode = new JsonNode();
	        jsonNode.addBooleanValue("actif", this._actif);
	        jsonNode.addIntegerValue("viewId", this._id);
	        jsonNode.addStringValue("viewName", this._name);
			return jsonNode;
	}

	@Override
	public void populateFromJavaScriptObject(JavaScriptObject jsObject) {
		if (jsObject == null) return;
        this._actif = JsObjectHelper.getKeyBoolean(jsObject, "actif");
        this._name = JsObjectHelper.getKeyString(jsObject, "viewName");
	}

}
