package kr.co.hdi.dataset.controller;

import kr.co.hdi.dataset.service.DatasetAssignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DatasetAssignController {

    private final DatasetAssignService datasetAssignService;

    @PostMapping("/api/data/assign")
    public void dataAssign(@RequestParam Long userId, @RequestParam String path) {

        datasetAssignService.matchUserAndData(userId, path);
    }
}
