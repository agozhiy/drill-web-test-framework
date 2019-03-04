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
package org.apache.drillui.test.framework.initial;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public abstract class WebBrowser {

  private static WebDriver driver;

  private static LinkedList<String> parentWindows = new LinkedList<>();

  private static void init() {
    switch (TestProperties.get("DRIVER_TYPE")) {
      case "CHROME":
        System.setProperty("webdriver.chrome.driver", getWebdriversPath());
        driver = new ChromeDriver();
        break;
      case "FIREFOX":
        System.setProperty("webdriver.gecko.driver", getWebdriversPath());
        driver = new FirefoxDriver();
        break;
      case "IE":
        System.setProperty("webdriver.ie.driver", getWebdriversPath());
        driver = new InternetExplorerDriver();
        break;
      case "EDGE":
        System.setProperty("webdriver.edge.driver", getWebdriversPath());
        driver = new EdgeDriver();
        break;
      default:
        System.setProperty("webdriver.chrome.driver", getWebdriversPath());
        driver = new ChromeDriver();
    }
    resetImplicitWait();
    openURL("/");
    maximizeWindow();
  }

  private static String getWebdriversPath() {
    String path = "webdrivers/" + TestProperties.OS + "_" + TestProperties.get("DRIVER_TYPE");
    if(TestProperties.OS.equals("WINDOWS")) {
      path += ".exe";
    }
    return path;
  }

  public static void setImplicitWait (int seconds) {
    getDriver().manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
  }

  public static void resetImplicitWait () {
    setImplicitWait(TestProperties.getInt("DEFAULT_TIMEOUT"));
  }

  public static WebDriver getDriver() {
    if (driver == null) {
      init();
    }

    return driver;
  }

  public static void openURL(String url) {
    driver.get(TestProperties.get("DRILL_HOST") + ":" + TestProperties.get("DRILL_PORT") + url);
  }

  public static String getURL() {
    URL url;
    try {
      url = new URL(driver.getCurrentUrl());
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    return url.getPath();
  }

  private static void maximizeWindow() {
    driver.manage().window().maximize();
  }

  public static void closeBrowser() {
    if (driver != null) {
      driver.quit();
      driver = null;
    }
  }

  public static void switchToOpenedWindow() {
    parentWindows.push(driver.getWindowHandle());
    for (String window : driver.getWindowHandles()) {
      if (!window.equals(parentWindows.getLast())) {
        driver.switchTo().window(window);
        break;
      }
    }
  }

  public static void closeWindow() {
    driver.close();
    if (parentWindows.size() != 0) {
      driver.switchTo().window(parentWindows.pop());
    }
  }

  public static void waitForWindowOpening(int numberOfOpenWindows) {
    new WebDriverWait(driver, TestProperties.getInt("DEFAULT_TIMEOUT"))
        .until(driver -> driver.getWindowHandles().size() != numberOfOpenWindows);
  }
}
