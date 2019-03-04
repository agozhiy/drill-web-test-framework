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

import org.apache.drillui.test.framework.initial.WebBrowser;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;

public abstract class BasePage {

  private static Map<Class<? extends BasePage>, BasePage> pages;

  BasePage() {
    PageFactory.initElements(getDriver(), this);
  }

  public static <T extends BasePage> T getPage(Class<T> pageClass) {

    if (pages == null) {
      pages = new HashMap<>();
    }

    if (!pages.containsKey(pageClass)) {
      try {
        pages.put(pageClass, pageClass.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        WebBrowser.closeBrowser();
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }

    return pageClass.cast(pages.get(pageClass));
  }

  public static void clearPages() {
    pages.clear();
  }

  protected static WebDriver getDriver() {
    return WebBrowser.getDriver();
  }

  protected static void sendText(WebElement element, String text) {
    Toolkit.getDefaultToolkit().getSystemClipboard()
        .setContents(new StringSelection(text), null);
    element.sendKeys(Keys.CONTROL, "a");
    element.sendKeys(Keys.CONTROL, "v");
  }

}
