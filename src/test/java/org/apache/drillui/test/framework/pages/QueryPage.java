/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import org.apache.drillui.test.framework.initial.WebBrowser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class QueryPage extends BasePage {
  @FindBy(xpath = "//*[@id=\"message\"]")
  private WebElement sampleQueryElement;

  @FindBy(xpath = "//*[@id=\"message\"]/button")
  private WebElement sampleQueryCloseButton;

  @FindBy(xpath = "//*[@id=\"queryForm\"]/div[1]/label")
  private WebElement queryTypeLabel;

  @FindBy(xpath = "//*[@id=\"sql\"]")
  private WebElement queryTypeSQLRButton;

  @FindBy(xpath = "//*[@id=\"physical\"]")
  private WebElement queryTypePHYSICALRButton;

  @FindBy(xpath = "//*[@id=\"logical\"]")
  private WebElement queryTypeLOGICALRButton;

  @FindBy(xpath = "//*[@id=\"queryForm\"]/div[2]/label")
  private WebElement queryInputLabel;

  @FindBy(xpath = "//*[@id=\"query-editor-format\"]/textarea")
  private WebElement queryInputField;

  @FindBy(xpath = "//*[@id=\"queryForm\"]/button")
  private WebElement submitButton;

  public WebElement getQueryTypeLabel() {
    return queryTypeLabel;
  }

  public QueryResultsPage submitQuery(String queryText) {
    sendText(queryInputField, queryText);
    submitButton.submit();
    return getPage(QueryResultsPage.class);
  }

  public QueryPage setQueryType(QueryType queryType) {
    switch (queryType) {
      case SQL:
        queryTypeSQLRButton.click();
        break;
      case PHYSICAL:
        queryTypePHYSICALRButton.click();
        break;
      case LOGICAL:
        queryTypeLOGICALRButton.click();
        break;
    }
    return this;
  }

  public enum QueryType {
    SQL,
    PHYSICAL,
    LOGICAL
  }
}
