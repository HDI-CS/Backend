package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.response.*;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.repository.IndustryDataRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kr.co.hdi.admin.data.exception.DataErrorCode.DATA_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndustryDataService {

    private final YearRepository yearRepository;
    private final IndustryDataRepository industryDataRepository;

    /*
    산업 디자인 연도 목록 조회
     */
    public List<YearResponse> getIndustryDataYears() {

        List<Year> years = yearRepository.findAll();
        return years.stream()
                .map(YearResponse::from)
                .toList();
    }

    /*
    산업 디자인 데이터셋 리스트 조회
     */
    public List<IndustryDataWithCategoryResponse> getIndustryDataList(Long yearId) {

        List<IndustryData> industryDatas = industryDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        return industryDatas.stream()
                .collect(Collectors.groupingBy(IndustryData::getIndustryDataCategory))
                .entrySet()
                .stream()
                .map(entry -> new IndustryDataWithCategoryResponse(
                        entry.getKey().name(),
                        entry.getValue().stream()
                                .map(IndustryDataResponse::from)
                                .toList()
                ))
                .toList();
    }

    /*
    산업 디자인 데이터셋 조회
     */
    public IndustryDataResponse getIndustryData(Long dataId) {

        IndustryData industryData = industryDataRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        return IndustryDataResponse.from(industryData);
    }

    /*
    산업 전문가에게 매칭할 데이터셋 후보 조회
     */
    public List<IndustryDataIdsResponse> getIndustryDataIds(Long yearId) {

        return industryDataRepository.findIdByYearId(yearId);
    }
}
