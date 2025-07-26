package controller;

import view.PersonView;
import view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * ViewResolver maps logical view names to actual View implementations.
 * This allows controllers to return simple string names like "personList"
 * and have them resolved to actual view objects.
 */

public class ViewResolver {
    private Map<String, View> views;

    public ViewResolver() {
        this.views = new HashMap<>();
        registerDefaultViews();
    }

    /**
     * Register all available views
     */
    private void registerDefaultViews() {
        PersonView personView = new PersonView();

        // Register person-related views
        views.put("personList", personView);
        views.put("personDetail", personView);
        views.put("personEdit", personView);
        views.put("error", personView); // PersonView can handle error pages too

        // Future: register other views
        // TeamView teamView = new TeamView();
        // views.put("teamList", teamView);
        // views.put("teamDetail", teamView);
    }

    /**
     * Resolve a logical view name to an actual View object
     */
    public View resolveView(String viewName) {
        // Handle special view names
        if (viewName.startsWith("redirect:")) {
            return new RedirectView(viewName.substring(9));
        }

        // Look up registered views
        View view = views.get(viewName);
        if (view != null) {
            return view;
        }

        // Default fallback
        return views.get("personList");
    }

    /**
     * Register a new view
     */
    public void registerView(String viewName, View view) {
        views.put(viewName, view);
    }
}
