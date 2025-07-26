package controller;

import dto.PersonDTO;
import http.HttpRequest;
import model.Person;
import service.PersonService;
import util.FormParser;
import util.PathParser;

import java.util.List;
import java.util.Map;

/**
 * PersonController handles Person-specific business logic.
 * Instead of returning HttpResponse directly, it returns ModelAndView objects
 * that contain the data and indicate which view should render the response.
 */

public class PersonController {

    private PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Handle HTTP requests and return ModelAndView for rendering
     */
    public ModelAndView handleRequest(HttpRequest request) {
        try {
            PathParser.PathInfo pathInfo = PathParser.parsePath(request.getPath());
            String method = request.getMethod().toUpperCase();

            // Debug logging
            System.out.println("🔍 PersonController handling: " + method + " " + request.getPath());
            System.out.println("🔍 Parsed as: resource=" + pathInfo.getResource() +
                    ", action=" + pathInfo.getAction() + ", id=" + pathInfo.getId());

            // Route to appropriate handler
            switch (method) {
                case "GET":
                    return handleGet(request, pathInfo);
                case "POST":
                    return handlePost(request, pathInfo);
                case "PUT":
                    return handlePut(request, pathInfo);
                case "DELETE":
                    return handleDelete(request, pathInfo);
                default:
                    return ModelAndView.error("Method not allowed: " + method);
            }

        } catch (Exception e) {
            System.err.println("❌ Error in PersonController: " + e.getMessage());
            e.printStackTrace();
            return ModelAndView.error("Internal Server Error: " + e.getMessage());
        }
    }

    private ModelAndView handleGet(HttpRequest request, PathParser.PathInfo pathInfo) {
        switch (pathInfo.getAction()) {
            case "index":
                return showAllPeople();
            case "show":
                return showPerson(pathInfo.getId());
            case "edit":
                return showEditForm(pathInfo.getId());
            default:
                return showAllPeople();
        }
    }

    private ModelAndView handlePost(HttpRequest request, PathParser.PathInfo pathInfo) {
        switch (pathInfo.getAction()) {
            case "index":        // Handle POST to main page (form submission)
            case "create":       // Handle POST to /person/create/
                return createPerson(request);
            case "update":       // Handle POST to /person/update/id (from edit form)
                return updatePerson(request, pathInfo.getId());
            case "delete":
                return deletePerson(pathInfo.getId());
            default:
                return ModelAndView.error("Unknown action: " + pathInfo.getAction());
        }
    }

    private ModelAndView handlePut(HttpRequest request, PathParser.PathInfo pathInfo) {
        if ("update".equals(pathInfo.getAction()) && pathInfo.getId() != null) {
            return updatePerson(request, pathInfo.getId());
        }
        return ModelAndView.error("Invalid update request");
    }

    private ModelAndView handleDelete(HttpRequest request, PathParser.PathInfo pathInfo) {
        if ("delete".equals(pathInfo.getAction()) && pathInfo.getId() != null) {
            return deletePerson(pathInfo.getId());
        }
        return ModelAndView.error("Invalid delete request");
    }

    // ==================== BUSINESS OPERATIONS ====================

    private ModelAndView showAllPeople() {
        try {
            List<Person> people = personService.findAllPersons();

            return new ModelAndView("personList")
                    .addObject("people", people)
                    .addObject("title", "Baylor Sports Updates Registration")
                    .addObject("totalPeople", people.size());

        } catch (Exception e) {
            return ModelAndView.error("Failed to load people: " + e.getMessage());
        }
    }

    private ModelAndView showPerson(Integer id) {
        if (id == null) {
            return ModelAndView.error("Person ID required");
        }

        try {
            Person person = personService.findPersonById(id);
            if (person == null) {
                return ModelAndView.error("Person not found");
            }

            return new ModelAndView("personDetail")
                    .addObject("person", person)
                    .addObject("title", "Person Details: " + person.getName());

        } catch (Exception e) {
            return ModelAndView.error("Failed to load person: " + e.getMessage());
        }
    }

    private ModelAndView showEditForm(Integer id) {
        if (id == null) {
            return ModelAndView.error("Person ID required");
        }

        try {
            Person person = personService.findPersonById(id);
            if (person == null) {
                return ModelAndView.error("Person not found");
            }

            return new ModelAndView("personEdit")
                    .addObject("person", person)
                    .addObject("title", "Edit Person: " + person.getName())
                    .addObject("isEdit", true);

        } catch (Exception e) {
            return ModelAndView.error("Failed to load person for editing: " + e.getMessage());
        }
    }

    private ModelAndView createPerson(HttpRequest request) {
        try {
            Map<String, String> formData = FormParser.parseFormData(request.getBody());
            PersonDTO personDTO = FormParser.createPersonDTOFromForm(formData);

            Person createdPerson = personService.createPerson(personDTO);

            // Redirect to main page after successful creation
            return ModelAndView.redirect("/person");

        } catch (IllegalArgumentException e) {
            // Show form with error message
            List<Person> people = personService.findAllPersons();
            return new ModelAndView("personList")
                    .addObject("people", people)
                    .addObject("errorMessage", "Validation Error: " + e.getMessage())
                    .addObject("title", "Baylor Sports Updates Registration");
        } catch (Exception e) {
            return ModelAndView.error("Failed to create person: " + e.getMessage());
        }
    }

    private ModelAndView updatePerson(HttpRequest request, Integer id) {
        try {
            Map<String, String> formData = FormParser.parseFormData(request.getBody());
            PersonDTO personDTO = FormParser.createPersonDTOFromForm(formData);

            Person updatedPerson = personService.updatePerson(id, personDTO);

            return ModelAndView.redirect("/person");

        } catch (IllegalArgumentException e) {
            return ModelAndView.error("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            return ModelAndView.error("Failed to update person: " + e.getMessage());
        }
    }

    private ModelAndView deletePerson(Integer id) {
        if (id == null) {
            return ModelAndView.error("Person ID required");
        }

        try {
            boolean deleted = personService.deletePerson(id);

            if (deleted) {
                return ModelAndView.redirect("/person");
            } else {
                return ModelAndView.error("Person not found or could not be deleted");
            }

        } catch (Exception e) {
            return ModelAndView.error("Failed to delete person: " + e.getMessage());
        }
    }
}
