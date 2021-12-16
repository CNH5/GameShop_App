package com.example.gameshop.charts;

import android.graphics.Color;
import android.view.View;
import com.example.gameshop.pojo.Price;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.*;
import lecho.lib.hellocharts.view.LineChartView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sheng
 * @date 2021/12/8 12:22
 */
public class HistoryPriceChart {
    private final LineChartView chart;
    private final List<PointValue> mPointValues = new ArrayList<>();
    private final List<AxisValue> mAxisXValues = new ArrayList<>();
    private final LineChartData data = new LineChartData();
    private int n;

    public HistoryPriceChart(LineChartView chart, List<Price> data, Float now_price) {
        this.chart = chart;

        for (int i = 0; i < data.size(); i++) {
            mPointValues.add(new PointValue(i, data.get(i).getPrice()));
            mAxisXValues.add(new AxisValue(i).setLabel(data.get(i).getDate().substring(5)));
        }


        Date date = new Date();
        String today = date.getMonth() + 1 + "-" + date.getDate();

        if (!data.get(data.size()-1).getDate().substring(5).equals(today)){
            mPointValues.add(new PointValue(mPointValues.size(), now_price));
            mAxisXValues.add(new AxisValue(mAxisXValues.size()).setLabel(today));
        }
    }

    public HistoryPriceChart line(String colorString) {
        Line line = new Line(mPointValues).setColor(Color.parseColor(colorString));

        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);

        lines.add(line);

        data.setValueLabelBackgroundColor(Color.parseColor(colorString));
        data.setValueLabelsTextColor(Color.WHITE);  //此处设置坐标点旁边的文字颜色
        data.setLines(lines);
        return this;
    }

    private void initX() {
        Axis axisX = new Axis(); //X轴
        axisX.setTextSize(12);//设置字体大小
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        axisX.setMaxLabelChars(Math.min(6, mPointValues.size())); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        axisX.setHasLines(true); //x 轴分割线
        data.setAxisXBottom(axisX); //x 轴在底部
    }

    private void initY() {
        Axis axisY = new Axis();  //Y轴
        axisY.setTextSize(15);//设置字体大小

        //data.setAxisYRight(axisY);  //y轴设置在右边
        List<AxisValue> values = new ArrayList<>();

        float min = Float.MAX_VALUE;  // 最小价格
        float max = Float.MIN_VALUE;  // 最大价格
        for (PointValue value : mPointValues) {
            min = Math.min(min, value.getY());
            max = Math.max(max, value.getY());
        }

        n = (int) ((max - min) / 10) + 3;
        chart.setMinimumHeight(n * 20);
        axisY.setMaxLabelChars(n);
        for (int i = (((int) (max / 10)) * 10 + 10); i >= min - 10; i -= 10) {
            values.add(new AxisValue(i).setLabel(String.valueOf(i)));
        }
        axisY.setHasTiltedLabels(false);  //坐标轴字体是斜的显示还是直的，true是斜的显示
        axisY.setTextColor(Color.BLACK);  //设置字体颜色
        axisY.setHasLines(true); // y轴分割线
        axisY.setValues(values);

        data.setAxisYLeft(axisY);  //Y轴设置在左边
    }

    public void init() {
        initX();
        initY();
        //设置行为属性，支持缩放、滑动以及平移
        chart.setInteractive(true);
        chart.setZoomType(ZoomType.HORIZONTAL);
        chart.setMaxZoom(2f);//最大方法比例
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        chart.setLineChartData(data);
        chart.setVisibility(View.VISIBLE);

        Viewport v = new Viewport(chart.getMaximumViewport());
        v.left = 0;
        v.right = 6;
        v.bottom = 0;
        v.top = n;
        chart.setCurrentViewport(v);
    }
}
