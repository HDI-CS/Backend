package kr.co.hdi.admin.assignment.dto.query;

import java.util.Set;
import java.util.stream.Collectors;

public record AssignmentDiff(
        Set<Long> toAdd,
        Set<Long> toRemove
) {
    public static AssignmentDiff of(
            Set<Long> existing,
            Set<Long> requested
    ) {
        Set<Long> toAdd = requested.stream()
                .filter(id -> !existing.contains(id))
                .collect(Collectors.toSet());

        Set<Long> toRemove = existing.stream()
                .filter(id -> !requested.contains(id))
                .collect(Collectors.toSet());

        return new AssignmentDiff(toAdd, toRemove);
    }
}
