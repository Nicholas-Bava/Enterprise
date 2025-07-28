package util;

import dto.PersonDTO;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormParser {

    /**
     * Parse URL-encoded form data into a key-value map
     */
    public static Map<String, String> parseFormData(String body) {
        Map<String, String> params = new HashMap<>();

        if (body == null || body.trim().isEmpty()) {
            return params;
        }

        String[] pairs = body.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);

            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (Exception e) {
                    params.put(keyValue[0], keyValue[1]);
                }
            } else if (keyValue.length == 1) {
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
     * name: Person's full name
     * email: Person's email address
     * age: Person's age as integer
     */
    public static PersonDTO createPersonDTOFromForm(Map<String, String> formData) {
        PersonDTO personDTO = new PersonDTO();

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
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email must be a valid email address");
        }
        personDTO.setEmail(email);

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

    public static PersonDTO createPersonDTOFromFormLenient(Map<String, String> formData) {
        PersonDTO personDTO = new PersonDTO();

        String name = formData.get("name");
        if (name != null && !name.trim().isEmpty()) {
            name = name.trim();
            if (name.length() <= 100) {
                personDTO.setName(name);
            }
        }

        String email = formData.get("email");
        if (email != null && !email.trim().isEmpty()) {
            email = email.trim();
            if (email.length() <= 100 && email.contains("@") && email.contains(".")) {
                personDTO.setEmail(email);
            }
        }

        String ageStr = formData.get("age");
        if (ageStr != null && !ageStr.trim().isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr.trim());
                if (age >= 1 && age <= 150) {
                    personDTO.setAge(age);
                }
            } catch (NumberFormatException e) {
            }
        }

        return personDTO;
    }

}
