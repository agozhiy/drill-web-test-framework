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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryResultsPage extends BasePage {
  @FindBy(xpath = "//*[@id=\"result\"]/tbody/tr/td")
  private WebElement queryResultLine;

  @FindBy(id = "result")
  private WebElement queryResultTable;

  @FindBy(xpath = "//*[@title=\"Open in new window\"]")
  private WebElement profileButton;

  public List<String> getResultsTableHeader() {
    return Jsoup.parse(queryResultTable.getAttribute("outerHTML"))
        .select("thead")
        .select("tr")
        .select("th")
        .stream()
        .map(Element::text)
        .collect(Collectors.toList());
  }

  public List<List<String>> getResultsTable() {
    return Jsoup.parse(queryResultTable.getAttribute("outerHTML"))
        .outputSettings(new Document.OutputSettings()
            .prettyPrint(false))
        .select("tbody")
        .select("tr")
        .stream()
        .map(row -> row.select("td")
            .stream()
            .map(Element::html)
            .collect(Collectors.toList()))
        .collect(Collectors.toList());
  }

  public String getQueryProfile() {
    String result = profileButton.getText();
    /*
      Example:
      Button text - "Query Profile: 2393e37c-0cc6-f39a-3df1-aae892ab106c COMPLETED"
      The Pattern should return "2393e37c-0cc6-f39a-3df1-aae892ab106c"
     */
    Matcher matcher = Pattern.compile(":\\s+([\\w-]+)")
        .matcher(result);
    if (matcher.find()) {
      result = matcher.group(1);
    }
    return result;
  }

  public String getFirstResultCell() {
    return queryResultLine.getText();
  }
}
