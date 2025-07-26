package util;

import dto.PersonDTO;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormParser {

    /**
     * Parse URL-encoded form data into a key-value map
     *
     * Input example: "name=John+Doe&email=john%40example.com&age=25"
     * Output: {"name": "John Doe", "email": "john@example.com", "age": "25"}
     *
     * @param body The raw form data string from HTTP request body
     * @return Map of field names to values
     */
    public static Map<String, String> parseFormData(String body) {
        Map<String, String> params = new HashMap<>();

        // Handle null or empty body
        if (body == null || body.trim().isEmpty()) {
            return params;
        }

        // Split by & to get individual field=value pairs
        String[] pairs = body.split("&");

        for (String pair : pairs) {
            // Split each pair by = (limit to 2 parts in case value contains =)
            String[] keyValue = pair.split("=", 2);

            if (keyValue.length == 2) {
                try {
                    // URL decode both key and value
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (Exception e) {
                    // If URL decoding fails, use raw values
                    params.put(keyValue[0], keyValue[1]);
                }
            } else if (keyValue.length == 1) {
                // Handle case where field has no value (e.g., "field1=&field2=value")
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    params.put(key, "");
                } catch (Exception e) {
                    params.put(keyValue[0], "");
                }
            }
        }

        return params;
    }

    /**
     * Convert form data map into a PersonDTO object
     *
     * Expected form fields:
     * - name: Person's full name (required)
     * - email: Person's email address (required)
     * - age: Person's age as integer (required)
     *
     * @param formData Map of form field names to values
     * @return PersonDTO object populated with form data
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public static PersonDTO createPersonDTOFromForm(Map<String, String> formData) {
        PersonDTO personDTO = new PersonDTO();

        // Parse and validate name
        String name = formData.get("name");
        if (name == null) {
            throw new IllegalArgumentException("Name field is required");
        }
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name cannot be longer than 100 characters");
        }
        personDTO.setName(name);

        // Parse and validate email
        String email = formData.get("email");
        if (email == null) {
            throw new IllegalArgumentException("Email field is required");
        }
        email = email.trim();
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email cannot be longer than 100 characters");
        }
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email must be a valid email address");
        }
        personDTO.setEmail(email);

        // Parse and validate age
        String ageStr = formData.get("age");
        if (ageStr == null) {
            throw new IllegalArgumentException("Age field is required");
        }
        ageStr = ageStr.trim();
        if (ageStr.isEmpty()) {
            throw new IllegalArgumentException("Age cannot be empty");
        }

        try {
            int age = Integer.parseInt(ageStr);
            if (age < 1) {
                throw new IllegalArgumentException("Age must be at least 1");
            }
            if (age > 150) {
                throw new IllegalArgumentException("Age cannot be greater than 150");
            }
            personDTO.setAge(age);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be a valid number");
        }

        return personDTO;
    }

    /**
     * Create PersonDTO from form data with partial validation (for updates)
     *
     * This method is more lenient and can handle cases where some fields might be missing
     * (useful for PATCH-style updates where only changed fields are sent)
     *
     * @param formData Map of form field names to values
     * @return PersonDTO object with available fields populated
     */
    public static PersonDTO createPersonDTOFromFormLenient(Map<String, String> formData) {
        PersonDTO personDTO = new PersonDTO();

        // Name (optional for updates)
        String name = formData.get("name");
        if (name != null && !name.trim().isEmpty()) {
            name = name.trim();
            if (name.length() <= 100) {
                personDTO.setName(name);
            }
        }

        // Email (optional for updates)
        String email = formData.get("email");
        if (email != null && !email.trim().isEmpty()) {
            email = email.trim();
            if (email.length() <= 100 && email.contains("@") && email.contains(".")) {
                personDTO.setEmail(email);
            }
        }

        // Age (optional for updates)
        String ageStr = formData.get("age");
        if (ageStr != null && !ageStr.trim().isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr.trim());
                if (age >= 1 && age <= 150) {
                    personDTO.setAge(age);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid age for lenient parsing
            }
        }

        return personDTO;
    }

    /**
     * Utility method to check if form data contains all required fields
     *
     * @param formData Map of form field names to values
     * @return true if all required fields are present and non-empty
     */
    public static boolean hasRequiredFields(Map<String, String> formData) {
        String name = formData.get("name");
        String email = formData.get("email");
        String age = formData.get("age");

        return name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                age != null && !age.trim().isEmpty();
    }

    /**
     * Utility method to get a trimmed string value from form data
     *
     * @param formData Map of form field names to values
     * @param fieldName Name of the field to retrieve
     * @param defaultValue Default value if field is missing or empty
     * @return Trimmed field value or default value
     */
    public static String getFormField(Map<String, String> formData, String fieldName, String defaultValue) {
        String value = formData.get(fieldName);
        if (value == null) {
            return defaultValue;
        }
        value = value.trim();
        return value.isEmpty() ? defaultValue : value;
    }
}
