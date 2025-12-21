package kr.co.hdi.domain.data.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.admin.data.dto.response.VisualDataResponse;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.co.hdi.domain.data.entity.QVisualData.visualData;

@RequiredArgsConstructor
public class VisualDataRepositoryImpl implements VisualDataRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<VisualDataResponse> search(String q, VisualDataCategory category) {

        return queryFactory
                .select(
                        Projections.constructor(
                                VisualDataResponse.class,
                                visualData.id,
                                visualData.brandCode,
                                visualData.brandName,
                                visualData.sectorCategory,
                                visualData.mainProductCategory,
                                visualData.mainProduct,
                                visualData.target,
                                visualData.referenceUrl,
                                visualData.logoImage
                        )
                )
                .from(visualData)
                .where(
                        keywordContains(q),
                        categoryEq(category),
                        visualData.deletedAt.isNull()
                )
                .fetch();
    }

    private BooleanExpression keywordContains(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }

        return visualData.brandCode.containsIgnoreCase(q)
                .or(visualData.brandName.containsIgnoreCase(q))
                .or(visualData.sectorCategory.containsIgnoreCase(q))
                .or(visualData.mainProductCategory.containsIgnoreCase(q))
                .or(visualData.mainProduct.containsIgnoreCase(q))
                .or(visualData.target.containsIgnoreCase(q))
                .or(visualData.referenceUrl.containsIgnoreCase(q));
    }

    private BooleanExpression categoryEq(VisualDataCategory category) {
        if (category == null) {
            return null;
        }
        return visualData.visualDataCategory.eq(category);
    }
}
