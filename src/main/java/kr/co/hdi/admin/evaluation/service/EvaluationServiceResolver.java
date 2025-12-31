package kr.co.hdi.admin.evaluation.service;
import kr.co.hdi.admin.survey.exception.SurveyErrorCode;
import kr.co.hdi.admin.survey.exception.SurveyException;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EvaluationServiceResolver {

    private final Map<DomainType, EvaluationService> serviceMap;

    public EvaluationServiceResolver(List<EvaluationService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(
                        EvaluationService::getDomainType,
                        Function.identity()
                ));
    }

    public EvaluationService resolve(DomainType domainType) {
        EvaluationService service = serviceMap.get(domainType);
        if (service == null) {
            throw new SurveyException(SurveyErrorCode.INVALID_DOMAIN_TYPE);
        }
        return service;
    }
}
