package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import fr.tsadeo.app.gwt.reportlog.client.service.Level;
import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
/**
 * Paramètre gérant les règles de construction des lignes :
 * - coupure du début de ligne
 * - commencer les lignes avec les logs levels, définition des noms des log levels
 * - commencer les lignes avec une liste de clés (liste de keyValue)
 * @author sylvie
 *
 */
public class ParamsLineRuleBean extends AbstractListKeyValuesBean implements IJsonNode {
	
	 private final static Logger log = Logger.getLogger("ParamsLineRuleBean");

	// cut beginning
	private ParamsLineRuleCutBeginBean _paramsLineRuleCutBeginBean = new ParamsLineRuleCutBeginBean();
	// line width min
	private ParamsLineRuleLineWidthMinBean _paramsLineRuleLineWidthMinBean = new ParamsLineRuleLineWidthMinBean();
	
	// begin with log level
	private boolean _beginWithLogLevel;

	// begin with others
	private boolean _beginWithKeys;
	
	// reconstruire des lignes complète en se basant sur les cles beginWith
	private boolean _rebuildCompleteLine = false;


    //--------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addNode("cutBeginBean", this._paramsLineRuleCutBeginBean.toJsonNode());
		jsonNode.addNode("lineWidthMin", this._paramsLineRuleLineWidthMinBean.toJsonNode());
		jsonNode.addBooleanValue("beginWithLogLevel", this._beginWithLogLevel);
		jsonNode.addBooleanValue("beginWithKeys", this._beginWithKeys);
		jsonNode.addBooleanValue("rebuildCompleteLine", this._rebuildCompleteLine);
		// begin with list
		jsonNode.addNode("listBeginWith", this.getListKeyValues().toJsonNode());
		
		// level values
		final List<JsonNode> nodes = new ArrayList<JsonNode>(5);
		for (Level level : Level.values()) {
			nodes.add(level.toJsonNode());
		}
		jsonNode.addNodeList("levels", nodes);
		
		return jsonNode;
	}
	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {

		if (jsObject == null) return;
		log.config("populateFromJavaScriptObject(): " + JsObjectHelper.toJsonString(jsObject));
		
		final JavaScriptObject cutBeginBeanJs = JsObjectHelper.getKeyObject(jsObject, "cutBeginBean");
		if (cutBeginBeanJs != null) {
			this._paramsLineRuleCutBeginBean.populateFromJavaScriptObject(cutBeginBeanJs);
		}
		
		final JavaScriptObject lineWidthMinJs = JsObjectHelper.getKeyObject(jsObject, "lineWidthMin");
		if (lineWidthMinJs != null) {
			this._paramsLineRuleLineWidthMinBean.populateFromJavaScriptObject(lineWidthMinJs);
		}
		
		
		this._beginWithLogLevel = JsObjectHelper.getKeyBoolean(jsObject, "beginWithLogLevel");
		this._beginWithKeys = JsObjectHelper.getKeyBoolean(jsObject, "beginWithKeys");
		this._rebuildCompleteLine = JsObjectHelper.getKeyBoolean(jsObject, "rebuildCompleteLine");
		
		// begin with list
		final JavaScriptObject listBeginWithJs = JsObjectHelper.getKeyObject(jsObject, "listBeginWith");
	    if (listBeginWithJs != null) {
	    	this.getListKeyValues().populateFromJavaScriptObject(listBeginWithJs);
	    }
	    
	    // level values
	    final JsArray<JavaScriptObject> levelsJs = JsObjectHelper.getKeyJsArray(jsObject, "levels");
	    if (levelsJs != null) {

           for (int i = 0; i <levelsJs.length(); i++) {
			 final JavaScriptObject levelJs = levelsJs.get(i);
			 if (levelJs == null) continue;
			 final Level level = Level.valueOf(Level.getLevelNameFromJs(levelJs));
			 if (level != null) {
			   level.populateFromJavaScriptObject(levelJs);
			 }
		  }
	    }
	
	}

	// --------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._paramsLineRuleCutBeginBean.clean();
		this._beginWithLogLevel = true;
		this._beginWithKeys = false;
		this._rebuildCompleteLine = false;
	}



	// ----------------------------------- public methods
	public boolean isRules() {
		return this.isBeginWithKeys() || this.isBeginWithLogLevel();
	}
	// ----------------------------------- accessor
	public ParamsLineRuleLineWidthMinBean getParamsLineRuleLineWidthMinBean() {
		return this._paramsLineRuleLineWidthMinBean;
	}
    public ParamsLineRuleCutBeginBean getParamsLineRuleCutBeginBean() {
    	return this._paramsLineRuleCutBeginBean;
    }
	public boolean isBeginWithLogLevel() {
		return _beginWithLogLevel;
	}

	public void setBeginWithLogLevel(boolean beginWithLogLevel) {
		this.hasValueChanged(this._beginWithLogLevel, beginWithLogLevel);
		this._beginWithLogLevel = beginWithLogLevel;
	}

	
	public boolean isBeginWithKeys() {
		return _beginWithKeys;
	}

	public void setBeginWithKeys(boolean beginWithKeys) {
		this.hasValueChanged(this._beginWithKeys, beginWithKeys);
		this._beginWithKeys = beginWithKeys;
	}

	public boolean isRebuildCompleteLine() {
		return this._rebuildCompleteLine;
	}
    public void setRebuildCompleteLine (boolean rebuildCompleteLine) {
    	this._rebuildCompleteLine = rebuildCompleteLine;
    }

}