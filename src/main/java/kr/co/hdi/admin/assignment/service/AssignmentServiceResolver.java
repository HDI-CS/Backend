package kr.co.hdi.admin.assignment.service;

import kr.co.hdi.admin.assignment.exception.AssignmentErrorCode;
import kr.co.hdi.admin.assignment.exception.AssignmentException;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AssignmentServiceResolver {

    private final Map<DomainType, AssignmentService> serviceMap;

    public AssignmentServiceResolver(List<AssignmentService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(
                        AssignmentService::getDomainType,
                        Function.identity()
                ));
    }

    public AssignmentService resolve(DomainType domainType) {
        AssignmentService service = serviceMap.get(domainType);
        if (service == null) {
            throw new AssignmentException(AssignmentErrorCode.INVALID_DOMAIN_TYPE);
        }
        return service;
    }
}
