package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import fr.tsadeo.app.gwt.reportlog.client.service.Level;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean.LogLevelToFilter;
import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;

/**
 * Tous les paramètres de filtrage - log level - list of keys to find - cut
 * lines
 * 
 * @author sylvie
 * 
 */
public class ParamsFilterBean extends AbstractListKeyValuesBean implements
		IJsonNode {

	private final static Logger log = Logger.getLogger("ParamsFilterBean");

	// log level
	private final LogLevelBean _logLevelBean = new LogLevelBean();

	// list of viewKeyValues
	private Map<Integer, ViewKeyValueBean> _mapViewKeyValueBeanById = new HashMap<Integer, ViewKeyValueBean>();
	// key to find true si au moins une vue active
	private boolean _keyToFind = false;

	// trim lines
	private final TrimLineBean _trimLineBean = new TrimLineBean();

	// -------------------------------- implementing IJsonNode
	public JsonNode toJsonNode() {

		final JsonNode jsonNode = new JsonNode();
		jsonNode.addBooleanValue("logLevel", this._logLevelBean._logLevel);
		jsonNode.addStringValue("logLevelValue",
				this._logLevelBean._logLevelValue);
		if (this._logLevelBean._logLevelToFilter != null) {
			jsonNode.addStringValue("logLevelToFilter",
					this._logLevelBean._logLevelToFilter.name());
		}

		jsonNode.addBooleanValue("keyToFind", this._keyToFind);

		jsonNode.addBooleanValue("cutLines", this._trimLineBean._trimLines);
		jsonNode.addIntegerValue("fromLineNumber",
				this._trimLineBean._fromLineNumber);
		jsonNode.addIntegerValue("toLineNumber",
				this._trimLineBean._toLineNumber);

		// list keyValues
		jsonNode.addNode("listKeyValueBean", this.getListKeyValues()
				.toJsonNode());

		// list view
		final List<JsonNode> nodes = new ArrayList<JsonNode>();
		for (ViewKeyValueBean viewKeyValueBean : this._mapViewKeyValueBeanById
				.values()) {
			nodes.add(viewKeyValueBean.toJsonNode());
		}
		jsonNode.addNodeList("listViewKeyValueBean", nodes);
		return jsonNode;
	}

	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {

		if (jsObject == null)
			return;
		this.setHasChanged();

		this._logLevelBean._logLevel = JsObjectHelper.getKeyBoolean(jsObject,
				"logLevel");
		this._logLevelBean._logLevelValue = JsObjectHelper.getKeyString(
				jsObject, "logLevelValue");
		if (JsObjectHelper.hasKey(jsObject, "logLevelToFilter")) {
			final String logLevelToFilterStr = JsObjectHelper.getKeyString(
					jsObject, "logLevelToFilter");
			this._logLevelBean._logLevelToFilter = (logLevelToFilterStr == null) ? null
					: LogLevelToFilter.valueOf(logLevelToFilterStr);
		}
		this._keyToFind = JsObjectHelper.getKeyBoolean(jsObject, "keyToFind");

		this._trimLineBean._trimLines = JsObjectHelper.getKeyBoolean(jsObject,
				"cutLines");
		this._trimLineBean._fromLineNumber = JsObjectHelper.getKeyNumber(
				jsObject, "fromLineNumber");
		this._trimLineBean._toLineNumber = JsObjectHelper.getKeyNumber(
				jsObject, "toLineNumber");

		// list keyValues
		final JavaScriptObject listKeyValueJs = JsObjectHelper.getKeyObject(
				jsObject, "listKeyValueBean");
		if (listKeyValueJs != null) {
			this.getListKeyValues()
					.populateFromJavaScriptObject(listKeyValueJs);
		}

		// map view [viewId - view]
		final JsArray<JavaScriptObject> listViewKeyValueJs = JsObjectHelper
				.getKeyJsArray(jsObject, "listViewKeyValueBean");
		if (listViewKeyValueJs != null) {
			for (int i = 0; i < listViewKeyValueJs.length(); i++) {
				final JavaScriptObject viewKeyValueJs = listViewKeyValueJs
						.get(i);
				if (viewKeyValueJs != null) {

					ViewKeyValueBean viewKeyValueBean = ViewKeyValueBean
							.buildViewKeyValueBeanFromJsObject(viewKeyValueJs);
					this._mapViewKeyValueBeanById.put(viewKeyValueBean.getId(),
							viewKeyValueBean);
				}

			}

		}
	}

	// ------------------------------- update from action
	public void updateFromAction(LogLevelBean logLevelBean) {
		if (logLevelBean == null) {
			return;
		}
		this.setLogLevel(logLevelBean._logLevel);
		if (logLevelBean._logLevelValue != null) {
			this.setLogLevelValue(logLevelBean._logLevelValue);
		}
	}

	public void updateFromAction(TrimLineBean trimLineBean) {
		if (trimLineBean == null) {
			return;
		}
		this.setCutLines(trimLineBean._trimLines);
		this.setFromLineNumber(trimLineBean._fromLineNumber);
		this.setToLineNumber(trimLineBean._toLineNumber);
	}

	// ----------------------------------- accessor
	public boolean isCutLines() {
		return this._trimLineBean._trimLines;
	}

	public void setCutLines(final boolean cutLines) {
		this.hasValueChanged(this._trimLineBean._trimLines, cutLines);
		this._trimLineBean._trimLines = cutLines;
	}

	public int getFromLineNumber() {
		return this._trimLineBean._fromLineNumber;
	}

	public void setFromLineNumber(final int fromLineNumber) {
		this.hasValueChanged(this._trimLineBean._fromLineNumber, fromLineNumber);
		this._trimLineBean._fromLineNumber = fromLineNumber;
	}

	public int getToLineNumber() {
		return this._trimLineBean._toLineNumber;
	}

	public void setToLineNumber(final int toLineNumber) {
		this.hasValueChanged(this._trimLineBean._toLineNumber, toLineNumber);
		this._trimLineBean._toLineNumber = toLineNumber;
	}

	public boolean isLogLevel() {
		return _logLevelBean._logLevel;
	}

	public void setLogLevel(boolean logLevel) {
		this.hasValueChanged(this._logLevelBean._logLevel, logLevel);
		this._logLevelBean._logLevel = logLevel;
	}

	public String getLogLevelValue() {
		return _logLevelBean._logLevelValue;
	}

	public void setLogLevelValue(String logLevelValue) {
		this.hasValueChanged(this._logLevelBean._logLevelValue, logLevelValue);
		this._logLevelBean._logLevelValue = logLevelValue;
	}

	public LogLevelToFilter getLogLevelToFilter() {
		return _logLevelBean._logLevelToFilter;
	}

	public void setLogLevelToFilter(LogLevelToFilter logLevelToFilter) {
		this.hasValueChanged(this._logLevelBean._logLevelToFilter,
				logLevelToFilter);
		this._logLevelBean._logLevelToFilter = logLevelToFilter;
	}

	public boolean isKeyToFind() {
		return _keyToFind;
	}

	public void setKeyToFind(boolean keyToFind) {
		this.hasValueChanged(this._keyToFind, keyToFind);
		this._keyToFind = keyToFind;
	}

	public List<ViewKeyValueBean> getListViewKeyValueBean() {
		final List<Integer> list = new ArrayList<Integer>(
				this._mapViewKeyValueBeanById.keySet());
		Collections.sort(list);

		final List<ViewKeyValueBean> listViewKeyValueBean = new ArrayList<ViewKeyValueBean>(
				list.size());
		for (Integer viewId : list) {
			listViewKeyValueBean.add(this._mapViewKeyValueBeanById.get(viewId));
		}
		return listViewKeyValueBean;
	}

	/**
	 * On introduire une condition supplémentaire sur la vue qui doit etre
	 * active key selected if - keyValue.selected = true and view.actif = true
	 */
	@Override
	public final ListKeyValueBean getListSelectedKeyValues() {

		log.config("getListSelectedKeyValues()");
		final ListKeyValueBean listSelectedKeyValues = super
				.getListSelectedKeyValues();

		Iterator<KeyValueBean> iterator = listSelectedKeyValues.iterator();
		// on elimine les cles dont les vues ne sont pas actives
		while (iterator.hasNext()) {
			KeyValueBean keyValueBean = iterator.next();
			log.config("keyValue: " + keyValueBean.getKeyValue());
			int viewId = keyValueBean.getViewId();
			if (!this._mapViewKeyValueBeanById.get(viewId).isActif()) {
				log.warning(" ---> removed!!");
				iterator.remove();
			}
		}

		return listSelectedKeyValues;
	}

	public ViewKeyValueBean getViewKeyValueBean(int id) {
		return this._mapViewKeyValueBeanById.get(id);
	}

	public void addViewKeyValueBean(final ViewKeyValueBean viewKeyValueBean) {
		this._mapViewKeyValueBeanById.put(viewKeyValueBean.getId(),
				viewKeyValueBean);
	}

	// ----------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._keyToFind = false;
		this._logLevelBean.clean();
		this._mapViewKeyValueBeanById.clear();
	}

	// ================================ INNER CLASS ================
	public static class LogLevelBean {

		private boolean _logLevel;
		private String _logLevelValue;
		private LogLevelToFilter _logLevelToFilter;

		public LogLevelBean() {
		}

		public LogLevelBean(boolean logLevel, Level level) {
			this._logLevel = logLevel;
			this._logLevelValue = (level == null) ? null : level.name();
		}

		private void clean() {
			this._logLevel = false;
			this._logLevelToFilter = LogLevelToFilter.SUP_OR_EQUALS;
			this._logLevelValue = null;
		}

	}

	public static class TrimLineBean {

		// cut lines
		private boolean _trimLines = false;
		private int _fromLineNumber;
		private int _toLineNumber;

		public TrimLineBean() {
		}

		public TrimLineBean(boolean trimLines, int fromLine, int toLine) {
			this._trimLines = trimLines;
			this._fromLineNumber = fromLine;
			this._toLineNumber = toLine;
		}

		public boolean isTrimLineOn() {
			return this._trimLines;
		}

		public int getFromLine() {
			return this._fromLineNumber;
		}

		public int getToLine() {
			return this._toLineNumber;
		}
	}

}
