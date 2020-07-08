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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.Serializable;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * The Groovy convenience class is a collection of static methods that provides a set of easy integration points for
 * Groovy.
 *
 * @author Medina Computama <medina.computama@gmail.com>
 */
public class Groovy {

    static Serializable compileExpression(String expression) {
        return expression;
    }

    static Serializable compileExpression(String expression, CompilerConfiguration parserContext) {
        return expression;
    }

    static Object executeExpression(Serializable compiledExpression, Map<String, Object> asMap, CompilerConfiguration parserContext) {
        Binding binding = new Binding(asMap);
        GroovyShell shell = new GroovyShell(binding, parserContext);
        return shell.evaluate(compiledExpression.toString());
    }

    static Object executeExpression(Serializable compiledExpression, Map<String, Object> asMap) {
        return executeExpression(compiledExpression, asMap, CompilerConfiguration.DEFAULT);
    }
    
}
