package fr.tsadeo.app.gwt.reportlog.client.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;

public class LocalStorageManager {
	
	public static final String KEY_PARAMS = "json.params";
	
	private static final LocalStorageManager impl = GWT.create(LocalStorageManager.class);
	public static final LocalStorageManager get() {
		return impl;
	}
	
	private Boolean localStorageSupported = null;
	private Storage storage;
	
	private Map<String, String> mapIfNotLocalStorageSupported;
		
	//--------------------------------------------- constructeur
	private LocalStorageManager() {
		this.initStorage();
	}

    public boolean isLocalStorageSupported() {
    	return this.localStorageSupported;
    }
    /**
     * Paramètres sauvegardés sous forme d'un objet json stringifié
     */
	public void storeParams(final String jsonParams) {
		this.storeKeyValue(KEY_PARAMS, jsonParams);
	}

	public String getParams() {
		return this.getValueFromKey(KEY_PARAMS);
	}

	//---------------------------------------------- private methods
	private void initStorage()  {

		this.storage =  Storage.getLocalStorageIfSupported();
		if (storage == null) {
			this.localStorageSupported = false;
			this.mapIfNotLocalStorageSupported = new HashMap<String, String>();
		} else {
			this.localStorageSupported = true;
		}

}
	private void storeKeyValue(final String key, final String value) {
		if (key == null || value == null) return;
		if (this.localStorageSupported) {
			this.storage.setItem(key,value);
		}
		else {
			this.mapIfNotLocalStorageSupported.put(key, value);
		}
	}
	private String getValueFromKey(final String key) {
		if (key == null) return null;

		if (this.localStorageSupported) {
			return this.storage.getItem(key);
		}
		else {
			return this.mapIfNotLocalStorageSupported.get(key);
		}
	}

}
