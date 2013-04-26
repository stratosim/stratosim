package com.stratosim.client.ui.graphexplorepanel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.ScatterChart;

public class GraphExplorePanel extends ResizeComposite {
  @UiField
  LayoutPanel mainPanel;
  @UiField
  ScrollPanel scrollPanel;

  private DataTable table = null;

  // TODO(tpondich): UI Binder
  private static final int GRAPH_LINE_WIDTH = 2;
  private static final int GRAPH_POINT_SIZE = 0;

  private static final int MARGIN = 10;
  private static final int CSS_BORDER_WIDTH = 1;
  private static final int MIN_HEIGHT = 250;

  private static GraphExplorePanelUiBinder uiBinder = GWT.create(GraphExplorePanelUiBinder.class);

  interface GraphExplorePanelUiBinder extends UiBinder<Widget, GraphExplorePanel> {}

  public GraphExplorePanel() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setData(DataTable table) {
    checkNotNull(table);
    checkArgument(table.getNumberOfColumns() >= 2);

    mainPanel.clear();

    // TODO(tpondich): Check that the table conforms to our standards.

    // TODO(tpondich): Defensive Copy.
    this.table = table;

    // See MainView::showGraphExplorePanel for why this is removed.
    // paint();
  }

  // Does java really not do this?
  private static final int[] toArray(Collection<Integer> coll) {
    int[] columnsA = new int[coll.size()];
    int i = 0;
    for (Integer v : coll) {
      columnsA[i++] = v;
    }
    return columnsA;
  }

  private void paint() {
    mainPanel.clear();

    String xTitle = table.getProperty(0, 0, "dimension");

    Multimap<String, Integer> chartTypes = LinkedHashMultimap.create();
    for (int i = 1; i < table.getNumberOfColumns(); i++) {
      chartTypes.put(table.getProperty(0, i, "group") + "|" + table.getProperty(0, i, "dimension"), i);
    }

    int nColumns = 1;
    int nRows = chartTypes.keySet().size();

    int i = 0;
    int width =
        (scrollPanel.getOffsetWidth() - (nColumns + 1) * MARGIN - 4 * CSS_BORDER_WIDTH) / nColumns;
    int height =
        (scrollPanel.getOffsetHeight() - (nRows + 1) * MARGIN - 4 * CSS_BORDER_WIDTH) / nRows;
    height = Math.max(MIN_HEIGHT, height);

    for (String yTitle : chartTypes.keySet()) {
      List<Integer> columns = Lists.newArrayList();
      columns.addAll(chartTypes.get(yTitle));

      List<String> colors = Lists.newArrayList();
      for (Integer c : columns) {
        colors.add(table.getProperty(0, c, "color"));
      }

      ScatterChart chart = createChart(columns, colors, xTitle, yTitle.substring(yTitle.indexOf("|") + 1), width, height);

      chart.setStylePrimaryName("stratosim-GraphExplorePanel-Chart");
      mainPanel.add(chart);
      mainPanel.setWidgetTopHeight(chart, MARGIN + (height + MARGIN) * i, Unit.PX, height, Unit.PX);
      mainPanel.setWidgetLeftRight(chart, MARGIN, Unit.PX, MARGIN, Unit.PX);

      i++;
    }
  }

  private ScatterChart createChart(List<Integer> columns, List<String> colors, String xTitle,
      String yTitle, int width, int height) {
    DataView view = DataView.create(table);

    Options opt = ScatterChart.createOptions();
    opt.setWidth(width);
    opt.setHeight(height);
    opt.setLineWidth(GRAPH_LINE_WIDTH);
    opt.setPointSize(GRAPH_POINT_SIZE);

    opt.setColors(colors.toArray(new String[colors.size()]));

    AxisOptions hopt = AxisOptions.create();
    hopt.setTitle(xTitle);
    opt.setHAxisOptions(hopt);
    AxisOptions vopt = AxisOptions.create();
    vopt.setTitle(yTitle);
    opt.setVAxisOptions(vopt);
    opt.setLegend(LegendPosition.RIGHT);

    Set<Integer> dataSet = Sets.newLinkedHashSet();
    dataSet.add(0);
    dataSet.addAll(columns);
    view.setColumns(toArray(dataSet));

    ScatterChart chart = new ScatterChart(view, opt);

    return chart;
  }

  @Override
  public void onResize() {
    paint();
  }
}
