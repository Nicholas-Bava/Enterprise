package view;

import http.HttpResponse;
import model.Person;
import template.JSoupTemplateEngine;

import java.util.List;
import java.util.Map;

public class PersonView implements View{

    private JSoupTemplateEngine templateEngine;

    public PersonView() {
        this.templateEngine = new JSoupTemplateEngine();
    }

    /**
     * Main render method - determines which specific rendering method to use
     * based on the model contents
     */
    @Override
    public HttpResponse render(Map<String, Object> model) {
        if (model.containsKey("isEdit") && Boolean.TRUE.equals(model.get("isEdit"))) {
            return renderPersonEdit(model);
        }
        if (model.containsKey("person") && !model.containsKey("people")) {
            // Single person detail page
            return renderPersonDetail(model);
        } else if (model.containsKey("people")) {
            // Main registration page
            return renderPersonList(model);
        } else if (model.containsKey("errorMessage")) {
            // Error page
            return renderError(model);
        } else {
            // Default to person list
            return renderPersonList(model);
        }
    }

    /**
     * Render the main person list page with registration form
     */
    private HttpResponse renderPersonList(Map<String, Object> model) {
        try {
            @SuppressWarnings("unchecked")
            List<Person> people = (List<Person>) model.get("people");
            String title = (String) model.getOrDefault("title", "Baylor Sports Updates Registration");
            String errorMessage = (String) model.get("errorMessage");
            String successMessage = (String) model.get("successMessage");

            // JSoup template engine to render
            String html = templateEngine.renderPersonListPage(
                    people != null ? people : List.of(),
                    errorMessage,
                    successMessage
            );

            // Create HTTP response
            HttpResponse response = new HttpResponse(200, "OK");
            response.setBody(html);
            return response;

        } catch (Exception e) {
            System.err.println("Error rendering person list: " + e.getMessage());
            return renderInternalError("Failed to render person list: " + e.getMessage());
        }
    }

    private HttpResponse renderPersonDetail(Map<String, Object> model) {
        try {
            Person person = (Person) model.get("person");
            String title = (String) model.getOrDefault("title", "Person Details");

            if (person == null) {
                return renderNotFound("Person not found");
            }

            String html = templateEngine.renderPersonDetailPage(person);

            HttpResponse response = new HttpResponse(200, "OK");
            response.setBody(html);
            return response;

        } catch (Exception e) {
            System.err.println("Error rendering person detail: " + e.getMessage());
            return renderInternalError("Failed to render person details: " + e.getMessage());
        }
    }

    /**
     * Render person edit form
     */
    public HttpResponse renderPersonEdit(Map<String, Object> model) {
        try {
            Person person = (Person) model.get("person");
            String title = (String) model.getOrDefault("title", "Edit Person");

            if (person == null) {
                return renderNotFound("Person not found");
            }

            String html = templateEngine.renderEditPersonPage(person);

            HttpResponse response = new HttpResponse(200, "OK");
            response.setBody(html);
            return response;

        } catch (Exception e) {
            System.err.println("Error rendering person edit form: " + e.getMessage());
            return renderInternalError("Failed to render edit form: " + e.getMessage());
        }
    }

    /**
     * Render error page
     */
    private HttpResponse renderError(Map<String, Object> model) {
        String errorMessage = (String) model.get("errorMessage");
        String title = (String) model.getOrDefault("title", "Error");

        return renderInternalError(errorMessage != null ? errorMessage : "An error occurred");
    }

    private HttpResponse renderNotFound(String message) {
        String html = createErrorPageHtml("Not Found", message != null ? message : "Page not found");

        HttpResponse response = new HttpResponse(404, "Not Found");
        response.setBody(html);
        return response;
    }

    private HttpResponse renderInternalError(String message) {
        String html = createErrorPageHtml("Internal Server Error", message != null ? message : "An internal error occurred");

        HttpResponse response = new HttpResponse(500, "Internal Server Error");
        response.setBody(html);
        return response;
    }

    private String createErrorPageHtml(String title, String message) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>%s - Baylor Sports Registration</title>
            <style>
                body { 
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                    margin: 0; 
                    padding: 40px; 
                    background: linear-gradient(135deg, #1e4d2b, #2d6b3f); 
                    color: white;
                    text-align: center;
                }
                .error-container { 
                    max-width: 600px; 
                    margin: 0 auto; 
                    background: rgba(255, 255, 255, 0.1); 
                    padding: 40px; 
                    border-radius: 10px; 
                    box-shadow: 0 4px 20px rgba(0,0,0,0.3);
                }
                h1 { 
                    color: #ffd700; 
                    font-size: 3em; 
                    margin-bottom: 20px; 
                }
                p { 
                    font-size: 1.2em; 
                    margin-bottom: 30px; 
                    line-height: 1.6;
                }
                .home-link { 
                    display: inline-block; 
                    background: #ffd700; 
                    color: #1e4d2b; 
                    padding: 12px 24px; 
                    text-decoration: none; 
                    border-radius: 6px; 
                    font-weight: bold;
                    transition: transform 0.2s ease;
                }
                .home-link:hover { 
                    transform: translateY(-2px); 
                }
            </style>
        </head>
        <body>
            <div class="error-container">
                <h1>%s</h1>
                <p>%s</p>
                <a href="/person" class="home-link">Return to Registration</a>
            </div>
        </body>
        </html>
        """.formatted(title, title, escapeHtml(message));
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
