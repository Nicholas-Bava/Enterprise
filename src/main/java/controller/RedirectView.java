package controller;

import http.HttpResponse;

import java.util.Map;

public class RedirectView implements view.View{

    private String redirectUrl;

    public RedirectView(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public HttpResponse render(Map<String, Object> model) {
        HttpResponse response = new HttpResponse(302, "Found");
        response.addHeader("Location", redirectUrl);
        return response;
    }
}
