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

import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.support.AbstractRuleFactory;
import org.jeasy.rules.support.RuleDefinition;
import org.jeasy.rules.support.reader.JsonRuleDefinitionReader;
import org.jeasy.rules.support.reader.RuleDefinitionReader;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.io.Reader;
import java.util.List;

/**
 * Factory to create {@link GroovyRule} instances.
 *
 * @author Medina Computama <medina.computama@gmail.com>
 */
public class GroovyRuleFactory extends AbstractRuleFactory {

    private RuleDefinitionReader reader;

    /**
     * Create a new {@link GroovyRuleFactory} with a given reader.
     *
     * @param reader to use to read rule definitions
     * @see YamlRuleDefinitionReader
     * @see JsonRuleDefinitionReader
     */
    public GroovyRuleFactory(RuleDefinitionReader reader) {
        this.reader = reader;
    }

    /**
     * Create a set of {@link GroovyRule} from a Reader.
     *
     * @param rulesDescriptor as a Reader
     * @return a set of rules
     */
    public Rules createRules(Reader rulesDescriptor) throws Exception {
        Rules rules = new Rules();
        List<RuleDefinition> ruleDefinitions = reader.read(rulesDescriptor);
        for (RuleDefinition ruleDefinition : ruleDefinitions) {
            rules.register(createRule(ruleDefinition));
        }
        return rules;
    }

    @Override
    protected Rule createSimpleRule(RuleDefinition ruleDefinition) {
        GroovyRule groovyRule = new GroovyRule()
                .name(ruleDefinition.getName())
                .description(ruleDefinition.getDescription())
                .priority(ruleDefinition.getPriority())
                .threshold(ruleDefinition.getThreshold())
                .when(ruleDefinition.getCondition());
        for (String action : ruleDefinition.getActions()) {
            groovyRule.then(action);
        }
        return groovyRule;
    }

}
