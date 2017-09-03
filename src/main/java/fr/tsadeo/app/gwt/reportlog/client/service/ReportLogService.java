package fr.tsadeo.app.gwt.reportlog.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.AttributToDisplayBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyToDisplayBlockBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.KeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ListKeyValueBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ListKeyValueBean.TypeKeyValue;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.LogsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean.DisplayMode;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean.LogLevelToFilter;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsDisplayBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsFilterBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsLineRuleBean;
import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsLineRuleCutBeginBean;
import fr.tsadeo.app.gwt.reportlog.client.util.IConstants;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.LocalStorageManager;

public class ReportLogService implements IConstants {

	private final static Logger log = Logger.getLogger("ReportLogService");
	private final static String URL = GWT.getHostPageBaseURL()
			+ "/files/skills.json";

	private final static String REGEX_ALL = "(.)*";

	private enum Step {
		A, B, C, D;
	}

	private enum StepFilter {
		beginWith, keyToFind, cutLines
	}

	private static final ReportLogService _instance = new ReportLogService();

	public static ReportLogService getInstance() {
		return _instance;
	}

	private LogsBean _logsBean;
	private ParamsBean _paramsBean;
	private ICallback<LogsBean> _callback;

	private Request currentRequest;

	// etapes intermédiaires

	// toutes les lignes numérotées
	private final ListNumberedLine _listLines = new ListNumberedLine();
	// toutes les lignes numérotées et filtrées
	private final ListNumberedLine _listFilteredLines = new ListNumberedLine();
	// toutes les lignes numérotées et filtrées avec application règles display
	private final ListNumberedLine _listDisplayedLines = new ListNumberedLine();

	private final void restoreParamsFromJson(final String paramsToRestore,
			final ParamsBean paramsBean, final ICallback<Boolean> callback) {

		if (paramsToRestore != null) {

			try {
				final JavaScriptObject paramsJs = JsObjectHelper
						.parseJson(paramsToRestore);
				paramsBean.populateFromJavaScriptObject(paramsJs);
				log.config("restoreParams from Json done!");

				callback.onSuccess(true);
			} catch (Throwable ex) {
				log.severe(ex.getMessage());
				callback.onError(new Exception(
						"Failure in parsing json value! Restauration cancelled!"));
			}
		} else {
			callback.onSuccess(false);
		}
	}

	private final void restoreParamsFromLocalStorage(
			final ParamsBean paramsBean, final ICallback<Boolean> callback) {

		final String json = LocalStorageManager.get().getParams();

		if (json != null) {
			final JavaScriptObject paramsJs = JsObjectHelper.parseJson(json);
			paramsBean.populateFromJavaScriptObject(paramsJs);
			log.config("restoreParams done");

			callback.onSuccess(true);
		} else {
			callback.onSuccess(false);
		}
	}

	/**
	 * Sauvegarde les paramètres en local
	 */
	private final void saveParamsToLocalStorage(final ParamsBean paramsBean,
			final ICallback<Boolean> callback) {

		final JsonNode jsonNode = paramsBean.toJsonNode();
		final String jsonToSave = jsonNode.toJson(5);
		LocalStorageManager.get().storeParams(jsonToSave);
		log.config("save: " + jsonToSave);

		callback.onSuccess(true);
	}

	private final void asyncCommand(final Command command,
			final ICallback<?> callback) {

		if (this.currentRequest != null) {
			this.currentRequest.cancel();
		}
		try {
			this.currentRequest = new RequestBuilder(RequestBuilder.GET, URL)
					.sendRequest(null, new RequestCallback() {

						@Override
						public void onResponseReceived(Request request,
								Response response) {

							log.config("onResponseReceived");
							if (200 == response.getStatusCode()) {

								command.execute();

							} else {
								log.config("Couldn't retrieve JSON ("
										+ response.getStatusText() + ")");
							}

						}

						@Override
						public void onError(Request request, Throwable exception) {
							callback.onError(exception);
						}
					});
		} catch (RequestException e) {
			log.severe("Couldn't retrieve JSON: " + e.getMessage());
		}
	}

	public final void asyncSaveToLocalStorage(final ParamsBean paramsBean,
			final ICallback<Boolean> callback) {
		final Command commandProcess = new Command() {

			@Override
			public void execute() {
				saveParamsToLocalStorage(paramsBean, callback);
			}
		};
		this.asyncCommand(commandProcess, callback);
	}

	public final void asyncRestoreFromJson(final String paramsToRestore,
			final ParamsBean paramsBean, final ICallback<Boolean> callback) {
		final Command commandProcess = new Command() {

			@Override
			public void execute() {
				restoreParamsFromJson(paramsToRestore, paramsBean, callback);
			}
		};
		this.asyncCommand(commandProcess, callback);
	}

	public final void asyncRestoreFromLocalStorage(final ParamsBean paramsBean,
			final ICallback<Boolean> callback) {
		final Command commandProcess = new Command() {

			@Override
			public void execute() {
				restoreParamsFromLocalStorage(paramsBean, callback);
			}
		};
		this.asyncCommand(commandProcess, callback);
	}

	public final void asyncProcess(final LogsBean logsBean,
			final ParamsBean paramsBean, final ICallback<LogsBean> callback) {

		final Command commandProcess = new Command() {

			@Override
			public void execute() {
				process(logsBean, paramsBean, callback);
			}
		};
		this.asyncCommand(commandProcess, callback);

	}

	private final void process(final LogsBean logsBean,
			final ParamsBean paramsBean, final ICallback<LogsBean> callback) {

		this._logsBean = logsBean;
		this._paramsBean = paramsBean;
		this._callback = callback;

		if (this._logsBean.hasChanged()
				|| paramsBean.getParamsLineRuleBean().hasChanged()
				|| paramsBean.getParamsDisplayBean()
						.getParamsDisplayLinesBean().hasChanged()) {
			this.processStep(Step.A);
		} else if (paramsBean.getParamsFilterBean().hasChanged()) {
			this.processStep(Step.B);
		} else {
			this.processStep(Step.C);
		}

		// mark beans as processed
		this._paramsBean.setHasBeenProcessed();
		this._logsBean.setHasBeenProcessed();
	}

	private final void processStep(final Step step) {

		switch (step) {
		case A:
			this.stepBuildLines(); // >> _listLines
		case B:
			this.stepFilterLogs(); // >> _listFilteredLines
		case C: {
			this.stepBuildDisplayLog(); // >> _listDisplayedLines
			this.endProcess();
			break;
		}
		}
	}

	private void endProcess() {
		if (this._paramsBean.getParamsOptionsBean().isSaveParamAutomatically()) {
			this.saveParamsToLocalStorage(this._paramsBean,
					new ICallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							_callback.onSuccess(_logsBean);
						}

						@Override
						public void onError(Throwable ex) {
							_callback.onSuccess(_logsBean);
						}
					});
		} else {
			this._callback.onSuccess(this._logsBean);
		}
	}

	private String cutLineBeginning(final String sourceLine,
			final ParamsLineRuleCutBeginBean cutLineBean) {
		if (!cutLineBean.isCutBeginning())
			return sourceLine;

		// gerer regex
		if (cutLineBean.isRegex()) {
			return sourceLine.replaceFirst(cutLineBean.getValue(), "");

		}
		// no regex
		else if (sourceLine.startsWith(cutLineBean.getValue())) {
			return sourceLine.substring(cutLineBean.getValue().length());
		}
		return sourceLine;
	}

	// 1ere etape
	// - couper le début des lignes si nécessaire
	// - reconstituer les lignes complètes et enlever les espaces inutiles
	// source: sourceLog
	// target: listLines
	// required if sourceLog changes
	private final void stepBuildLines() {
		log.config("stepBuildLines()");

		this._listLines.clear();
		int compteur = 0;
		if (this._logsBean == null || this._logsBean.getSourceLog() == null) {
			return;
		}

		final String sourceLog = this._logsBean.getSourceLog();
		final String[] sourceLogTab = sourceLog.split("\n");
		// reconstituer les lignes complètes
		// si vide : 1 ligne
		// sinon : si commence par [Level] ou une cle beginWith: nouvelle ligne
		// sinon ajout à la précédente

		final ParamsLineRuleBean paramsLineRuleBean = this._paramsBean
				.getParamsLineRuleBean();
		final ParamsLineRuleCutBeginBean paramsLineRuleCutBeginBean = paramsLineRuleBean
				.getParamsLineRuleCutBeginBean();

		// determine si on doit reconstruire ou non des lignes complètes
		// si false, toutes les lignes sont des nouvelles lignes
		// on maitient qd meme la recheche des levels pour la coloration
		// syntaxique
		final boolean rebuildCompleteLine = paramsLineRuleBean
				.isRebuildCompleteLine();

		// Dans le cas où on veut reconstituer la ligne complète, chercher
		// minimum de caractéres
		// d'une ligne complete pourqu'elle puisse être chainée avec la ligne
		// suivante
		final boolean analyseLineWidth = rebuildCompleteLine
				&& paramsLineRuleBean.getParamsLineRuleLineWidthMinBean()
						.isLineWidthMin();
		int lineWidthMin = (!analyseLineWidth) ? -1 : paramsLineRuleBean
				.getParamsLineRuleLineWidthMinBean().getIntValue();

		// on mémorise la longueur de la ligne précédente
		int previousLineWidth = -1;

		StringBuilder targetSb = null;
		Level levelLastLine = null;
		final List<Level> currentLevelList = new ArrayList<Level>(1);

		// pour chaque ligne source originale
		for (int i = 0; i < sourceLogTab.length; i++) {

			final String sourceLine = sourceLogTab[i].trim();
			final String cuttedSourceLine = this.cutLineBeginning(sourceLine,
					paramsLineRuleCutBeginBean);

			// longueur de la ligne
			final int lineWidth = (analyseLineWidth) ? cuttedSourceLine
					.length() : -1;
			if (lineWidth == 0) {
				// next line
				continue;
			}

			final boolean forceNewLine = (!analyseLineWidth) ? false
					: previousLineWidth < lineWidthMin;
			// if (forceNewLine) {
			// log.warning(i + " > width: " + lineWidth + " - previous width: "
			// + previousLineWidth + " - min width: " + lineWidthMin);
			// }
			previousLineWidth = lineWidth;

			// new Line ...
			if (this.isNewLine(cuttedSourceLine, paramsLineRuleBean,
					currentLevelList, rebuildCompleteLine, forceNewLine)) {

				// ajouter la ligne précédente à la liste
				if (targetSb != null) {
					this._listLines.add(this.manageLineNumbers(
							targetSb.toString(), compteur++, levelLastLine));
				}

				// debut de la ligne
				levelLastLine = (currentLevelList.isEmpty()) ? null
						: currentLevelList.get(0);
				currentLevelList.clear();

				targetSb = new StringBuilder();
				targetSb.append(cuttedSourceLine);
				continue;
			}

			// suite de ligne
			if (targetSb == null) {
				targetSb = new StringBuilder();
			}

			// à ajouter à la ligne précédente
			targetSb.append(cuttedSourceLine);
		}

		// fin du log
		if (targetSb != null) {
			this._listLines.add(this.manageLineNumbers(targetSb.toString(),
					compteur++, levelLastLine));
		}

	}

	private final NumberedLine manageLineNumbers(final String line,
			final int compteur, final Level level) {
		if (_paramsBean.getParamsDisplayBean().isLineNumbers()) {
			return new NumberedLine(compteur, line, level, this._paramsBean
					.getParamsDisplayBean().isLevelColor());
		} else {
			return new NumberedLine(line, level, this._paramsBean
					.getParamsDisplayBean().isLevelColor());
		}
	}

	/**
	 * 
	 * @param line
	 * @param paramsLineRuleBean
	 * @param currentLevelList
	 *            : au sein de la méthode determine le level de la ligne
	 * @return
	 */
	private final boolean isNewLine(final String line,
			final ParamsLineRuleBean paramsLineRuleBean,
			final List<Level> currentLevelList,
			final boolean rebuildCompleteLine, boolean forceNewLine) {

		// dans tous les cas on détermine le level de la ligne
		if (paramsLineRuleBean.isBeginWithLogLevel()) {

			boolean result = this.lineStartWithLevels(line, currentLevelList);
			if (result)
				return true;
		}

		// on force la ligne comme nouvelle ligne
		if (forceNewLine) {
			return true;
		}

		// if no rules or not rebuild complete line >> keep the line
		if (!paramsLineRuleBean.isRules() || !rebuildCompleteLine) {
			return true;
		}

		// /----------------------------------------------------------
		// Here we must determine if the line is a new line or not
		// ------------------------------------------------------------

		if (!paramsLineRuleBean.isBeginWithKeys())
			return false;

		// Does this line begin with any of the key ?
		final List<KeyValueBean> list = paramsLineRuleBean
				.getListSelectedKeyValues();
		for (int i = 0; i < list.size(); i++) {

			final String keyValue = list.get(i).getKeyValue();
			if (line.startsWith(keyValue)) {
				return true;
			}
		}

		return false;

	}

	private final boolean lineStartWithLevels(final String line,
			final List<Level> currentLevelList) {

		final Level[] levelValues = Level.values();
		for (int i = 0; i < levelValues.length; i++) {
			if (line.startsWith(levelValues[i].getValue())) {
				currentLevelList.add(levelValues[i]);
				return true;
			}
		}
		return false;
	}

	/**
	 * si la ligne commence avec un des levels de la liste alors return true
	 */
	private final boolean lineStartWithLevels(final NumberedLine numberedLine,
			final List<Level> levels) {

		for (Level level : levels) {
			if (numberedLine._line.startsWith(level.getValue())) {
				return true;
			}
		}
		return false;
	}

	// 2èmeétape
	// filtrage en fonction des criteres de filtres
	// source: listLines
	// target: listFilteredLines
	// required if sourceLog or filter changes
	private final void stepFilterLogs() {
		log.config("stepFilterLogs()");
		this._listFilteredLines.clear();
		final ParamsFilterBean paramFilter = this._paramsBean
				.getParamsFilterBean();

		// keys to find(only selected keys)
		final ListKeyValueBean listKeyToFindOrRemove = (paramFilter
				.isKeyToFind()) ? paramFilter.getListSelectedKeyValues() : null;

		// determiner la liste des levels à controller
		List<Level> listLevelsToControl = null;

		// log level filter
		if (paramFilter.isLogLevel()) {

			listLevelsToControl = new ArrayList<Level>();
			final Level level = Level.valueOf(paramFilter.getLogLevelValue());

			boolean resultFilter = false;
			final Level[] levelValues = Level.values();

			if (paramFilter.getLogLevelToFilter() == LogLevelToFilter.EQUALS) {
				// LEVEL EQUALS
				listLevelsToControl.add(level);
			} else if (paramFilter.getLogLevelToFilter() == LogLevelToFilter.INF_OR_EQUALS) {
				// LEVEL INF OR EQUALS
				for (int l = level.ordinal(); !resultFilter
						&& l < levelValues.length; l++) {
					listLevelsToControl.add(levelValues[l]);
				}
			} else {
				// LEVEL SUP OR EQUALS
				for (int l = level.ordinal(); !resultFilter && l >= 0; l--) {
					listLevelsToControl.add(levelValues[l]);
				}
			}

		}

		// Pour chaque ligne
		for (int i = 0; i < this._listLines.size(); i++) {

			final NumberedLine currentNumeredLine = this._listLines.get(i);
			if (currentNumeredLine == null)
				continue;

			// passage de tous les filtres en chaine
			if (this.filterWithAllFilters(currentNumeredLine,
					listLevelsToControl, listKeyToFindOrRemove, paramFilter,
					StepFilter.cutLines)) {
				this._listFilteredLines.add(currentNumeredLine);
			}
		}
	}

	/**
	 * 
	 * @param numberedline
	 *            required
	 * @param levels
	 *            can be null
	 * @param listBeginWithKeys
	 *            can be null
	 * @param listKeyToFindOrRemove
	 *            can be null (only selected keys)
	 * @param paramsLineRuleBean
	 * @param stepFilter
	 * @return true si tous les filtres ont été passés avec succès
	 */
	private boolean filterWithAllFilters(final NumberedLine numberedline,
			final List<Level> levels,
			final ListKeyValueBean listKeyToFindOrRemove,
			final ParamsFilterBean paramsFilterBean, final StepFilter stepFilter) {

		StepFilter nextStep = null;
		boolean result = false;
		switch (stepFilter) {

		case cutLines:
			result = this.filterLineCutLines(numberedline, paramsFilterBean);
			nextStep = StepFilter.beginWith;
			break;

		// begin with log levels
		case beginWith:
			result = this.filterLineBeginWith(numberedline, levels, null);
			nextStep = StepFilter.keyToFind;
			break;

		case keyToFind:
			result = this.filterLineWithKeyToFind(numberedline,
					listKeyToFindOrRemove);
			nextStep = null;
			break;
		}

		if (result && nextStep != null) {
			return filterWithAllFilters(numberedline, levels,
					listKeyToFindOrRemove, paramsFilterBean, nextStep);
		}

		// log.config(numberedline.toString(20) + " : filter " + result);
		return result;

	}

	private final boolean filterLineCutLines(final NumberedLine numberedline,
			final ParamsFilterBean paramsFilterBean) {

		// pas de filtrage sur numero de ligne
		if (!paramsFilterBean.isCutLines()) {
			return true;
		}

		// filtrage sur numero de ligne : on élimine ce qui est en dehors de
		// l'intervale [fromLineNumber - toLineNumber]
		if ((paramsFilterBean.getFromLineNumber() >= 0 && numberedline
				.getNumber() < paramsFilterBean.getFromLineNumber())
				|| (paramsFilterBean.getToLineNumber() > 0 && numberedline
						.getNumber() > paramsFilterBean.getToLineNumber())) {
			return false;
		}

		return true;
	}

	/**
	 * filtrage sur les logs levels ou beginWithKey si une seule des conditions
	 * est rempli, on garde la ligne
	 * 
	 * @param numberedline
	 *            required
	 * @param levels
	 *            can be null
	 * @param listBeginWithKeys
	 *            can be null
	 * @return
	 */
	private final boolean filterLineBeginWith(final NumberedLine numberedline,
			final List<Level> levels, final List<KeyValueBean> listBeginWithKeys) {

		// aucune restriction on garde la ligne
		if (levels == null && listBeginWithKeys == null) {
			return true;
		}

		// ==== AU MOINS UNE RESTRICTION ===============

		// keep all line with Keys begin with
		if (listBeginWithKeys != null) {

			for (int i = 0; i < listBeginWithKeys.size(); i++) {

				final String keyValue = listBeginWithKeys.get(i).getKeyValue();
				if (numberedline._line.startsWith(keyValue)) {
					return true;
				}
			}
		}

		if (levels != null) {
			return this.lineStartWithLevels(numberedline, levels);
		}
		return false;

	}

	// retenir les lignes qui contiennent au moins une des clés si clés existent
	// sinon garder la ligne
	private final boolean filterLineWithKeyToFind(
			final NumberedLine numberedline,
			final ListKeyValueBean listKeyToFindOrRemove) {

		if (listKeyToFindOrRemove != null && !listKeyToFindOrRemove.isEmpty()) {

			final TypeKeyValue typeKeyValues = listKeyToFindOrRemove
					.getTypeKeyValues();
			final boolean result = this.lineContainKeysToFind(numberedline,
					listKeyToFindOrRemove);

			// si toutes les cles sont de type remove, on garde toutes les
			// lignes sauf celles marquées toRemove
			if (typeKeyValues == TypeKeyValue.remove && !result) {
				return true;
			}
			// si toutes les cles sont de type find, on garde les lignes
			// marquées toKeep
			else if (typeKeyValues == TypeKeyValue.find && result) {
				return true;
			}
			// si mélange de cle, on garde toutes les lignes marquées toKeep à
			// l'exclusion de celles marquées toRemove
			else if (typeKeyValues == TypeKeyValue.both && result) {
				if (numberedline.isToKeep() && !numberedline.isToRemove()) {
					return true;
				}
			}
			// on ne garde pas la ligne
			return false;

		} else {
			// aucune cle : on garde la ligne
			return true;
		}
	}

	private final void applyDislayParams(final NumberedLine numberedLine,
			final List<KeyValueBean> listKeyToFind,
			final ParamsDisplayBean paramsDisplayBean) {

		// log.config("applyDislayParams(): " + numberedLine.toString(20));

		// for each key to find
		for (int i = 0; i < listKeyToFind.size(); i++) {

			final KeyValueBean keyToFind = listKeyToFind.get(i);
			if (!this.lineContainKeyToFind(numberedLine, keyToFind))
				continue;

			final KeyToDisplayBlockBean keyToDisplayBlockBean = paramsDisplayBean
					.getKeyToDisplayBlockBean(keyToFind.getKeyName());

			if (keyToDisplayBlockBean == null)
				continue; // next KeyValueBean

			final DisplayMode displayMode = keyToDisplayBlockBean
					.getDisplayMode();
			// log.config("display mode: " + displayMode);

			switch (displayMode) {

			case ALL_LINE:
				// une ligne entière ne doit apparaitre qu'une fois!
				if (!numberedLine.isDisplayAllLine()) {
					numberedLine.setDisplayAllLine();
					this._listDisplayedLines.add(numberedLine);
				}

				break;
			case NEXT_JSON:
				final NumberedLine nextJson = this.extractNextJson(
						numberedLine, keyToFind, true);
				if (nextJson != null) {

					this._listDisplayedLines.add(nextJson);
				}
				break;
			case ATTRIBUTS_JSON:

				final NumberedLine nextJsonAttributes = this
						.extractJsonAttributes(numberedLine, keyToFind,
								keyToDisplayBlockBean);
				if (nextJsonAttributes != null) {
					this._listDisplayedLines.add(nextJsonAttributes);
				}
				break;
			}
		}
	}

	private final NumberedLine extractJsonAttributes(
			final NumberedLine numberedLine, final KeyValueBean keyValueBean,
			final KeyToDisplayBlockBean keyToDisplayBlockBean) {
		log.config("extractJsonAttributes()");
		try {

			final NumberedLine nextJson = this.extractNextJson(numberedLine,
					keyValueBean, false);
			if (nextJson == null)
				return null;

			final List<AttributToDisplayBean> listAttributes = keyToDisplayBlockBean
					.getListAttributToDisplay();
			final JSONValue jsonValue = JSONParser.parseStrict(nextJson._line);

			final JSONObject jsonObject = jsonValue.isObject();
			if (jsonObject == null)
				return null;

			final StringBuilder extractAttribute = new StringBuilder(
					this._buildDisplayKeyJson(keyValueBean));

			for (int j = 0; j < listAttributes.size(); j++) {
				final AttributToDisplayBean attributeBean = listAttributes
						.get(j);
				final String key = attributeBean.getAttributValue();
				final JSONValue result = this.extractRecursiveJsonKey(
						jsonObject, key);
				if (result != null) {
					extractAttribute.append(key);
					extractAttribute.append(": ");
					extractAttribute.append(result.toString());
					extractAttribute.append(", ");
				}
			}

			return new NumberedLine(numberedLine._number,
					extractAttribute.toString(), null, false);

		} catch (Exception e) {
			log.severe("error in extractJsonAttributes()" + e.getMessage());
		}

		return null;
	}

	/**
	 * Extraction recursive si la cle est une cle composée : key1.key2.key3
	 * 
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	private JSONValue extractRecursiveJsonKey(final JSONObject jsonObject,
			final String key) {

		log.config("extractJsonKey: key > " + key + " - jsonObject > "
				+ jsonObject.toString());
		final int pos = key.indexOf(".");
		// cle simple
		if (pos == -1) {
			return jsonObject.containsKey(key) ? jsonObject.get(key) : null;
		}

		// cle composée
		final String beginKey = key.substring(0, pos);
		final String suiteKey = key.substring(pos + 1);
		log.config("beginKey: " + beginKey + " - suiteKey: " + suiteKey);

		final JSONValue jsonValue = jsonObject.containsKey(beginKey) ? jsonObject
				.get(beginKey) : null;
		final JSONObject secondJsonObject = jsonValue.isObject();
		if (secondJsonObject == null) {
			return jsonValue;
		} else {
			// appel resursif
			return this.extractRecursiveJsonKey(secondJsonObject, suiteKey);
		}

	}

	/**
	 * on marque la ligne avec R (toRemove) ou K (toKeep)
	 * 
	 * @param numberedline
	 * @param listKeyToFindOrRemove
	 *            (only selected keys)
	 * @return true si un des deux marqueur est présent ou bien si aucune cle à
	 *         chercher
	 */
	private final boolean lineContainKeysToFind(
			final NumberedLine numberedline,
			final ListKeyValueBean listKeyToFindOrRemove) {

		final boolean firstOccurence = listKeyToFindOrRemove.getTypeKeyValues() != TypeKeyValue.both;
		/*
		 * si uniquement keyToFind : on cherche la premiere occurence si
		 * uniquement keyToRemove : on cherche la première occurence si melange
		 * keyToFind et keyToRemove on continue jusqu'à definir les deux
		 * attributs (toKeep and toRemove)
		 */
		for (int i = 0; i < listKeyToFindOrRemove.size(); i++) {
			KeyValueBean keyToFind = listKeyToFindOrRemove.get(i);

			if (this.lineContainKeyToFind(numberedline, keyToFind)) {

				if (keyToFind.isRemove()) {
					numberedline.setToRemove();
				} else {
					numberedline.setToKeep();
				}

				if (firstOccurence) {
					return true;
				}
				if (numberedline.isToRemove() && numberedline.isToKeep()) {
					return true;
				}
			}
		}
		return numberedline.isToRemove() || numberedline.isToKeep();
	}

	private final boolean lineContainKeyToFind(final NumberedLine numberedline,
			final KeyValueBean keyToFind) {

		final String keyValue = keyToFind.getKeyValue();
		// log.config("lineContainKeysToFind > keyValue: " + keyValue);

		if (keyToFind.isRegex()
				&& numberedline._line.matches(REGEX_ALL + keyValue + REGEX_ALL)) {
			// log.config("line matches keyValue! > keyValue: " + keyValue);
			return true;
		} else if (!keyToFind.isRegex()
				&& numberedline._line.indexOf(keyValue) != -1) {
			// log.config("line contains keyValue! > keyValue: " + keyValue);
			return true;
		}

		return false;
	}

	private final int getKeyPositionInLine(final String line,
			final KeyValueBean keyToFind) {

		if (!keyToFind.isSelected())
			return -1;
		if (keyToFind.isRegex()) {
			return 0; // pour les regex on prend la ligne complète
		} else {
			return line.indexOf(keyToFind.getKeyValue());
		}
	}

	// 3ème étape
	// constuire le log de sortie en fonction des criteres d'affichage
	// source: listLines
	// target: listFilteredLines
	// required if sourceLog or filter changes or display change
	private void stepBuildDisplayLog() {
		log.config("stepBuildDisplayLog()");

		// build displayed lines
		this._listDisplayedLines.clear();
		final ListKeyValueBean listKeyValueBean = this._paramsBean
				.getParamsFilterBean().getListSelectedKeyValues();
		if (!this._paramsBean.getParamsFilterBean().isKeyToFind()
				|| listKeyValueBean.isEmpty()
				|| listKeyValueBean.getTypeKeyValues() == TypeKeyValue.remove) {
			this._listDisplayedLines.addAll(this._listFilteredLines);
		} else {
			// final List<KeyValueBean> listKeyToFind =
			// this._paramsBean.getParamsFilterBean().getListKeyValues();
			final ParamsDisplayBean paramsDisplayBean = this._paramsBean
					.getParamsDisplayBean();
			for (int i = 0; i < this._listFilteredLines.size(); i++) {

				this.applyDislayParams(this._listFilteredLines.get(i),
						listKeyValueBean, paramsDisplayBean);

			}
		}

		this._logsBean.setNumeredLines(this._listLines);
		this._logsBean.setTargetLog(this._listDisplayedLines);
	}

	private final NumberedLine extractNextJson(final NumberedLine numberedLine,
			final KeyValueBean keyValueBean, final boolean addKey) {

		final String line = numberedLine._line;
		if (keyValueBean == null || line == null)
			return null;
		final StringBuilder sb = new StringBuilder();
		// (keyValueBean.getKeyValue() + ": "):

		final int pos = this.getKeyPositionInLine(line, keyValueBean);
		if (pos == -1)
			return null;

		int openingParenth = 0;
		int closingParenth = 0;
		final char[] charArray = line.substring(pos).toCharArray();

		for (int i = 0; i < charArray.length; i++) {
			final char c = charArray[i];

			if (c == '{') {

				openingParenth++;
				sb.append(c);

			} else if (c == '}' && openingParenth > 0) {

				closingParenth++;
				sb.append(c);
				if (openingParenth == closingParenth) {
					break;
				}

			} else if (openingParenth > 0) {
				sb.append(c);
			} else {
				continue;
			}
		}

		return (sb.toString().isEmpty()) ? null : new NumberedLine(
				numberedLine._number,
				((addKey) ? this._buildDisplayKeyJson(keyValueBean) : "")
						+ sb.toString(), null, false);
	}

	private final String _buildDisplayKeyJson(final KeyValueBean keyValueBean) {

		return " >> [KEY] " + keyValueBean.getKeyValue() + " - [JSON] ";
	}

	// =============================================== INNER CLASS
	public final static class ListNumberedLine extends ArrayList<NumberedLine> {

		private static final long serialVersionUID = 1L;

		List<String> getListString() {

			final List<String> listString = new ArrayList<String>(this.size());
			for (int i = 0; i < this.size(); i++) {
				listString.add(this.get(i).toString());
			}
			return listString;
		}
	}

	public final static class NumberedLine {
		private final int _number;
		private final Level _level;
		private final String _line;
		private final boolean _activeStyleName;
		private String _styleName;

		private boolean _toKeep = false;
		private boolean _toRemove = false;

		private boolean _displayAllLine = false;

		private boolean _pickByUser = false;

		public boolean isPickByUser() {
			return this._pickByUser;
		}

		public void setPickByUser(boolean pickByUser) {
			this._pickByUser = pickByUser;
		}

		public void setDisplayAllLine() {
			this._displayAllLine = true;
		}

		public boolean isDisplayAllLine() {
			return this._displayAllLine;
		}

		public void setToKeep() {
			this._toKeep = true;
		}

		public Boolean isToKeep() {
			return this._toKeep;
		}

		public void setToRemove() {
			this._toRemove = true;
		}

		public boolean isToRemove() {
			return this._toRemove;
		}

		public Level getLevel() {
			return this._level;
		}

		public int getNumber() {
			return this._number;
		}

		public String getStyleName() {
			if (!this._activeStyleName || this._level == null) {
				this._styleName = STYLE_LOG;
			}
			if (this._styleName == null) {
				this._styleName = this.getLevelStyleName(this._level);
			}
			if (this._pickByUser) {
				return this._styleName + " " + STYLE_IMG_PICK;
			}
			return this._styleName;
		}

		private String getLevelStyleName(final Level level) {
			switch (level) {
			case DEBUG:
				return STYLE_LOG_DEBUG;
			case INFO:
				return STYLE_LOG_INFO;
			case TRACE:
				return STYLE_LOG_TRACE;
			case ERROR:
				return STYLE_LOG_ERROR;
			case WARN:
				return STYLE_LOG_WARNING;
			}
			return STYLE_LOG;
		}

		private NumberedLine(String line, final Level level,
				final boolean activeStyleName) {
			this(-1, line, level, activeStyleName);
		}

		private NumberedLine(final int number, final String line,
				final Level level, final boolean activeStyleName) {
			this._number = number;
			this._line = line;
			this._level = level;
			this._activeStyleName = activeStyleName;

		}

		public String toString() {
			return ((this._number >= 0) ? this._number + " - " : "")
					+ this._line;
		}

		public String toString(int cut) {
			return ((this._number >= 0) ? this._number + " - " : "")
					+ this._line.substring(0, cut);
		}
	}
}
