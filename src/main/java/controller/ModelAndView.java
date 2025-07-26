package controller;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelAndView contains both the data (model) and the logical view name.
 * This is what controllers return to indicate what data to display and which view to use.
 */

public class ModelAndView {

    private Map<String, Object> model;
    private String viewName;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
        this.model = new HashMap<>();
    }

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model != null ? model : new HashMap<>();
    }

    // Add data to the model
    public ModelAndView addObject(String key, Object value) {
        model.put(key, value);
        return this;
    }

    public String getViewName() { return viewName; }
    public Map<String, Object> getModel() { return model; }

    public static ModelAndView redirect(String url) {
        return new ModelAndView("redirect:" + url);
    }

    public static ModelAndView error(String errorMessage) {
        return new ModelAndView("error").addObject("errorMessage", errorMessage);
    }
}
