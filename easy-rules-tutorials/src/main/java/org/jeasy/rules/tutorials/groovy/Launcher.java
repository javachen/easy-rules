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
package org.jeasy.rules.tutorials.groovy;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.groovy.GroovyRule;

import java.util.HashMap;
import java.util.Map;

public class Launcher {

    public static void main(String[] args) {
        Facts facts = new Facts();
        Map<String, Object> event = new HashMap<>();
        event.put("RemoveCount", 12);
        event.put("SkuCount", 30);
        event.put("TotalPrice", 8);
        event.put("GoodsCount", 24);
        event.put("NoScanCount", 41);
        facts.put("event", event);

        // define rules
        GroovyRule groovyRule1 = new GroovyRule().name("rule1")
                .priority(1)
                .threshold(0.95)
                .when("event.get('RemoveCount') > 2");

        GroovyRule groovyRule2 = new GroovyRule().name("rule2")
                .priority(2)
                .threshold(0.5)
                .when("event.get('SkuCount') >=4 && event.get('TotalPrice')<10");

        GroovyRule groovyRule3 = new GroovyRule().name("rule3")
                .priority(3)
                .threshold(0.2)
                .when("event.get('GoodsCount') <=2 || event.get('GoodsCount') > 20");

        GroovyRule groovyRule4 = new GroovyRule().name("rule4")
                .priority(4)
                .when("event.get('NoScanCount') >=2");

        Rules rules = new Rules();
        rules.register(groovyRule1);
        rules.register(groovyRule2);
        rules.register(groovyRule3);
        rules.register(groovyRule4);

        // fire rules on known facts
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true).skipOnFirstNonTriggeredRule(true);
        RulesEngine rulesEngine = new DefaultRulesEngine(parameters);

//        rulesEngine.check(rules, facts);

        rulesEngine.fire(rules, facts);
    }

}