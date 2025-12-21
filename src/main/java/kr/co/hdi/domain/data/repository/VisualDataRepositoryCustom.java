package kr.co.hdi.domain.data.repository;

import kr.co.hdi.admin.data.dto.response.IndustryDataResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataResponse;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.enums.VisualDataCategory;

import java.util.List;

public interface VisualDataRepositoryCustom {

    List<VisualDataResponse> search(String q, VisualDataCategory category);
}
