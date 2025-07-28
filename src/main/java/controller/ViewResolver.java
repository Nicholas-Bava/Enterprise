package controller;

import view.PersonView;
import view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * ViewResolver maps view names to actual View implementations.
 * This allows controllers to return simple string names like "personList"
 * and have them resolved to actual view objects.
 */

public class ViewResolver {
    // Map of view names and views
    private Map<String, View> views;

    public ViewResolver() {
        this.views = new HashMap<>();
        registerDefaultViews();
    }

    /**
     * Register views
     */
    private void registerDefaultViews() {
        PersonView personView = new PersonView();

        views.put("personList", personView);
        views.put("personDetail", personView);
        views.put("personEdit", personView);
        views.put("error", personView);

    }

    /**
     * Resolve a logical view name to an actual View object
     */
    public View resolveView(String viewName) {
        if (viewName.startsWith("redirect:")) {
            return new RedirectView(viewName.substring(9));
        }

        // Look up registered views
        View view = views.get(viewName);
        if (view != null) {
            return view;
        }

        return views.get("personList");
    }

    public void registerView(String viewName, View view) {
        views.put(viewName, view);
    }
}
