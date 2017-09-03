package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
/**
 * Dans le cadre des Paramètres gérant les règles de construction des lignes :
 * - détermine la valeur à couper au début de chaque ligne sur les logs initials avant la 
 * construction des lignes finales.
 * @author sylvie
 *
 */
public class ParamsLineRuleCutBeginBean extends AbstractBean implements IJsonNode {
	
	private static final String BEGIN = "^";
	// cut beginning
	private boolean _cutBeginning;
	private String _value;
	private boolean _regEx;
	
	// calculé
	private String _regexValue;

	//---------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addBooleanValue("cutBeginning", this._cutBeginning);
		jsonNode.addStringValue("value", this._value);
		jsonNode.addBooleanValue("regEx", this._regEx);
		return jsonNode;
	}

	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {

		if (jsObject == null) return;
		this._cutBeginning = JsObjectHelper.getKeyBoolean(jsObject, "cutBeginning");
		this._value = JsObjectHelper.getKeyString(jsObject, "value");
		this._regEx = JsObjectHelper.getKeyBoolean(jsObject, "regEx");
	}
	// --------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._cutBeginning = false;
		this._regEx = false;
		this._value = null;

	}

	public boolean isCutBeginning() {
		return this._cutBeginning;
	}

	public void seCutBeginning(final boolean cutBeginning) {
		this.hasValueChanged(this._cutBeginning, cutBeginning);
		this._cutBeginning = cutBeginning;
	}

	public boolean isRegex() {
		return this._regEx;
	}

	public void setRegex(final boolean cutBeginningRegex) {
		this.hasValueChanged(this._regEx, cutBeginningRegex);
		this._regEx = cutBeginningRegex;
	}

	public void setValue(final String cutBeginningValue) {
		this.hasValueChanged(this._value, cutBeginningValue);
		this._value = cutBeginningValue;
	}

	public String getValue() {
		return this._value;
	}
	public String getRegexValue() {
		if (this._regexValue == null) {
			this._regexValue = (this._value.startsWith(BEGIN))?this._value:
					BEGIN + this._value;
		}
		return this._regexValue;
	}


}