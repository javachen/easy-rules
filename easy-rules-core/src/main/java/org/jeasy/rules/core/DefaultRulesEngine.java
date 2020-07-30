/*
 * The MIT License
 *
 *  Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.core;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link RulesEngine} implementation.
 * <p>
 * Rules are fired according to their natural order which is priority by default.
 * This implementation iterates over the sorted set of rules, evaluates the condition
 * of each rule and executes its actions if the condition evaluates to true.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
@Slf4j
public final class DefaultRulesEngine extends AbstractRulesEngine {
    /**
     * Create a new {@link DefaultRulesEngine} with default parameters.
     */
    public DefaultRulesEngine() {
        super();
    }

    /**
     * Create a new {@link DefaultRulesEngine}.
     *
     * @param parameters of the engine
     */
    public DefaultRulesEngine(final RulesEngineParameters parameters) {
        super(parameters);
    }

    @Override
    public Boolean fire(Rules rules, Facts facts) {
        beforeRulesEvaluate(rules, facts);
        Boolean result = doFire(rules, facts);
        afterRulesEvaluate(rules, facts);
        log.debug("Fire result:{}", result);
        return result;
    }

    Boolean doFire(Rules rules, Facts facts) {
        if (rules.isEmpty()) {
            log.warn("No rules registered! Nothing to apply");
            return true;
        }
        logEngineParameters();
        log(rules);
        log(facts);
        log.debug("Rules evaluation started");
        for (Rule rule : rules) {
            final String name = rule.getName();
            final int priority = rule.getPriority();
            if (priority > parameters.getPriorityThreshold()) {
                log.warn("Rule priority ({}) exceeded at rule '{}' with priority={}, next rules will be skipped",
                        parameters.getPriorityThreshold(), name, priority);
                break;
            }
            if (!shouldRuleEvaluate(rule, facts)) {
                log.debug("Rule '{}' has been skipped before being evaluated", name);
                continue;
            }
            boolean evaluationResult = false;
            try {
                evaluationResult = rule.evaluate(facts);
            } catch (Exception exception) {
                log.error("Rule '" + name + "' evaluated with error", exception);
                onEvaluationError(rule, facts, exception);
                // give the option to either skip next rules on evaluation error or continue by considering the evaluation error as false
                if (parameters.isSkipOnFirstNonTriggeredRule()) {
                    log.warn("Next rules will be skipped since parameter skipOnFirstNonTriggeredRule is set");
                    continue; //异常，则执行下一个
                }
            }

            boolean randomResult = false;
            if (evaluationResult) {
                Double threshold = rule.getThreshold();
                if (threshold > Rule.DEFAULT_THRESHOLD) {
                    threshold = Rule.DEFAULT_THRESHOLD;
                }
                if (threshold < 0d) {
                    threshold = 0d;
                }
                Double randomValue = RandomUtils.nextDouble(0, Rule.DEFAULT_THRESHOLD);
                randomResult = randomValue < threshold;
                log.info("Rule '{}' has been evaluated to {}, randomResult is {}, {} -> {}", name, evaluationResult, randomResult, randomValue, threshold);
            }
            afterRuleEvaluate(rule, facts, evaluationResult, randomResult);

            if (evaluationResult && randomResult) {
                try {
                    beforeExecute(rule, facts);
                    rule.execute(facts);
                    log.debug("Rule '{}' performed action successfully", name);
                    onExecuteSuccess(rule, facts);
                    if (parameters.isSkipOnFirstAppliedRule()) {
                        log.debug("Next rules will be skipped since parameter skipOnFirstAppliedRule is set");
                        return true; //有一个执行成功，则不再往下执行
                    }
                } catch (Exception exception) {
                    log.error("Rule '" + name + "' performed action with error", exception);
                    onExecuteFailure(rule, exception, facts);
                    if (parameters.isSkipOnFirstFailedRule()) {
                        log.debug("Next rules will be skipped since parameter skipOnFirstFailedRule is set");
                        continue; //异常，则执行下一个
                    }
                }
            } else {
                log.info("Rule '{}' has been evaluated to false, action will not been executed", name);
            }
        }
        return false;
    }

    private void logEngineParameters() {
        log.debug("{}", parameters);
    }

    private void log(Rules rules) {
        log.debug("Registered rules:");
        for (Rule rule : rules) {
            log.debug("Rule { name = '{}', description = '{}', priority = '{}', threshold= '{}', condition = '{}'}",
                    rule.getName(), rule.getDescription(), rule.getPriority(), rule.getThreshold(), ((BasicRule) rule).getExpression());
        }
    }

    private void log(Facts facts) {
        log.debug("Known facts:");
        for (Fact<?> fact : facts) {
            log.debug("{}", fact);
        }
    }

    @Override
    public Map<Rule, Boolean> check(Rules rules, Facts facts) {
        beforeRulesEvaluate(rules, facts);
        Map<Rule, Boolean> result = doCheck(rules, facts);
        afterRulesEvaluate(rules, facts);
        log.debug("Check result:{}", result);
        return result;
    }

    private Map<Rule, Boolean> doCheck(Rules rules, Facts facts) {
        log.debug("Checking rules");
        Map<Rule, Boolean> result = new HashMap<>();
        for (Rule rule : rules) {
            if (shouldRuleEvaluate(rule, facts)) {
                result.put(rule, rule.evaluate(facts));
            }
        }
        return result;
    }

    private void onExecuteFailure(final Rule rule, final Exception exception, Facts facts) {
        ruleListeners.forEach(ruleListener -> ruleListener.onFailure(rule, facts, exception));
    }

    private void onExecuteSuccess(final Rule rule, Facts facts) {
        ruleListeners.forEach(ruleListener -> ruleListener.onSuccess(rule, facts));
    }

    private void beforeExecute(final Rule rule, Facts facts) {
        ruleListeners.forEach(ruleListener -> ruleListener.beforeExecute(rule, facts));
    }

    private boolean shouldRuleEvaluate(Rule rule, Facts facts) {
        return ruleListeners.stream().allMatch(ruleListener -> ruleListener.beforeEvaluate(rule, facts));
    }

    private void afterRuleEvaluate(Rule rule, Facts facts, Boolean evaluationResult, Boolean randomResult) {
        ruleListeners.forEach(ruleListener -> ruleListener.afterEvaluate(rule, facts, evaluationResult, randomResult));
    }

    private void onEvaluationError(Rule rule, Facts facts, Exception exception) {
        ruleListeners.forEach(ruleListener -> ruleListener.onEvaluationError(rule, facts, exception));
    }

    private void beforeRulesEvaluate(Rules rule, Facts facts) {
        rulesEngineListeners.forEach(rulesEngineListener -> rulesEngineListener.beforeEvaluate(rule, facts));
    }

    private void afterRulesEvaluate(Rules rule, Facts facts) {
        rulesEngineListeners.forEach(rulesEngineListener -> rulesEngineListener.afterExecute(rule, facts));
    }
}
