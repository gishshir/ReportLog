package fr.tsadeo.app.gwt.reportlog.client.params.filter.viewkeyvalue;

/**
 * Listener à l'écoute des modifications des view par l'utilisateur
 * @author sylvie
 *
 */
public interface IViewListener {

	// ajout d'une vue par l'utilisateur ou par restoration json
	public void addView(final int viewId, final String viewName, final Boolean active);
	
	// suppression de la vue par l'utilisateur
	public void removeView(final int viewId);
	
	// changement des valeurs par l'utilisateur
	public void changeViewValues (int viewId, final String viewName, 
			final Boolean activeView);
	
	// selection de la vue par l'utilisateur
	// affiche ou masque le détail d'une vue
	public void selectView(Integer viewId, final Boolean showDetail);
	
	// indique qu'une vue est en train d'être editer
	public void editingView(int viewId, boolean editing);
	
    // deplacement par drag and drop d'une keyValue d'une vue à une autre
	public void dragAndDropItem(String keyName, int fromViewId, int toViewId);
}
