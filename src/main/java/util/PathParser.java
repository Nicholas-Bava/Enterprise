package util;

public class PathParser {

    /**
     * Represents the parsed components of a URL path
     */
    public static class PathInfo {
        private String resource;    // e.g., "person"
        private String action;      // e.g., "index", "create", "delete", "update", "show"
        private Integer id;         // e.g., 123 (for specific person operations)

        public PathInfo(String resource, String action, Integer id) {
            this.resource = resource;
            this.action = action;
            this.id = id;
        }

        public String getResource() { return resource; }
        public String getAction() { return action; }
        public Integer getId() { return id; }

        @Override
        public String toString() {
            return String.format("PathInfo{resource='%s', action='%s', id=%s}",
                    resource, action, id);
        }
    }

    /**
     * URL Options:
     * - "/" or ""                  → PathInfo{resource="person", action="index", id=null}
     * - "/person"                  → PathInfo{resource="person", action="index", id=null}
     * - "/person/"                 → PathInfo{resource="person", action="index", id=null}
     * - "/person/create/"          → PathInfo{resource="person", action="create", id=null}
     * - "/person/delete/123"       → PathInfo{resource="person", action="delete", id=123}
     * - "/person/update/456"       → PathInfo{resource="person", action="update", id=456}
     * - "/person/789"              → PathInfo{resource="person", action="show", id=789}
     */
    public static PathInfo parsePath(String path) {
        // Handle null or empty path
        if (path == null || path.trim().isEmpty()) {
            return new PathInfo("person", "index", null);
        }

        // Remove leading and trailing slashes, then split
        String cleanPath = path.replaceAll("^/+|/+$", "");

        // Root path "/" becomes empty string after cleaning
        if (cleanPath.isEmpty()) {
            return new PathInfo("person", "index", null);
        }

        String[] segments = cleanPath.split("/");

        // Single segment cases
        if (segments.length == 1) {
            String segment = segments[0];

            if (segment.equals("person") || segment.isEmpty()) {
                // "/person" → show main page
                return new PathInfo("person", "index", null);
            } else {
                // Unknown resource, default to person index
                return new PathInfo("person", "index", null);
            }
        }

        // Two segment cases: /person/action or /person/id
        else if (segments.length == 2) {
            String resource = segments[0];
            String second = segments[1];

            // Check if second segment is an action
            switch (second) {
                case "create":
                    return new PathInfo(resource, "create", null);
                case "index":
                case "list":
                    return new PathInfo(resource, "index", null);
                default:
                    // Try to parse as ID for showing individual person
                    try {
                        int id = Integer.parseInt(second);
                        return new PathInfo(resource, "show", id);
                    } catch (NumberFormatException e) {
                        // Not a valid ID, treat as unknown action → default to index
                        return new PathInfo(resource, "index", null);
                    }
            }
        }

        // Three segment cases: /person/action/id
        else if (segments.length == 3) {
            String resource = segments[0];
            String action = segments[1];
            String idStr = segments[2];

            try {
                int id = Integer.parseInt(idStr);

                switch (action) {
                    case "delete":
                        return new PathInfo(resource, "delete", id);
                    case "update":
                        return new PathInfo(resource, "update", id);
                    case "edit":
                        return new PathInfo(resource, "edit", id);
                    case "show":
                    case "view":
                        return new PathInfo(resource, "show", id);
                    default:
                        // Unknown action with ID, default to show
                        return new PathInfo(resource, "show", id);
                }
            } catch (NumberFormatException e) {
                // Invalid ID format
                return new PathInfo(resource, "index", null);
            }
        }

        // More than 3 segments or other cases → default to index
        return new PathInfo("person", "index", null);
    }

    /**
     * Utility method to build URLs for redirects or links
     */
    public static class UrlBuilder {

        public static String personIndex() {
            return "/person";
        }

        public static String personCreate() {
            return "/person/create/";
        }

        public static String personShow(int id) {
            return "/person/" + id;
        }

        public static String personUpdate(int id) {
            return "/person/update/" + id;
        }

        public static String personDelete(int id) {
            return "/person/delete/" + id;
        }
    }
}
