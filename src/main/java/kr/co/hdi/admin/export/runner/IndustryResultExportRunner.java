package kr.co.hdi.admin.export.runner;

import kr.co.hdi.admin.export.service.IndustryResultExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("export-industry-result")
@RequiredArgsConstructor
public class IndustryResultExportRunner implements ApplicationRunner {

    private final IndustryResultExportService service;

    @Override
    public void run(ApplicationArguments args) {
        List<String> positional = args.getNonOptionArgs();

        if (positional.size() < 3) {
            throw new IllegalArgumentException(
                    "사용법: --spring.profiles.active=export-industry-result <assessmentRoundId> <yearId> <outputPath>"
            );
        }

        Long assessmentRoundId = Long.parseLong(positional.get(0));
        Long yearId = Long.parseLong(positional.get(1));
        String outputPath = positional.get(2);

        service.export(assessmentRoundId, yearId, outputPath);
    }
}
