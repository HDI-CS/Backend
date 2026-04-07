package kr.co.hdi.admin.parser.runner;

import kr.co.hdi.admin.parser.service.VisualImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("import")
@RequiredArgsConstructor
public class VisualImportRunner implements CommandLineRunner {

    private final VisualImportService service;

    @Override
    public void run(String... args) {

        service.importFromLocal(
                "/Users/choijeong-in/Downloads/visual/data.xlsx",
                "/Users/choijeong-in/Downloads/visual/images",
                11L
        );

        System.out.println("✅ IMPORT 완료");
    }
}
