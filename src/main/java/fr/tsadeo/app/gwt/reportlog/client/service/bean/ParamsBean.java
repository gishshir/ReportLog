package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
/**
 * Contient tous les paramètres
 * - filter
 * - display
 * - line rules
 * @author sylvie
 *
 */
public final class ParamsBean extends AbstractBean implements IJsonNode  {
	
	public enum LogLevelToFilter {
		EQUALS, INF_OR_EQUALS, SUP_OR_EQUALS;
	}

	public enum DisplayMode {
		ALL_LINE, NEXT_JSON, ATTRIBUTS_JSON;
	}

	private final ParamsFilterBean _paramsFilterBean = new ParamsFilterBean();
	private final ParamsDisplayBean _paramsDisplayBean = new ParamsDisplayBean();
	private final ParamsLineRuleBean _paramsLineRuleBean = new ParamsLineRuleBean();
	private final OptionsBean _paramsOptionsBean = new OptionsBean();

	//------------------------------------ overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addNode("paramsFilterBean", this._paramsFilterBean.toJsonNode());
		jsonNode.addNode("paramsDisplayBean", this._paramsDisplayBean.toJsonNode());
		jsonNode.addNode("paramsLineRuleBean", this._paramsLineRuleBean.toJsonNode());
		jsonNode.addNode("paramsOptionsBean", this._paramsOptionsBean.toJsonNode());
		return jsonNode;
	}
	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {

		if (jsObject == null) return;
		this.clean();
		this.setHasChanged();
		
		// paramsFilter
		final JavaScriptObject paramsFilterJs = JsObjectHelper.getKeyObject(jsObject, "paramsFilterBean");
		if (paramsFilterJs != null) {
			this._paramsFilterBean.populateFromJavaScriptObject(paramsFilterJs);
		}
		
		// paramsDisplay
		final JavaScriptObject paramsDisplayJs = JsObjectHelper.getKeyObject(jsObject, "paramsDisplayBean");
		if (paramsDisplayJs != null) {
			this._paramsDisplayBean.populateFromJavaScriptObject(paramsDisplayJs);
		}
		
		// paramsLineRules
		final JavaScriptObject paramsLineRulesJs = JsObjectHelper.getKeyObject(jsObject, "paramsLineRuleBean");
		if (paramsLineRulesJs != null) {
			this._paramsLineRuleBean.populateFromJavaScriptObject(paramsLineRulesJs);
		}
		
		// paramsOptions
		final JavaScriptObject paramsOptionJs = JsObjectHelper.getKeyObject(jsObject, "paramsOptionsBean");
		if (paramsOptionJs != null) {
			this._paramsOptionsBean.populateFromJavaScriptObject(paramsOptionJs);
		}
	}
	
	// ----------------------------------- accessor
	public ParamsFilterBean getParamsFilterBean() {
		return _paramsFilterBean;
	}

	public ParamsDisplayBean getParamsDisplayBean() {
		return _paramsDisplayBean;
	}
	
	public ParamsLineRuleBean getParamsLineRuleBean() {
		return this._paramsLineRuleBean;
	}
    public OptionsBean getParamsOptionsBean() {
    	return this._paramsOptionsBean;
    }
	// ----------------------------------- overriding IReportLogBean
	@Override
	public void setHasBeenProcessed() {
		super.setHasBeenProcessed();
		this._paramsDisplayBean.setHasBeenProcessed();
		this._paramsFilterBean.setHasBeenProcessed();
		this._paramsLineRuleBean.setHasBeenProcessed();
	}
	@Override
	public void clean() {
		this._paramsDisplayBean.clean();
		this._paramsFilterBean.clean();
		this._paramsLineRuleBean.clean();
	}


}
