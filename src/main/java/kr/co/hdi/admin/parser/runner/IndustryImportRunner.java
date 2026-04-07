package kr.co.hdi.admin.parser.runner;

import kr.co.hdi.admin.parser.service.HeadphoneImportService;
import kr.co.hdi.admin.parser.service.VisualImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("import")
@RequiredArgsConstructor
public class IndustryImportRunner implements CommandLineRunner {

    private final HeadphoneImportService service;

    @Override
    public void run(String... args) {

        service.importFromLocal(
                "/Users/choijeong-in/Downloads/headphone/data.xlsx",
                "/Users/choijeong-in/Downloads/headphone/images",
                10L
        );

        System.out.println("✅ IMPORT 완료");
    }
}

