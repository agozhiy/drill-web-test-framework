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

package org.apache.drillui.test.framework.testng.unsecure.query.profiles;

import org.apache.drillui.test.framework.steps.restapi.RestBaseSteps;
import org.apache.drillui.test.framework.steps.webui.BaseSteps;
import org.apache.drillui.test.framework.steps.webui.QueryProfileDetailsSteps;
import org.apache.drillui.test.framework.steps.webui.QuerySteps;
import org.apache.drillui.test.framework.testng.unsecure.BaseUnsecureTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ProfileDetailsTest extends BaseUnsecureTest {

  private final String testQuery = "select * from cp.`tpch/nation.parquet` limit 5";
  private final QueryProfileDetailsSteps queryProfileDetailsSteps = BaseSteps.getSteps(QueryProfileDetailsSteps.class);
  private final QuerySteps querySteps = BaseSteps.getSteps(QuerySteps.class);
  private String queryProfile;

  @BeforeClass
  public final void setupProfileDetailsTest() {
    RestBaseSteps.setupStoragePlugins();
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

  @Test (priority = -1)
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
    String newQueryProfile =  queryProfileDetailsSteps.navigateTab("Edit Query")
        .rerunSQL()
        .getQueryProfile();
    String queryText = queryProfileDetailsSteps.openProfile(newQueryProfile)
        .getQueryText();
    assertEquals(queryText, testQuery);
  }

  @Test
  public void rerunPhysicalPlan() {
    // Getting the physical plan if not present
    String queryPlan = querySteps.explainPlanForQuery(testQuery)
        .getRow(0).get(1);
    // Preparation: submitting the physical plan and getting the profile
    String newQueryProfile = querySteps.runPhysical(queryPlan)
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
    String queryPlan = querySteps.explainPlanLogicalForQuery(testQuery)
        .getRow(0).get(1)
        .replace("\"LOGICAL\"", "\"EXEC\"");
    // Preparation: submitting the physical plan and opening the profile
    String newQueryProfile = querySteps.runLogical(queryPlan)
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
}
