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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryResultsPage extends BasePage {
  @FindBy(xpath = "//*[@id=\"result\"]/tbody/tr/td")
  private WebElement queryResultLine;

  @FindBy(xpath = "//*[@id=\"result_wrapper\"]/div[2]/div[1]/div/table/thead")
  private WebElement queryResultTableHeader;

  @FindBy(xpath = "//*[@id=\"result\"]/tbody")
  private WebElement queryResultTableBody;

  @FindBy(xpath = "//*[@title=\"Open in new window\"]")
  private WebElement profileButton;

  public List<String> getResultsTableHeader() {
    List<String> resultsTableHead = new LinkedList<>();
    for (String column : queryResultTableHeader.getAttribute("outerHTML").split("<th")) {
      if (!column.contains("</th>")) {
        continue;
      }
      resultsTableHead.add(column.
          replaceAll("<span .*", "").
          replaceAll(".*class=\"DataTables_sort_wrapper\">", "").
          replaceAll("</thead>", "").trim());
    }
    return resultsTableHead;
  }

  public List<List<String>> getResultsTableBody() {
    List<List<String>> resultsTable = new LinkedList<>();
    for (String row : queryResultTableBody.getAttribute("outerHTML").split("<tr")) {
      if (!row.contains("</tr>")) {
        continue;
      }
      List<String> columns = new LinkedList<>();
      for (String column : row.replaceAll("</tr>.*", "").trim().split("</td>")) {
        columns.add(column.replaceAll(".*>", "").trim());
      }
      resultsTable.add(columns);
    }
    return resultsTable;
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
