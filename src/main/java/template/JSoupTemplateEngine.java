package template;

import model.Person;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;

/**
 * Engine to render html of the various pages. Edit Button and Delete Button are provided on the table next to the
 * records. The edit button will bring up a page to edit and save details. The delete button will delete the record.
 */
public class JSoupTemplateEngine {

    private String baseTemplate;

    public JSoupTemplateEngine() {
        this.baseTemplate = getBaseTemplate();
    }

    public String renderPersonListPage(List<Person> people, String errorMessage, String successMessage) {
        Document doc = Jsoup.parse(baseTemplate);

        // Set page title
        doc.title("Baylor Sports Updates Registration");
        doc.select("h1").first().text("Baylor Sports Updates Registration");

        // Handle error/success messages
        handleMessages(doc, errorMessage, successMessage);

        // Populate the people table
        populatePeopleTable(doc, people);

        return doc.outerHtml();
    }

    public String renderPersonDetailPage(Person person) {
        Document doc = Jsoup.parse(getPersonDetailTemplate());

        if (person != null) {
            doc.select("#person-name").first().text(person.getName());
            doc.select("#person-email").first().text(person.getEmail());
            doc.select("#person-age").first().text(String.valueOf(person.getAge()));
            doc.select("#person-id").first().text(String.valueOf(person.getId()));
        }

        return doc.outerHtml();
    }

    private void handleMessages(Document doc, String errorMessage, String successMessage) {
        Element messageContainer = doc.select("#message-container").first();

        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            Element errorDiv = messageContainer.appendElement("div")
                    .addClass("error-message")
                    .text(errorMessage);
        }

        if (successMessage != null && !successMessage.trim().isEmpty()) {
            Element successDiv = messageContainer.appendElement("div")
                    .addClass("success-message")
                    .text(successMessage);
        }
    }

    private void populatePeopleTable(Document doc, List<Person> people) {
        Element tbody = doc.select("#people-table tbody").first();
        tbody.empty();

        if (people == null || people.isEmpty()) {
            Element row = tbody.appendElement("tr");
            row.appendElement("td")
                    .attr("colspan", "6")
                    .addClass("no-data")
                    .text("No registrations yet");
            return;
        }

        for (Person person : people) {
            Element row = tbody.appendElement("tr");

            row.appendElement("td").text(String.valueOf(person.getId()));

            row.appendElement("td").text(person.getName());

            row.appendElement("td").text(person.getEmail());

            row.appendElement("td").text(String.valueOf(person.getAge()));

            Element actionsCell = row.appendElement("td");

            // Edit button
            Element editForm = actionsCell.appendElement("form")
                    .attr("method", "GET")
                    .attr("action", "/person/edit/" + person.getId())
                    .attr("style", "display:inline; margin-right: 5px;");

            editForm.appendElement("button")
                    .attr("type", "submit")
                    .addClass("edit-btn")
                    .text("Edit");

            // Delete button
            Element deleteForm = actionsCell.appendElement("form")
                    .attr("method", "POST")
                    .attr("action", "/person/delete/" + person.getId())
                    .attr("style", "display:inline;");

            deleteForm.appendElement("button")
                    .attr("type", "submit")
                    .addClass("delete-btn")
                    .attr("onclick", "return confirm('Are you sure you want to delete " +
                            person.getName().replace("'", "\\'") + "?')")
                    .text("Delete");
        }
    }

    // Create a form pre-populated with person's data for editing
    public String renderEditPersonPage(Person person) {
        Document doc = Jsoup.parse(getBaseTemplate());

        doc.title("Edit Person - Baylor Sports Registration");
        doc.select("h1").first().text("Edit Person");

        Element form = doc.select("form").first();
        form.attr("action", "/person/update/" + person.getId());
        form.attr("method", "POST");

        Element nameInput = doc.select("input[name=name]").first();
        if (nameInput != null) {
            nameInput.attr("value", person.getName());
        }

        Element emailInput = doc.select("input[name=email]").first();
        if (emailInput != null) {
            emailInput.attr("value", person.getEmail());
        }

        Element ageInput = doc.select("input[name=age]").first();
        if (ageInput != null) {
            ageInput.attr("value", String.valueOf(person.getAge()));
        }

        Element submitButton = doc.select("button[type=submit]").first();
        if (submitButton != null) {
            submitButton.text("Update Person");
        }

        Element formSection = doc.select(".form-section").first();
        if (formSection != null) {
            Element cancelButton = formSection.appendElement("a")
                    .attr("href", "/person")
                    .attr("style", "display: inline-block; margin-left: 10px; padding: 12px 24px; background: #6c757d; color: white; text-decoration: none; border-radius: 6px; font-weight: 600;")
                    .text("Cancel");
        }

        Element formHeading = doc.select(".form-section h2").first();
        if (formHeading != null) {
            formHeading.text("Edit Person: " + person.getName());
        }

        Element table = doc.select("table").first();
        if (table != null) {
            table.remove();
        }

        Elements h2Elements = doc.select("h2");
        for (Element h2 : h2Elements) {
            if ("Current Registrations".equals(h2.text())) {
                h2.remove();
                break;
            }
        }

        return doc.outerHtml();
    }

    private String getBaseTemplate() {
        return """
<!DOCTYPE html>
<html>
<head>
    <title>Baylor Sports Updates</title>
    <style>
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 0; 
            padding: 20px; 
            background-color: #f5f7fa; 
            color: #333;
        }
        .container { 
            max-width: 900px; 
            margin: 0 auto; 
            background: white; 
            padding: 30px; 
            border-radius: 10px; 
            box-shadow: 0 4px 20px rgba(0,0,0,0.1); 
        }
        h1 { 
            color: #1e4d2b; 
            border-bottom: 3px solid #ffd700; 
            padding-bottom: 15px; 
            margin-bottom: 30px;
            text-align: center;
        }
        h2 { 
            color: #2d5a3d; 
            margin-top: 30px; 
            margin-bottom: 20px; 
        }
        .form-section { 
            background: linear-gradient(135deg, #f8f9fa, #e9ecef); 
            padding: 25px; 
            border-radius: 8px; 
            margin-bottom: 30px; 
            border: 1px solid #dee2e6;
        }
        .form-group { 
            margin-bottom: 18px; 
        }
        label { 
            display: block; 
            margin-bottom: 6px; 
            font-weight: 600; 
            color: #495057; 
        }
        input[type="text"], input[type="email"], input[type="number"] { 
            width: 100%; 
            padding: 12px; 
            border: 2px solid #ced4da; 
            border-radius: 6px; 
            font-size: 14px; 
            transition: border-color 0.3s ease;
            box-sizing: border-box;
        }
        input[type="text"]:focus, input[type="email"]:focus, input[type="number"]:focus {
            outline: none;
            border-color: #1e4d2b;
            box-shadow: 0 0 0 3px rgba(30, 77, 43, 0.1);
        }
        .submit-btn { 
            background: linear-gradient(135deg, #1e4d2b, #2d6b3f); 
            color: white; 
            padding: 12px 24px; 
            border: none; 
            border-radius: 6px; 
            cursor: pointer; 
            font-size: 16px; 
            font-weight: 600;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .submit-btn:hover { 
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(30, 77, 43, 0.3);
        }
        table { 
            width: 100%; 
            border-collapse: collapse; 
            margin-top: 20px; 
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        th, td { 
            padding: 15px 12px; 
            text-align: left; 
            border-bottom: 1px solid #dee2e6; 
        }
        th { 
            background: linear-gradient(135deg, #1e4d2b, #2d5a3d); 
            color: white; 
            font-weight: 600;
            text-transform: uppercase;
            font-size: 12px;
            letter-spacing: 1px;
        }
        tr:hover { 
            background-color: #f8f9fa; 
        }
        .delete-btn { 
            background: linear-gradient(135deg, #dc3545, #c82333); 
            color: white;
            padding: 6px 12px; 
            border: none;
            border-radius: 4px;
            font-size: 12px; 
            cursor: pointer;
            font-weight: 500;
            transition: transform 0.2s ease;
        }
        .delete-btn:hover { 
            transform: scale(1.05);
        }
        .edit-btn { 
            background: linear-gradient(135deg, #007bff, #0056b3); 
            color: white;
            padding: 6px 12px; 
            border: none;
            border-radius: 4px;
            font-size: 12px; 
            cursor: pointer;
            font-weight: 500;
            transition: transform 0.2s ease;
        }
        .edit-btn:hover { 
            transform: scale(1.05);
        }
        .no-data {
            text-align: center;
            color: #6c757d;
            font-style: italic;
            padding: 30px;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 12px 15px;
            border: 1px solid #f5c6cb;
            border-radius: 6px;
            margin-bottom: 20px;
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 12px 15px;
            border: 1px solid #c3e6cb;
            border-radius: 6px;
            margin-bottom: 20px;
        }
        .statistics {
            background: #e3f2fd;
            padding: 15px;
            border-radius: 6px;
            margin-bottom: 20px;
            border-left: 4px solid #2196f3;
        }
        .statistics h3 {
            margin-top: 0;
            color: #1976d2;
        }
        .statistics p {
            margin: 5px 0;
            font-weight: 500;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Baylor Sports Updates Registration</h1>
        
        <div id="message-container"></div>
        
        <div class="form-section">
            <h2>Register for Baylor Sports Updates</h2>
            <form method="POST" action="/">
                <div class="form-group">
                    <label for="name">Full Name:</label>
                    <input type="text" id="name" name="name" required maxlength="100">
                </div>
                
                <div class="form-group">
                    <label for="email">Email Address:</label>
                    <input type="email" id="email" name="email" required maxlength="100">
                </div>
                
                <div class="form-group">
                    <label for="age">Age:</label>
                    <input type="number" id="age" name="age" min="1" max="150" required>
                </div>
                
                <button type="submit" class="submit-btn">Register Now</button>
            </form>
        </div>
        
        <h2>Current Registrations</h2>
        <table id="people-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Age</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                
            </tbody>
        </table>
    </div>
</body>
</html>
        """;
    }

    private String getPersonDetailTemplate() {
        return """
<!DOCTYPE html>
<html>
<head>
    <title>Person Details</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .person-card { 
            max-width: 500px; 
            margin: 0 auto; 
            padding: 30px; 
            border: 1px solid #ddd; 
            border-radius: 8px; 
            background: #f9f9f9; 
        }
        .person-detail { margin-bottom: 15px; }
        .label { font-weight: bold; color: #333; }
        .back-link { display: inline-block; margin-top: 20px; color: #1e4d2b; text-decoration: none; }
        .back-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="person-card">
        <h1>Person Details</h1>
        <div class="person-detail">
            <span class="label">ID:</span> <span id="person-id"></span>
        </div>
        <div class="person-detail">
            <span class="label">Name:</span> <span id="person-name"></span>
        </div>
        <div class="person-detail">
            <span class="label">Email:</span> <span id="person-email"></span>
        </div>
        <div class="person-detail">
            <span class="label">Age:</span> <span id="person-age"></span>
        </div>
        <a href="/" class="back-link">← Back to Registration List</a>
    </div>
</body>
</html>
        """;
    }
}
