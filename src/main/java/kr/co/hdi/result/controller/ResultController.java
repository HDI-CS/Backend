package kr.co.hdi.result.controller;

import kr.co.hdi.result.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @GetMapping("/survey/result")
    public void getSurveyResult(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "path") String path) {

        resultService.getResponseResult(type, path);
    }
}
