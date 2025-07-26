package controller;

import http.HttpRequest;
import http.HttpResponse;
import util.PathParser;
import view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * DispatcherServlet acts as the front controller - the single entry point for all HTTP requests.
 * It routes requests to appropriate controllers and coordinates the MVC flow.
 * This is equivalent to Spring's DispatcherServlet.
 */
public class DispatcherServlet {

    private Map<String, Object> controllers;
    private ViewResolver viewResolver;

    public DispatcherServlet() {
        this.controllers = new HashMap<>();
        this.viewResolver = new ViewResolver();
    }

    /**
     * Register controllers with the dispatcher
     */
    public void registerController(String path, Object controller) {
        controllers.put(path, controller);
    }

    /**
     * Main entry point - handles all HTTP requests
     */
    public HttpResponse handleRequest(HttpRequest request) {
        try {
            // 1. Parse the request path to determine which controller to use
            PathParser.PathInfo pathInfo = PathParser.parsePath(request.getPath());
            String resource = pathInfo.getResource();

            // 2. Route to appropriate controller
            Object controller = controllers.get(resource);
            if (controller == null) {
                return createErrorResponse(404, "Controller not found for: " + resource);
            }

            // 3. Call controller to handle request
            ModelAndView modelAndView = callController(controller, request);

            // 4. Resolve view and render response
            return renderResponse(modelAndView);

        } catch (Exception e) {
            System.err.println("Error in DispatcherServlet: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Call the appropriate controller method
     */
    private ModelAndView callController(Object controller, HttpRequest request) {
        if (controller instanceof PersonController) {
            return ((PersonController) controller).handleRequest(request);
        }
        // Future: add other controller types here
        throw new IllegalArgumentException("Unknown controller type: " + controller.getClass());
    }

    /**
     * Resolve view and render the response
     */
    private HttpResponse renderResponse(ModelAndView modelAndView) {
        // 1. Resolve logical view name to actual View object
        View view = viewResolver.resolveView(modelAndView.getViewName());

        // 2. Have the view render the response
        return view.render(modelAndView.getModel());
    }

    private HttpResponse createErrorResponse(int statusCode, String message) {
        HttpResponse response = new HttpResponse(statusCode, getStatusMessage(statusCode));
        response.setBody(createErrorPageHtml(message));
        return response;
    }

    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            default: return "Error";
        }
    }

    private String createErrorPageHtml(String message) {
        return "<!DOCTYPE html><html><head><title>Error</title></head>" +
                "<body><h1>Error</h1><p>" + message + "</p></body></html>";
    }
}
