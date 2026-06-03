package kr.co.hdi.crawl.excel;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("excel")
@RequiredArgsConstructor
public class ExcelRunner implements CommandLineRunner {

    private final IndustryResultExcelService excelService;

    @Override
    public void run(String... args) {

        Long yearId = 10L;

        excelService.exportIndustryResultToFile(yearId);

        System.out.println("✅ 엑셀 생성 완료: industry_result_0527.xlsx");
    }
}