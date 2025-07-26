package view;

import http.HttpResponse;
import model.Person;
import template.JSoupTemplateEngine;

import java.util.List;
import java.util.Map;

public interface View {

    HttpResponse render(Map<String, Object> model);
}
