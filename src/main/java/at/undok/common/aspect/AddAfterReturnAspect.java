package at.undok.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StopWatch;

@Slf4j
// @Aspect
// @Component
@Profile("dev")
public class AddAfterReturnAspect {

    // @Around("execution(* at.undok.undok.client.service..*(..))")
    public Object afterReturn(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        log.info("Execution time of " + className + "." + methodName + " "
                + ":: " + stopWatch.getTotalTimeMillis() + " ms");
        return result;
    }


    // @Around("execution(* org.springframework.security.access..*(..))")
    public Object doHandle(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("access denied aop: {}", joinPoint);
        return joinPoint.proceed();
    }
}
