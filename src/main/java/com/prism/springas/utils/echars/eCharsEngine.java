package com.prism.springas.utils.echars;

import com.alibaba.fastjson.JSON;
import com.prism.springas.utils.BasePage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * eChars数据可视化引擎
 */
public class eCharsEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    Map charsData = null;
    Map option = null;
    Integer charsType  = 0;
    List<BasePage> executeDataList = null;
    /**
     * init eCharsEngine
     * @param executeData 待处理的数据
     */
    public eCharsEngine(int type,List<BasePage> executeData){
        charsData = new ConcurrentHashMap();
        charsType = type;
        executeDataList = executeData;
    }

    /**
     * 数据JSON可视化处理
     * @return
     */
    public String getEcharsData(){
        return JSON.toJSONString(this.option);
    }

    /**
     * 单折线/柱状图
     * @param text
     * @return
     */
    public eCharsEngine toParseBasicChars(String text){
        Map option = new ConcurrentHashMap();

        Map title = new ConcurrentHashMap();
        title.put("text",text);

        Map xAxis = new ConcurrentHashMap();
        xAxis.put("type","category");
        xAxis.put("data",this.charsData.get("xAxis-data"));

        Map axisLabel  = new ConcurrentHashMap();
        axisLabel.put("interval",0);
        axisLabel.put("rotate","45");

        xAxis.put("axisLabel",axisLabel);

        Map yAxis = new ConcurrentHashMap();
        yAxis.put("type","value");

        Map series = new ConcurrentHashMap();
        series.put("data",this.charsData.get("series-data"));
        series.put("type",this.charsType == 0 ? "line" : "bar");

        List seriesData = new ArrayList<>();
        seriesData.add(series);

        Map tooltip = new ConcurrentHashMap();
        tooltip.put("trigger","axis");

        option.put("title",title);
        option.put("xAxis",xAxis);
        option.put("yAxis",yAxis);
        option.put("tooltip",tooltip);
        option.put("series",seriesData);

        this.option = option;
        return this;
    }

    /**
     * 多折线/柱状图
     * @param text
     * @return
     */
    public eCharsEngine toParseMoreChars(String text){
        Map option = new ConcurrentHashMap();

        Map title = new ConcurrentHashMap();
        title.put("text",text);

        Map xAxis = new ConcurrentHashMap();
        xAxis.put("type","category");
        xAxis.put("boundaryGap",false);
        xAxis.put("data",this.charsData.get("xAxis-data"));

        Map axisLabel  = new ConcurrentHashMap();
        axisLabel.put("interval",0);
        axisLabel.put("rotate","45");
        xAxis.put("axisLabel",axisLabel);

        Map yAxis = new ConcurrentHashMap();

        yAxis.put("type","value");

        Map legend = new ConcurrentHashMap();
        legend.put("data",this.charsData.get("legend-data"));

        Map tooltip = new ConcurrentHashMap();
        tooltip.put("trigger","axis");

        option.put("title",title);
        option.put("xAxis",xAxis);
        option.put("yAxis",yAxis);
        option.put("tooltip",tooltip);
        option.put("series",this.charsData.get("series-data"));

        this.option = option;
        return this;
    }

    /**
     * 环形图数据处理
     * @param text
     * @param seriesName
     * @return
     */
    public eCharsEngine toParseDoughnut(String text,String seriesName){
        Map option = new ConcurrentHashMap();

        Map title = new ConcurrentHashMap();
        title.put("text",text);
        title.put("left","47.5%");

        Map tooltip = new ConcurrentHashMap();
        tooltip.put("trigger","item");
        tooltip.put("formatter","{a} <br/>{b}: {c} ({d}%)");

        Map legend = new ConcurrentHashMap();
        legend.put("orient","vertical");
        legend.put("x","left");
        legend.put("data",this.charsData.get("legend-data"));

        Map series = new ConcurrentHashMap();
        series.put("name",seriesName);
        series.put("type","pie");

        Map detail = new ConcurrentHashMap();
        detail.put("formatter","{value}%");

        series.put("detail",detail);
        series.put("avoidLabelOverlap",false);

        List<String> radius = new ArrayList<>();
        radius.add("50%");
        radius.add("70%");

        series.put("radius",radius.toArray());
        Map label = new ConcurrentHashMap();

        Map labelNormal = new ConcurrentHashMap();
        labelNormal.put("show",false);
        labelNormal.put("position","center");
        labelNormal.put("formatter","{b}:{d}%");

        Map labelEmphasis = new ConcurrentHashMap();
        labelEmphasis.put("show",true);

        Map textStyle = new ConcurrentHashMap();
        textStyle.put("fontWeight","bold");
        labelEmphasis.put("textStyle",textStyle);

        label.put("normal",labelNormal);
        label.put("emphasis",labelEmphasis);

        series.put("label",label);

        Map labelLine = new ConcurrentHashMap();

        Map labelLineNormal = new ConcurrentHashMap();
        labelLineNormal.put("show",false);

        labelLine.put("normal",labelNormal);

        series.put("labelLine",labelLine);
        series.put("data",this.charsData.get("series-data"));

        option.put("title",title);
        option.put("tooltip",tooltip);
        option.put("legend",legend);
        option.put("series",series);

        this.option = option;
        return this;
    }

    /**
     * 环形图
     * @param legendCol
     * @param seriesDataCol
     * @return
     */
    public  eCharsEngine basicPie(String legendCol,String seriesDataCol){
        this.charsData.put("legend-orient","vertical");
        this.charsData.put("legend-x","left");
        this.charsData.put("series-type","pie");

        List<Object> seriesDatas = new ArrayList<>();
        List<Object> legendData = new ArrayList<>();

        for (BasePage data:this.executeDataList) {
            legendData.add(data.get(legendCol));
            Map<String,Object> m = new ConcurrentHashMap<>();
            m.put("name",data.get(legendCol));
            m.put("value",Double.parseDouble(data.get(seriesDataCol) + ""));
            seriesDatas.add(m);
        }
        this.charsData.put("legend-data",legendData);
        this.charsData.put("series-data",seriesDatas.stream().distinct().collect(Collectors.toList()));
        return this;
    }

    /**
     * 线型图/柱状图
     * @param xDataCol
     * @param seriesDataCol
     * @return
     */
    public eCharsEngine basicLine(String xDataCol,String seriesDataCol){
        List<Object> xAxisData = new ArrayList<>();
        List<Object> seriesData = new ArrayList<>();
        for (BasePage data:this.executeDataList) {
            xAxisData.add(data.get(xDataCol));
            seriesData.add(Double.parseDouble(data.get(seriesDataCol) + ""));
        }

        this.charsData.put("xAxis-data",xAxisData);
        this.charsData.put("series-data",seriesData);
        return this;
    }

    /**
     * 多重线型图/柱状图
     * @param xDataCol
     * @param seriesDataCol
     * @param legendCol
     * @return
     */
    public eCharsEngine moreLine(String xDataCol,String seriesDataCol,String legendCol){
        List<Object> xAxisData = new ArrayList<>();
        List<Object> seriesDatas = new ArrayList<>();
        List<Object> legendData = new ArrayList<>();

        String xAxis = "";
        for (BasePage data:this.executeDataList) {
            List<Object> seriesData = new ArrayList<>();
            legendData.add(data.get(legendCol));

            if (!xAxis.contains(data.get(xDataCol)+"")){
                xAxisData.add(data.get(xDataCol));
                xAxis = xAxis + "," + data.get(xDataCol);
            }
            Map<String,Object> m = new ConcurrentHashMap<>();
            m.put("name",data.get(legendCol));
            m.put("type",this.charsType == 0 ? "line" : "bar");
            List<BasePage> seriesList = this.executeDataList.stream().filter((BasePage execute) -> execute.get(legendCol).equals(data.get(legendCol))).collect(Collectors.toList());
            for (BasePage sl:seriesList){
                seriesData.add(sl.get(seriesDataCol) == null ? 0 : Double.parseDouble(sl.get(seriesDataCol) + ""));
            }
            m.put("data",seriesData);
            seriesDatas.add(m);
        }

        this.charsData.put("xAxis-data",xAxisData);
        this.charsData.put("legend-data",legendData);
        this.charsData.put("series-data",seriesDatas.stream().distinct().collect(Collectors.toList()));
        return this;
    }
}
