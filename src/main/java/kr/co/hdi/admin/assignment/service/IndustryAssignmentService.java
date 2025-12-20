package kr.co.hdi.admin.assignment.service;

import kr.co.hdi.domain.assignment.repository.IndustryDataAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndustryAssignmentService implements AssignmentService {

    private final IndustryDataAssignmentRepository industryDataAssignmentRepository;

}
