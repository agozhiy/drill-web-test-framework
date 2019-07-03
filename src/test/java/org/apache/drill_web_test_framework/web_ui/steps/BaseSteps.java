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
package org.apache.drill_web_test_framework.web_ui.steps;

import org.apache.drill_web_test_framework.web_ui.WebBrowser;
import org.apache.drill_web_test_framework.web_ui.pages.BasePage;
import org.apache.drill_web_test_framework.properties.PropertiesConst;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BaseSteps {

  private static Map<Class<? extends BaseSteps>, BaseSteps> steps;

  public static <T extends BaseSteps> T getSteps(Class<T> stepsClass) {

    if (steps == null) {
      steps = new HashMap<>();
    }

    if (!steps.containsKey(stepsClass)) {
      try {
        steps.put(stepsClass, stepsClass.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }

    return stepsClass.cast(steps.get(stepsClass));
  }

  public static void setImplicitWait(int seconds) {
    WebBrowser.setImplicitWait(seconds);
  }

  public static void resetImplicitWait() {
    WebBrowser.resetImplicitWait();
  }

  public static void tearDown() {
    BasePage.clearPages();
    WebBrowser.closeBrowser();
  }

  public static void openUrl(String url) {
    WebBrowser.openURL(url);
  }

  public static void waitForURL(String url) {
    new WebDriverWait(WebBrowser.getDriver(), PropertiesConst.DEFAULT_TIMEOUT)
        .until(driver -> WebBrowser.getURL().equals(url)
    );
  }

  public static String getURL() {
    return WebBrowser.getURL();
  }

  protected WebDriver getDriver() {
    return WebBrowser.getDriver();
  }

  protected static void pressKey(int key) {
    Robot robot;
    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    robot.keyPress(key);
    robot.keyRelease(key);
  }
}
