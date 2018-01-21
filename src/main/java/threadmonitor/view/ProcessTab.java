/*
 * Copyright (c) 2018年01月19日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.view;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import threadmonitor.entry.Progress;
import threadmonitor.services.ProsessService;
import threadmonitor.util.Utils;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/19
 * @Version 1.0.0
 */
public class ProcessTab extends Tab {

    private final ProsessService prosessService;
    private ConcurrentHashMap<Tab, HashMap<String, Series>> seriesMap = new ConcurrentHashMap();

    public ProcessTab(String pid , ProsessService prosessService) throws ParseException {
        this.prosessService = prosessService;

        this.setText(pid);
        GridPane gridPane = new GridPane();
        List<Map<String, Progress>> records = prosessService.getDbService().getRecentRecord(prosessService.getConnInfo().getConnectIp());
        HashMap<String, Series> tabMap = new HashMap<>();
        Series memSeries = new Series();
        memSeries.getData().addAll(getSeriesData(records, pid, "mem"));
        LineChart<String, Number> memLineChart = getLineChart(memSeries, "内存占用趋势");
        tabMap.put("mem", memSeries);

        Series cpuSeries = new Series();
        cpuSeries.getData().addAll(getSeriesData(records, pid, "cpu"));
        LineChart<String, Number> cpuLineChart = getLineChart(cpuSeries, "CPU占用趋势");
        tabMap.put("cpu", cpuSeries);

        seriesMap.put(this, tabMap);
        gridPane.add(memLineChart, 1, 1);
        gridPane.add(cpuLineChart, 2, 1);

        TextArea processInfo = new TextArea();
        processInfo.appendText(prosessService.getSshService().queryProcessInfo(getConmand(records, pid), pid));

        gridPane.add(processInfo, 1, 2, 2, 1);
        this.setContent(gridPane);

        TabThread exitOnTabClose = new TabThread(this, pid);
        exitOnTabClose.start();

        this.setOnCloseRequest(event -> exitOnTabClose.cancel());
    }

    private String getConmand(List<Map<String, Progress>> records, String pid) {
        return records.get(0).get(pid).getCommandColumn();
    }

    private LineChart<String, Number> getLineChart(Series series, String name)
            throws ParseException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart(xAxis, yAxis);
        lineChart.getData().add(series);
        lineChart.setTitle(name);
        return lineChart;
    }

    private List<Data> getSeriesData(List<Map<String, Progress>> records, String pid, String field)
            throws ParseException {
        List<Data> dataList = new ArrayList<>();
        for (Map<String, Progress> map : records) {
            Progress current = map.get(pid);
            if (current == null) {
                continue;
            }
            dataList.add(getData(current, field));
        }
        return dataList;
    }

    private Data getData(Progress progress, String field) throws ParseException {
        switch (field) {
            case "cpu":
                return new Data(Utils.getHMS(progress.getDate()),
                        Utils.strToNum(progress.getCpuColumn()));
            case "mem":
                return new Data(Utils.getHMS(progress.getDate()),
                        Utils.strToNum(progress.getMemColumn()));
            default:
                return new Data<>();
        }
    }

    private class TabThread extends Thread {

        private Tab tab;
        private String pid;
        private AtomicBoolean isrun = new AtomicBoolean(true);

        public TabThread(Tab tab, String pid) {
            this.tab = tab;
            this.pid = pid;
        }

        @Override
        public void run() {
            while (isrun.get()) {
                List<Map<String, Progress>> records = prosessService.getDbService().getRecentRecord(prosessService.getConnInfo().getConnectIp());
                HashMap<String, Series> tabMap = seriesMap.get(tab);

                Platform.runLater(() -> {
                    for (String field : tabMap.keySet()) {
                        try {
                            List<Data> newdata = getSeriesData(records, pid, field);
                            tabMap.get(field).getData().clear();
                            tabMap.get(field).getData().addAll(newdata);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            isrun.set(false);
        }
    }
}
