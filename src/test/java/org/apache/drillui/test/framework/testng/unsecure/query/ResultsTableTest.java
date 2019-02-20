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
package org.apache.drillui.test.framework.testng.unsecure.query;

import org.apache.drillui.test.framework.steps.webui.BaseSteps;
import org.apache.drillui.test.framework.testng.unsecure.BaseUnsecureTest;
import org.testng.annotations.Test;
import org.apache.drillui.test.framework.steps.webui.QueryResultsSteps;
import org.apache.drillui.test.framework.steps.webui.QuerySteps;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class ResultsTableTest extends BaseUnsecureTest {

  private final QuerySteps querySteps = BaseSteps.getSteps(QuerySteps.class);
  private final QueryResultsSteps queryResultsSteps = BaseSteps.getSteps(QueryResultsSteps.class);

  @Test(groups = {"functional"})
  public void debugTest() {
    querySteps.runSQL("SELECT * FROM cp.`employee.json` LIMIT 9");
    assertEquals(QueryResultsSteps.rowsCount(), 9);
    assertEquals(QueryResultsSteps.columnsCount(), 16);
    assertEquals(queryResultsSteps.getRow(0), Arrays.asList("1", "Sheri Nowmer", "Sheri", "Nowmer", "1", "President", "0", "1", "1961-08-26", "1994-12-01 00:00:00.0", "80000.0", "0", "Graduate Degree", "S", "F", "Senior Management"));
  }

}
