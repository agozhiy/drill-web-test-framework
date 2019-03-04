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

package org.apache.drillui.test.framework.steps.webui;

import org.apache.drillui.test.framework.initial.WebBrowser;
import org.apache.drillui.test.framework.pages.BasePage;
import org.apache.drillui.test.framework.pages.QueryProfileDetailsPage;
import org.apache.drillui.test.framework.pages.QueryProfileDetailsPage.QueryType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProfileDetailsSteps extends BaseSteps {

  public QueryProfileDetailsSteps openProfile(String queryProfile) {
    BaseSteps.openUrl("/profiles/" + queryProfile);
    return this;
  }

  public void validatePage() {

  }

  public QueryProfileDetailsSteps navigateTab(String tabText) {
    getPage().navigateTab(tabText);
    return this;
  }

  public String activeTab() {
    return getPage().activeTab();
  }

  public String activePanel() {
    return getPage().activePanelId();
  }

  public String getQueryText() {
    return getPage().waitForEditorText()
        .getQueryText();
  }

  public QueryProfileDetailsSteps setQueryText(String text) {
    getPage().setQueryText(text);
    return this;
  }

  public QueryResultsSteps rerunSQL() {
    getPage().setQueryType(QueryType.SQL)
        .rerunQuery();
    return BaseSteps.getSteps(QueryResultsSteps.class);
  }

  public QueryResultsSteps rerunPhysical() {
    getPage().setQueryType(QueryType.PHYSICAL)
        .rerunQuery();
    return BaseSteps.getSteps(QueryResultsSteps.class);
  }

  public QueryResultsSteps rerunLogical() {
    getPage().setQueryType(QueryType.LOGICAL)
        .rerunQuery();
    return BaseSteps.getSteps(QueryResultsSteps.class);
  }

  public boolean validatePlan(String pattern) {
    String plan = getPage().getPlan();
    Matcher matcher = Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE)
        .matcher(plan);
    return matcher.find();
  }

  public VisualPlan getVisualPlan() {
    return getVisualPlan(getPage().getPlanNodes());
  }

  public VisualPlan getPrintingPlan() {
    return getVisualPlan(getPage().getPrintedPlanNodes());
  }

  private VisualPlan getVisualPlan(List<Map<String, String>> planNodes) {
    VisualPlan plan = null;
    VisualPlan.PlanNode head = null;
    Map<VisualPlan.PlanNode, Integer> coordinates = new HashMap<>();
    for (Map<String, String> nodeMap : planNodes) {
      String label = nodeMap.get("label");
      Integer coordinate = Integer.parseInt(nodeMap.get("coordinate"));
      if (plan == null) {
        plan = new VisualPlan();
        head = plan.append(label);
      } else {
        if (coordinate > coordinates.get(head)) {
          head = head.append(label);
        } else {
          while (!coordinate.equals(coordinates.get(head))) {
            head = head.parent;
          }
          head = head.parent.append(label);
        }
      }
      coordinates.put(head, coordinate);
    }
    return plan;
  }

  private QueryProfileDetailsPage getPage() {
    return BasePage.getPage(QueryProfileDetailsPage.class);
  }

  public QueryProfileDetailsSteps printPlan() {
    Thread t = getPage().printPlan();
    while (t.isAlive()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      BaseSteps.pressEsc();
    }
    WebBrowser.switchToOpenedWindow();
    return this;
  }

  public QueryProfileDetailsSteps closePrintWindow() {
    WebBrowser.closeWindow();
    return this;
  }

  public class VisualPlan {

    private PlanNode root;

    public PlanNode append(String label) {
      if (root == null) {
        root = new PlanNode(label);
      }
      return root;
    }

    public PlanNode getNode(String label) {
      return root.getNode(label);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      } else if (!(obj instanceof VisualPlan)) {
        return false;
      }
      return root.equals(((VisualPlan) obj).root);
    }

    @Override
    public int hashCode() {
      return root.hashCode();
    }

    @Override
    public String toString() {
      return root.toString();
    }

    public class PlanNode {

      private final String label;
      private PlanNode parent;
      private Set<PlanNode> children = new HashSet<>();

      private PlanNode(String label) {
        this.label = label;
      }

      public PlanNode append(String label) {
        PlanNode child = new PlanNode(label);
        child.parent = this;
        children.add(child);
        return child;
      }

      public PlanNode getNode(String label) {
        if (this.label.equals(label)) {
          return this;
        } else {
          for (PlanNode child : children) {
            PlanNode desiredNode = child.getNode(label);
            if (desiredNode != null) {
              return desiredNode;
            }
          }
        }
        return null;
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == this) {
          return true;
        } else if (!(obj instanceof PlanNode)) {
          return false;
        }
        return label.equals(((PlanNode) obj).label) &&
            children.equals(((PlanNode) obj).children);
      }

      @Override
      public int hashCode() {
        return label.hashCode();
      }

      @Override
      public String toString() {
        StringBuilder result = new StringBuilder(label);
        for (PlanNode child : children) {
          result.append(" | ")
              .append(child.toString());
        }
        return result.toString();
      }
    }
  }
}
