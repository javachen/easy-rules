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
package org.jeasy.rules.groovy;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.BasicRule;

/**
 * A {@link org.jeasy.rules.api.Rule} implementation that uses <a href="https://github.com/apache/groovy">Groovy</a> to evaluate and execute the rule.
 *
 * @author Medina Computama <medina.computama@gmail.com>
 */
public class GroovyRule extends BasicRule {

    private Condition condition = Condition.FALSE;
    private List<Action> actions = new ArrayList<>();
    
    /**
     * Create a new Groovy rule.
     */
    public GroovyRule() {
        super(Rule.DEFAULT_NAME, Rule.DEFAULT_DESCRIPTION, Rule.DEFAULT_PRIORITY);
    }

    /**
     * Set rule name.
     *
     * @param name of the rule
     * @return this rule
     */
    public GroovyRule name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set rule description.
     *
     * @param description of the rule
     * @return this rule
     */
    public GroovyRule description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set rule priority.
     *
     * @param priority of the rule
     * @return this rule
     */
    public GroovyRule priority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Specify the rule's condition as Groovy expression.
     * @param condition of the rule
     * @return this rule
     */
    public GroovyRule when(String condition) {
        return this.when(condition, CompilerConfiguration.DEFAULT);
    }

    /**
     * Specify the rule's condition as Groovy expression.
     * @param condition of the rule
     * @param parserContext the Groovy parser context
     * @return this rule
     */
    public GroovyRule when(String condition, CompilerConfiguration parserContext) {
        this.condition = new GroovyCondition(condition, parserContext);
        return this;
    }

    /**
     * Add an action specified as an Groovy expression to the rule.
     * @param action to add to the rule
     * @return this rule
     */
    public GroovyRule then(String action) {
        return this.then(action, CompilerConfiguration.DEFAULT);
    }

    /**
     * Add an action specified as an Groovy expression to the rule.
     * @param action to add to the rule
     * @param parserContext the Groovy parser context
     * @return this rule
     */
    public GroovyRule then(String action, CompilerConfiguration parserContext) {
        this.actions.add(new GroovyAction(action, parserContext));
        return this;
    }

    @Override
    public boolean evaluate(Facts facts) {
        return condition.evaluate(facts);
    }

    @Override
    public void execute(Facts facts) throws Exception {
        for (Action action : actions) {
            action.execute(facts);
        }
    }
    
}
