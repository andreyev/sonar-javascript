/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.javascript.checks;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.javascript.checks.utils.CheckUtils;
import org.sonar.plugins.javascript.api.tree.ScriptTree;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.visitors.DoubleDispatchVisitorCheck;
import org.sonar.plugins.javascript.api.visitors.JavaScriptFile;
import org.sonar.plugins.javascript.api.visitors.LineIssue;
import org.sonar.plugins.javascript.api.visitors.SubscriptionVisitorCheck;
import org.sonar.squidbridge.annotations.RuleTemplate;

@Rule(key = "JavaScriptRegex")
@RuleTemplate
public class JavaScriptRegexCheck extends SubscriptionVisitorCheck {

  // private static final String MESSAGE = "Replace all tab characters in this file by sequences of white-spaces.";

  private static final String DEFAULT_MESSAGE = "The regular expression matches this comment.";
  private static final String DEFAULT_REGULAR_EXPRESSION = "";

  @RuleProperty(
    key = "regularExpression",
    description = "The regular expression",
    defaultValue = "" + DEFAULT_REGULAR_EXPRESSION)
  private String regularExpression = DEFAULT_REGULAR_EXPRESSION;

  @RuleProperty(
    key = "message",
    description = "The issue message",
    defaultValue = "" + DEFAULT_MESSAGE)
  private String message = DEFAULT_MESSAGE;

  private Pattern pattern = null;

  public void setMessage(String message) {
    this.message = message;
  }

  public void setRegularExpression(String regularExpression) {
    this.regularExpression = regularExpression;
  }

//  @Override
  public void visitScript(ScriptTree tree) {
    JavaScriptFile javaScriptFile = getContext().getJavaScriptFile();
    List<String> lines = CheckUtils.readLines(javaScriptFile);

    if (pattern == null && !Strings.isNullOrEmpty(regularExpression)) {
      try {
        pattern = Pattern.compile(regularExpression, Pattern.DOTALL);
      } catch (RuntimeException e) {
        throw new IllegalArgumentException("Unable to compile regular expression: " + regularExpression, e);
      }
    }

//    if (pattern != null) {
      for (int i = 0; i < lines.size(); i++) {
//        if (pattern.matcher(lines.get(i)).matches()) {
        if (lines.get(i).contains("127")) {
          addIssue(new LineIssue(this, i + 1, DEFAULT_MESSAGE));
          break;
        }
      }
 //   }

  }
  @Override
  public Set<Tree.Kind> nodesToVisit() {
    return ImmutableSet.of(Tree.Kind.TOKEN);
  }
}