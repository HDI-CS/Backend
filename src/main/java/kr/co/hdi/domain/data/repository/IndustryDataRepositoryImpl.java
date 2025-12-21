package kr.co.hdi.domain.data.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.admin.data.dto.response.IndustryDataResponse;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.co.hdi.domain.data.entity.QIndustryData.industryData;
import static kr.co.hdi.domain.data.entity.QVisualData.visualData;

@RequiredArgsConstructor
public class IndustryDataRepositoryImpl implements IndustryDataRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IndustryDataResponse> search(String q, IndustryDataCategory category) {

        return queryFactory
                .select(
                        Projections.constructor(
                                IndustryDataResponse.class,
                                industryData.id,
                                industryData.originalId,
                                industryData.companyName,
                                industryData.productName,
                                industryData.modelName,
                                industryData.price,
                                industryData.material,
                                industryData.size,
                                industryData.weight,
                                industryData.referenceUrl,
                                industryData.registeredAt,
                                industryData.productPath,
                                industryData.productTypeName,
                                industryData.detailImagePath,
                                industryData.frontImagePath,
                                industryData.sideImagePath
                        )
                )
                .from(industryData)
                .where(
                        keywordContains(q),
                        categoryEq(category),
                        industryData.deletedAt.isNull()
                )
                .fetch();
    }

    private BooleanExpression keywordContains(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }

        return industryData.originalId.containsIgnoreCase(q)
                .or(industryData.companyName.containsIgnoreCase(q))
                .or(industryData.material.containsIgnoreCase(q))
                .or(industryData.modelName.containsIgnoreCase(q))
                .or(industryData.productName.containsIgnoreCase(q))
                .or(industryData.referenceUrl.containsIgnoreCase(q));
    }

    private BooleanExpression categoryEq(IndustryDataCategory category) {
        if (category == null) {
            return null;
        }
        return industryData.industryDataCategory.eq(category);
    }
}
