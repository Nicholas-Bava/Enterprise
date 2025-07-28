package view;

import http.HttpResponse;

import java.util.Map;

public interface View {

    HttpResponse render(Map<String, Object> model);
}
