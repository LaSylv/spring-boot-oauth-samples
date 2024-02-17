package com.github.lasylv.oauth2.strava;

import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@org.springframework.stereotype.Controller
public class Controller {

    private final StravaApiService stravaApiService;

    public Controller(StravaApiService stravaApiService) {
        this.stravaApiService = stravaApiService;
    }

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String overview(Model model) {
        return "index";
    }

    @GetMapping(path = "/runs", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView runs(Model model) {
        return new ModelAndView("runs", Map.of("runs", stravaApiService.getActivitiesFromStrava()));
    }

}