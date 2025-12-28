package kr.co.hdi.admin.survey.service;
import kr.co.hdi.admin.assignment.exception.AssignmentErrorCode;
import kr.co.hdi.admin.assignment.exception.AssignmentException;
import kr.co.hdi.admin.assignment.service.AssignmentService;
import kr.co.hdi.admin.survey.exception.SurveyErrorCode;
import kr.co.hdi.admin.survey.exception.SurveyException;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SurveyServiceResolver {

    private final Map<DomainType, SurveyService> serviceMap;

    public SurveyServiceResolver(List<SurveyService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(
                        SurveyService::getDomainType,
                        Function.identity()
                ));
    }

    public SurveyService resolve(DomainType domainType) {
        SurveyService service = serviceMap.get(domainType);
        if (service == null) {
            throw new SurveyException(SurveyErrorCode.INVALID_DOMAIN_TYPE);
        }
        return service;
    }
}
