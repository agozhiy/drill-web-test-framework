/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.drillui.test.framework.steps.webui;

import org.apache.drillui.test.framework.pages.BasePage;
import org.apache.drillui.test.framework.pages.QueryProfileDetailsPage;
import org.apache.drillui.test.framework.pages.QueryProfileDetailsPage.QueryType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProfileDetailsSteps extends BaseSteps {

  public QueryProfileDetailsSteps openProfile(String queryProfile) {
    BaseSteps.openUrl("/profiles/" + queryProfile);
    return this;
  }

  public void validatePage() {

  }

  public QueryProfileDetailsSteps navigateTab(String tabText) {
    getPage().navigateTab(tabText);
    return this;
  }

  public String activeTab() {
    return getPage().activeTab();
  }

  public String activePanel() {
    return getPage().activePanelId();
  }

  public String getQueryText() {
    return getPage().waitForEditorText()
        .getQueryText();
  }

  public QueryProfileDetailsSteps setQueryText(String text) {
    getPage().setQueryText(text);
    return this;
  }

  public QueryResultsSteps rerunSQL() {
    getPage().setQueryType(QueryType.SQL)
        .rerunQuery();
    return BaseSteps.getSteps(QueryResultsSteps.class);
  }

  public QueryResultsSteps rerunPhysical() {
    getPage().setQueryType(QueryType.PHYSICAL)
        .rerunQuery();
    return BaseSteps.getSteps(QueryResultsSteps.class);
  }

  public QueryResultsSteps rerunLogical() {
    getPage().setQueryType(QueryType.LOGICAL)
        .rerunQuery();
    return BaseSteps.getSteps(QueryResultsSteps.class);
  }

  public boolean validatePlan(String pattern) {
    String plan = getPage().getPlan();
    Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE)
        .matcher(plan);
    return matcher.find();
  }

  private QueryProfileDetailsPage getPage() {
    return BasePage.getPage(QueryProfileDetailsPage.class);
  }
}
