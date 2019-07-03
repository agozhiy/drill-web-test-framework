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

package org.apache.drill_web_test_framework.web_ui.tests.query.profiles;

import org.apache.drill_web_test_framework.properties.PropertiesConst;
import org.apache.drill_web_test_framework.rest_api.data.RestStorageSteps;
import org.apache.drill_web_test_framework.web_ui.steps.BaseSteps;
import org.apache.drill_web_test_framework.web_ui.steps.NavigationSteps;
import org.apache.drill_web_test_framework.web_ui.steps.QueryProfileDetailsSteps;
import org.apache.drill_web_test_framework.web_ui.steps.QuerySteps;
import org.apache.drill_web_test_framework.web_ui.tests.FunctionalTest;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ProfileDetailsTest extends FunctionalTest {
  private final String testQuery = "select * from cp.`tpch/nation.parquet` limit 5";
  private final QueryProfileDetailsSteps queryProfileDetailsSteps = BaseSteps.getSteps(QueryProfileDetailsSteps.class);
  private final QuerySteps querySteps = BaseSteps.getSteps(QuerySteps.class);
  private final NavigationSteps navigationSteps = BaseSteps.getSteps(NavigationSteps.class);
  private String queryProfile;

  @BeforeClass
  public final void setupProfileDetailsTest() {
    RestStorageSteps.setupStoragePlugins();
    navigationSteps.navigateQuery();
    queryProfile = querySteps.runSQL(testQuery)
        .getQueryProfile();
  }

  @BeforeMethod
  public final void openProfile() {
    queryProfileDetailsSteps.openProfile(queryProfile);
  }

  @Test
  public void validateProfileDetailsPage() {
    queryProfileDetailsSteps.validatePage();
  }

  @Test(priority = -1)
  public void tabNavigationTest() {
    assertEquals(queryProfileDetailsSteps.activeTab(), "Query");
    assertEquals(queryProfileDetailsSteps.activePanel(), "query-query");
    queryProfileDetailsSteps.navigateTab("Edit Query");
    assertEquals(queryProfileDetailsSteps.activeTab(), "Edit Query");
    assertEquals(queryProfileDetailsSteps.activePanel(), "query-edit");
    queryProfileDetailsSteps.navigateTab("Visualized Plan");
    assertEquals(queryProfileDetailsSteps.activeTab(), "Visualized Plan");
    assertEquals(queryProfileDetailsSteps.activePanel(), "query-visual");
    queryProfileDetailsSteps.navigateTab("Physical Plan");
    assertEquals(queryProfileDetailsSteps.activeTab(), "Physical Plan");
    assertEquals(queryProfileDetailsSteps.activePanel(), "query-physical");
    queryProfileDetailsSteps.navigateTab("Query");
    assertEquals(queryProfileDetailsSteps.activeTab(), "Query");
    assertEquals(queryProfileDetailsSteps.activePanel(), "query-query");
  }

  @Test
  public void queryTabTest() {
    String queryText = queryProfileDetailsSteps.navigateTab("Query")
        .getQueryText();
    assertEquals(queryText, testQuery);
    String editedQuery = "Edited query";
    queryText = queryProfileDetailsSteps.setQueryText(editedQuery)
        .getQueryText();
    // This tab is immutable, so the text shouldn't be changed.
    assertEquals(queryText, testQuery);
  }

  @Test
  public void editQueryTabTest() {
    String queryText = queryProfileDetailsSteps.navigateTab("Edit Query")
        .getQueryText();
    assertEquals(queryText, testQuery);
    String editedQuery = "Edited query";
    queryText = queryProfileDetailsSteps.setQueryText(editedQuery)
        .getQueryText();
    assertEquals(queryText, editedQuery);
    queryText = queryProfileDetailsSteps.setQueryText(testQuery)
        .getQueryText();
    assertEquals(queryText, testQuery);
  }

  @Test
  public void rerunQuery() {
    String newQueryProfile = queryProfileDetailsSteps.navigateTab("Edit Query")
        .rerunSQL()
        .getQueryProfile();
    String queryText = queryProfileDetailsSteps.openProfile(newQueryProfile)
        .getQueryText();
    assertEquals(queryText, testQuery);
  }

  @Test
  public void rerunPhysicalPlan() {
    // Getting the physical plan if not present
    navigationSteps.navigateQuery();
    String queryPlan = querySteps.explainPlanForQuery(testQuery)
        .getRow(1).get(1);
    // Preparation: submitting the physical plan and getting the profile
    String newQueryProfile = navigationSteps.navigateQuery()
        .runPhysical(queryPlan)
        .getQueryProfile();
    // The test itself
    newQueryProfile = queryProfileDetailsSteps.openProfile(newQueryProfile)
        .navigateTab("Edit Query")
        .rerunPhysical()
        .getQueryProfile();
    String queryText = queryProfileDetailsSteps.openProfile(newQueryProfile)
        .getQueryText();
    assertTrue(queryPlan.contains(queryText));
  }

  @Test
  public void rerunLogicalPlan() {
    // Getting the physical plan
    navigationSteps.navigateQuery();
    String queryPlan = querySteps.explainPlanLogicalForQuery(testQuery)
        .getRow(1).get(1)
        .replace("\"LOGICAL\"", "\"EXEC\"");
    // Preparation: submitting the physical plan and opening the profile
    String newQueryProfile = navigationSteps.navigateQuery()
        .runLogical(queryPlan)
        .getQueryProfile();
    // The test itself
    newQueryProfile = queryProfileDetailsSteps.openProfile(newQueryProfile)
        .navigateTab("Edit Query")
        .rerunLogical()
        .getQueryProfile();
    String queryText = queryProfileDetailsSteps.openProfile(newQueryProfile)
        .getQueryText();
    assertTrue(queryPlan.contains(queryText));
  }

  @Test
  public void verifyPhysicalPlan() {
    assertTrue(queryProfileDetailsSteps.navigateTab("Physical Plan")
        .validatePlan("Screen.*" +
            "Project.*" +
            "SelectionVectorRemover.*" +
            "Limit.*" +
            "Scan"));
  }


  @Test
  public void verifyVisualPlan() {
    QueryProfileDetailsSteps.VisualPlan expectedPlan = queryProfileDetailsSteps.new VisualPlan();
    expectedPlan
        .append("Screen 00-00")
        .append("Project 00-01")
        .append("SelectionVectorRemover 00-02")
        .append("Limit 00-03")
        .append("Scan 00-04");
    assertEquals(queryProfileDetailsSteps.navigateTab("Visualized Plan")
            .getVisualPlan(),
        expectedPlan);
  }

  @Test
  public void verifyComplexVisualPlan() {
    QueryProfileDetailsSteps.VisualPlan expectedPlan = queryProfileDetailsSteps.new VisualPlan();
    expectedPlan
        .append("Screen 00-00")
        .append("Project 00-01")
        .append("SelectionVectorRemover 00-02")
        .append("Limit 00-03")
        .append("Limit 00-04")
        .append("HashJoin 00-05")
        .append("Project 00-06")
        .append("Scan 00-08");
    expectedPlan.getNode("HashJoin 00-05")
        .append("Scan 00-07");
    String complexSQL = "select t1.n_nationkey from cp.`tpch/nation.parquet` t1 join cp.`tpch/nation.parquet` t2 on t1.n_nationkey = t2.n_nationkey limit 5";
    navigationSteps.navigateQuery();
    String complexPlanProfile = querySteps.runSQL(complexSQL)
        .getQueryProfile();
    QueryProfileDetailsSteps.VisualPlan actualPlan = queryProfileDetailsSteps.openProfile(complexPlanProfile)
        .navigateTab("Visualized Plan")
        .getVisualPlan();
    assertEquals(actualPlan, expectedPlan);
  }

  @Test
  public void verifyPrintPlan() {
//    if (PropertiesConst.RUN_ON_SELENOID) {
//      throw new SkipException("These Tests can't be run on Selenoid");
//    }
    String complexSQL = "select t1.n_nationkey from cp.`tpch/nation.parquet` t1 join cp.`tpch/nation.parquet` t2 on t1.n_nationkey = t2.n_nationkey limit 5";
    navigationSteps.navigateQuery();
    String complexPlanProfile = querySteps.runSQL(complexSQL)
        .getQueryProfile();
    QueryProfileDetailsSteps.VisualPlan expectedPlan = queryProfileDetailsSteps.new VisualPlan();
    expectedPlan
        .append("Screen 00-00")
        .append("Project 00-01")
        .append("SelectionVectorRemover 00-02")
        .append("Limit 00-03")
        .append("Limit 00-04")
        .append("HashJoin 00-05")
        .append("Project 00-06")
        .append("Scan 00-08");
    expectedPlan.getNode("HashJoin 00-05")
        .append("Scan 00-07");
    QueryProfileDetailsSteps.VisualPlan actualPlan = queryProfileDetailsSteps.openProfile(complexPlanProfile)
        .navigateTab("Visualized Plan")
        .printPlan()
        .getPrintingPlan();
    queryProfileDetailsSteps.closePrintWindow();
    assertEquals(actualPlan, expectedPlan);
  }

  @Test
  public void verifyOperators() {
    assertEquals(queryProfileDetailsSteps.getOperatorsList(),
        Arrays.asList("00-xx-00 - SCREEN",
            "00-xx-01 - PROJECT",
            "00-xx-02 - SELECTION_VECTOR_REMOVER",
            "00-xx-03 - LIMIT",
            "00-xx-04 - PARQUET_ROW_GROUP_SCAN"));
  }

  @Test
  public void verifyFullJSONProfile() {
    assertEquals(queryProfileDetailsSteps.validateJson(), "Ok");
  }

  @Test
  public void verifyCollapsibleItems() {
    assertTrue(queryProfileDetailsSteps.clickAllCollapsibleItems()
        .expandAllCollapsedItems()
        .clickAllCollapsibleItemsReversed()
        .isAllItemsCollapsed(), "Some items are not collapsed!");
    assertTrue(queryProfileDetailsSteps.clickAllCollapsibleItems()
        .isAllItemsExpanded());
  }
}
