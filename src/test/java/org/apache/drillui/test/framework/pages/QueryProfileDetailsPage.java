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

package org.apache.drillui.test.framework.pages;

import org.apache.drillui.test.framework.initial.TestProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryProfileDetailsPage extends BasePage{

  @FindBy(id = "query-tabs")
  private WebElement tabs;

  @FindBy(id = "query-content")
  private WebElement panels;

  @FindBy(id = "sql")
  private WebElement radioSQL;

  @FindBy(id = "physical")
  private WebElement radioPhysical;

  @FindBy(id = "logical")
  private WebElement radioLogical;

  @FindBy(xpath = "//button[contains(text(), 'Re-run query')]")
  private WebElement rerunButton;

  @FindBy(id = "query-visual-canvas")
  private WebElement printedNodes;

  @FindBy(xpath = "//button[contains(text(), 'Print Plan')]")
  private WebElement printPlan;

  private WebElement activePanel() {
    return panels.findElement(By.cssSelector(".active"));
  }

  private WebElement queryTextArea() {
    return activePanel().findElement(By.className("ace_text-input"));
  }

  private WebElement queryContent() {
    return activePanel().findElement(By.className("ace_content"));
  }

  public QueryProfileDetailsPage navigateTab(String tabText) {
    tabs.findElement(By.linkText(tabText))
        .click();
    return this;
  }

  public String activeTab() {
    return tabs.findElement(By.className("active"))
        .getText();
  }

  public List<Map<String, String>> getPlanNodes() {
    return getPlanNodes(activePanel());
  }

  public List<Map<String, String>> getPrintedPlanNodes() {
    return getPlanNodes(printedNodes);
  }

  private List<Map<String, String>> getPlanNodes(WebElement parentElement) {
    return parentElement.findElements(By.cssSelector(".node.enter"))
        .stream()
        .map(element -> {
          Map<String, String> node = new HashMap<>();
          node.put("label", element.getText());
          String coordinate = element.getAttribute("transform");
          Matcher matcher = Pattern.compile(",(\\d+)\\)")
              .matcher(coordinate);
          if (matcher.find()) {
            coordinate = matcher.group(1);
          }
          node.put("coordinate", coordinate);
          return node;
    }).collect(Collectors.toList());
  }

  public String activePanelId() {
    // Cannot use By.className due to a space in the class name.
    return activePanel().getAttribute("id");
  }

  public String getQueryText() {
    return queryContent().getText();
  }

  public String getPlan() {
    return activePanel().getText();
  }

  public QueryProfileDetailsPage setQueryText(String text) {
    sendText(queryTextArea(), text);
    return this;
  }

  public QueryProfileDetailsPage waitForEditorText() {
    if (getQueryText().equals("")) {
      new WebDriverWait(getDriver(), TestProperties.getInt("DEFAULT_TIMEOUT"))
          .until(driver -> !getQueryText().equals(""));
    }
    return this;
  }

  public QueryProfileDetailsPage setQueryType(QueryType queryType) {
    switch (queryType) {
      case SQL:
        radioSQL.click();
        break;
      case PHYSICAL:
        radioPhysical.click();
        break;
      case LOGICAL:
        radioLogical.click();
        break;
    }
    return this;
  }

  public void rerunQuery() {
    rerunButton.submit();
  }

  public Thread printPlan() {
    Thread t = new Thread( () -> printPlan.click());
    t.start();
    return t;
  }

  public enum QueryType {
    SQL,
    PHYSICAL,
    LOGICAL
  }
}
