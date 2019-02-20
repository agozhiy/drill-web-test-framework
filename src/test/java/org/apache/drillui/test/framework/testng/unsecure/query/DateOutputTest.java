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
import org.testng.annotations.Test;
import org.apache.drillui.test.framework.steps.webui.QuerySteps;
import org.apache.drillui.test.framework.testng.unsecure.BaseUnsecureTest;

import static org.testng.Assert.assertEquals;

public class DateOutputTest extends BaseUnsecureTest {

  private final QuerySteps querySteps = BaseSteps.getSteps(QuerySteps.class);

  @Test(groups = {"functional"})
  public void selectDateQuery() {
    String result = querySteps.runSQL("select date '2017-04-06';")
        .getRow(0).get(0);
    String expected = "2017-04-06";
    assertEquals(result, expected);
  }

  @Test(groups = {"functional"})
  public void selectDateQuery2() {
    String result = querySteps.runSQL("SELECT * FROM cp.`employee.json`;")
        .getRow(0).get(0);
    String expected = "1";
    assertEquals(result, expected);
  }

  @Test(groups = {"functional"})
  public void selectTimestampZeroQuery() {
    String result = querySteps.runSQL("select timestamp '2017-04-06 11:22:33';")
        .getRow(0).get(0);
    String expected = "2017-04-06T11:22:33";
    assertEquals(result, expected);
  }

  @Test(groups = {"functional"})
  public void selectTimestampOneQuery() {
    String result = querySteps.runSQL("select timestamp '2017-04-06 11:22:33.1';")
        .getRow(0).get(0);
    String expected = "2017-04-06T11:22:33.100";
    assertEquals(result, expected);
  }

  @Test(groups = {"functional"})
  public void selectTimestampTwoQuery() {
    String result = querySteps.runSQL("select timestamp '2017-04-06 11:22:33.12';")
        .getRow(0).get(0);
    String expected = "2017-04-06T11:22:33.120";
    assertEquals(result, expected);
  }

  @Test(groups = {"functional"})
  public void selectTimestampThreeQuery() {
    String result = querySteps.runSQL("select timestamp '2017-04-06 11:22:33.123';")
        .getRow(0).get(0);
    String expected = "2017-04-06T11:22:33.123";
    assertEquals(result, expected);
  }

  @Test(groups = {"functional"})
  public void selectTimeQuery() {
    String result = querySteps.runSQL("select time '11:22:33';")
        .getRow(0).get(0);
    String expected = "11:22:33";
    assertEquals(result, expected);
  }

}
