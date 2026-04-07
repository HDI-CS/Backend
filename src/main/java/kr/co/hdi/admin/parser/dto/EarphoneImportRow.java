package kr.co.hdi.admin.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EarphoneImportRow {

    private final String code;
    private final String companyName;
    private final String productName;
    private final String productPath;
    private final String productTypeName;
    private final String usage;
    private final String noiseCancelling;
    private final String codec;
    private final String extraFeatures;
    private final String controlType;
    private final String maxPlayTime;
    private final String chargeTime;
    private final String weight;
    private final String price;
    private final String registeredAt;
    private final String referenceUrl;

    private final String waterproof;
    private final String shoppingUrl;
}