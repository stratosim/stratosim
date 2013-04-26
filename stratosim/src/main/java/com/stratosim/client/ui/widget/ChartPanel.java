package com.stratosim.client.ui.widget;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.ScatterChart;

public class ChartPanel extends Composite implements RequiresResize {

  // This is a hack because I can't figure out when offset sizes are gettable.
  @SuppressWarnings("unused")
  // It is used, eclipse just doesn't get it.
  private Timer offsetSizeGetter;

  private static final int GRAPH_LINE_WIDTH = 2;
  private static final int GRAPH_POINT_SIZE = 0;

  private DataView view;
  private String title;
  private String xTitle;
  private String yTitle;
  private String color;

  private FlowPanel panel;

  public ChartPanel(DataView view, String title, String xTitle, String yTitle, String color) {
    this.view = view;
    this.title = title;
    this.xTitle = xTitle;
    this.yTitle = yTitle;
    this.color = color;

    panel = new FlowPanel();

    initWidget(panel);
  }

  protected void onLoad() {
    offsetSizeGetter = new SizeTimer(this);
  }

  private Widget createChart(int width, int height) {
    Options opt = ScatterChart.createOptions();
    opt.setLineWidth(GRAPH_LINE_WIDTH);
    opt.setColors(color);
    opt.setPointSize(GRAPH_POINT_SIZE);
    opt.setLegend(LegendPosition.NONE);
    AxisOptions hopt = AxisOptions.create();
    hopt.setTitle(xTitle);
    opt.setHAxisOptions(hopt);
    AxisOptions vopt = AxisOptions.create();
    vopt.setTitle(yTitle);
    opt.setVAxisOptions(vopt);
    opt.setTitle(title);

    opt.setWidth(width);
    opt.setHeight(height);

    ScatterChart chart = new ScatterChart(view, opt);

    return chart;
  }

  @Override
  public void onResize() {
    if (getOffsetWidth() != 0 && getOffsetHeight() != 0) {
      panel.clear();
      panel.add(createChart(getOffsetWidth(), getOffsetHeight()));
    }
  }
}
