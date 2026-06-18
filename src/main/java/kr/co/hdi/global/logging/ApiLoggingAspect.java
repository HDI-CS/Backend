package kr.co.hdi.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

    @Around("execution(* kr.co.hdi..*Controller.*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String userId = getUserId(request);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            log.info("[API] SUCCESS | userId={} | method={} | uri={} | elapsed={}ms",
                    userId, method, uri, elapsed);

            return result;

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;

            log.error("[API] FAIL | userId={} | method={} | uri={} | elapsed={}ms | error={} | message={}",
                    userId, method, uri, elapsed, e.getClass().getSimpleName(), e.getMessage());

            throw e;
        }
    }

    private String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return "anonymous";

        Object userId = session.getAttribute("userId");
        return userId == null ? "anonymous" : userId.toString();
    }
}