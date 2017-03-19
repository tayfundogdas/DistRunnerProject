package org.td.distrunner.engine;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class MethodLogger {
	@Around("execution(* *(..)) && @annotation(EnableLogging)")
	public Object around(ProceedingJoinPoint point) {
		long start = System.nanoTime();
		Object result = "";
		long end = 0;
		try {
			result = point.proceed();
			end = System.nanoTime() - start;
		} catch (Throwable e) {
			LogHelper.logError(e);
		}

		// sample output
		// com.example.Foo #power(2, 10): 1024 in 12μs
		StringBuilder logText = new StringBuilder();
		logText.append(point.getSignature().getDeclaringTypeName());
		logText.append(' ');
		logText.append('#');
		logText.append(MethodSignature.class.cast(point.getSignature()).getMethod().getName());
		logText.append('(');
		for (Object parameter : point.getArgs()) {
			logText.append(parameter.toString());
		}
		logText.append(')');
		logText.append(':');
		logText.append(' ');
		logText.append(result);
		logText.append(" in ");
		logText.append(end);
		logText.append("ns");

		LogHelper.logTrace(logText.toString());
		return result;
	}
}
