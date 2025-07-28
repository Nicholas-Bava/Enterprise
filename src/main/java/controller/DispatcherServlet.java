package controller;

import http.HttpRequest;
import http.HttpResponse;
import util.PathParser;
import view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * DispatcherServlet is the front controller to receive Http Requests and route them to the proper controller
 * (in this case, this is always the Person Controller).
 */
public class DispatcherServlet {

    private Map<String, Object> controllers;
    private ViewResolver viewResolver;

    public DispatcherServlet() {
        this.controllers = new HashMap<>();
        this.viewResolver = new ViewResolver();
    }

    /**
     * Register controller with the dispatcher
     */
    public void registerController(String path, Object controller) {
        controllers.put(path, controller);
    }

    /**
     * Handles HTTP requests
     */
    public HttpResponse handleRequest(HttpRequest request) {
        try {
            PathParser.PathInfo pathInfo = PathParser.parsePath(request.getPath());
            String resource = pathInfo.getResource();

            Object controller = controllers.get(resource);
            if (controller == null) {
                return createErrorResponse(404, "Controller not found for: " + resource);
            }

            ModelAndView modelAndView = callController(controller, request);

            return renderResponse(modelAndView);

        } catch (Exception e) {
            System.err.println("Error in DispatcherServlet: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Call the appropriate controller method (only PersonController in this case)
     */
    private ModelAndView callController(Object controller, HttpRequest request) {
        if (controller instanceof PersonController) {
            return ((PersonController) controller).handleRequest(request);
        }
        throw new IllegalArgumentException("Unknown controller type: " + controller.getClass());
    }

    /**
     * Resolve view and render the response
     */
    private HttpResponse renderResponse(ModelAndView modelAndView) {
        View view = viewResolver.resolveView(modelAndView.getViewName());

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
