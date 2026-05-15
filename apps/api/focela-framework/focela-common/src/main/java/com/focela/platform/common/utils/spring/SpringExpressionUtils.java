package com.focela.platform.common.utils.spring;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utility class for Spring EL expressions.
 */
public class SpringExpressionUtils {

    /**
     * Spring EL expression parser.
     */
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    /**
     * Parameter name discoverer.
     */
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private SpringExpressionUtils() {
    }

    /**
     * Parse a single EL expression from an aspect join point.
     *
     * @param joinPoint        join point
     * @param expressionString EL expression
     * @return evaluation result
     */
    public static Object parseExpression(JoinPoint joinPoint, String expressionString) {
        Map<String, Object> result = parseExpressions(joinPoint, Collections.singletonList(expressionString));
        return result.get(expressionString);
    }

    /**
     * Parse multiple EL expressions from an aspect join point.
     *
     * @param joinPoint         join point
     * @param expressionStrings EL expressions
     * @return map of expression to evaluation result
     */
    public static Map<String, Object> parseExpressions(JoinPoint joinPoint, List<String> expressionStrings) {
        // skip parsing when the input is empty
        if (CollUtil.isEmpty(expressionStrings)) {
            return MapUtil.newHashMap();
        }

        // Step 1: build the EvaluationContext for parsing.
        // Resolve the annotated method via the join point.
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // Resolve parameter names with Spring's ParameterNameDiscoverer.
        String[] paramNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        // Spring expression evaluation context.
        EvaluationContext context = new StandardEvaluationContext();
        // Populate the context with argument values.
        if (ArrayUtil.isNotEmpty(paramNames)) {
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // Step 2: parse each expression individually.
        Map<String, Object> result = MapUtil.newHashMap(expressionStrings.size(), true);
        expressionStrings.forEach(key -> {
            Object value = EXPRESSION_PARSER.parseExpression(key).getValue(context);
            result.put(key, value);
        });
        return result;
    }

    /**
     * Parse an EL expression using the Spring bean factory as the root.
     *
     * @param expressionString EL expression
     * @return evaluation result
     */
    public static Object parseExpression(String expressionString) {
        return parseExpression(expressionString, null);
    }

    /**
     * Parse an EL expression using the Spring bean factory as the root.
     *
     * @param expressionString EL expression
     * @param variables        variables for the evaluation context
     * @return evaluation result
     */
    public static Object parseExpression(String expressionString, Map<String, Object> variables) {
        if (StrUtil.isBlank(expressionString)) {
            return null;
        }
        Expression expression = EXPRESSION_PARSER.parseExpression(expressionString);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(SpringUtil.getApplicationContext()));
        if (MapUtil.isNotEmpty(variables)) {
            context.setVariables(variables);
        }
        return expression.getValue(context);
    }

}
