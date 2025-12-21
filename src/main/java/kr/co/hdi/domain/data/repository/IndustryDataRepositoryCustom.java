package kr.co.hdi.domain.data.repository;

import kr.co.hdi.admin.data.dto.response.IndustryDataResponse;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;

import java.util.List;

public interface IndustryDataRepositoryCustom {

    List<IndustryDataResponse> search(String q, IndustryDataCategory category);
}
