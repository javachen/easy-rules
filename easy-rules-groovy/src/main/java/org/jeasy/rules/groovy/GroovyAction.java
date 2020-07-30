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

import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;

import java.io.Serializable;

/**
 * This class is an implementation of {@link Action} that uses <a href="https://github.com/apache/groovy">Groovy</a> to execute the action.
 *
 * @author Medina Computama <medina.computama@gmail.com>
 */
@Slf4j
public class GroovyAction implements Action {
    private String expression;
    private Serializable compiledExpression;

    /**
     * Create a new {@link GroovyAction}.
     *
     * @param expression the action written in expression language
     */
    public GroovyAction(String expression) {
        this.expression = expression;
        compiledExpression = Groovy.compileExpression(expression);
    }

    /**
     * Create a new {@link GroovyAction}.
     *
     * @param expression    the action written in expression language
     * @param parserContext the Groovy parser context
     */
    public GroovyAction(String expression, CompilerConfiguration parserContext) {
        this.expression = expression;
        compiledExpression = Groovy.compileExpression(expression, parserContext);
    }

    @Override
    public void execute(Facts facts) {
        try {
            Groovy.executeExpression(compiledExpression, facts.asMap());
        } catch (Exception e) {
            log.error("Unable to evaluate expression: '" + expression + "' on facts: " + facts, e);
            throw e;
        }
    }

}
