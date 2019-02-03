package com.helloingob.accsum.handler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.zkoss.zhtml.Div;
import org.zkoss.zhtml.Script;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Hbox;

import com.helloingob.accsum.data.to.TagTO;
import com.helloingob.accsum.data.to.TransferTO;

public class ChartHandler {

    public static void drawChart(Hbox chartBox, List<TransferTO> transfers, boolean drawAll) {
        chartBox.getChildren().clear();

        Div div = new Div();
        div.setId("container");
        div.setStyle("width:1200px;height:300px;");
        chartBox.appendChild(div);

        Script script = new Script(generateHighchart(calculateTagAmounts(transfers, drawAll)));
        div.appendChild(script);

        Clients.evalJavaScript("draw();");
    }

    private static Map<String, Double> calculateTagAmounts(List<TransferTO> transfers, boolean drawAll) {
        Map<String, Double> tagAmounts = new TreeMap<String, Double>();
        for (TransferTO transfer : transfers) {
            for (TagTO tag : transfer.getTags()) {
                if (drawAll || tag.isVisible()) {
                    Double amount = 0.0;
                    if (tagAmounts.containsKey(tag.getTitle())) {
                        amount = tagAmounts.get(tag.getTitle());
                    }
                    tagAmounts.put(tag.getTitle(), amount - transfer.getAmount());
                }
            }
        }
        return tagAmounts;
    }

    private static String generateHighchart(Map<String, Double> tagAmounts) {
        tagAmounts = sortByValue(tagAmounts);

        String tags = "";
        String values = "";
        for (Map.Entry<String, Double> entry : tagAmounts.entrySet()) {
            tags += "'" + entry.getKey() + "', ";
            values += (entry.getValue() * -1) + ", ";
        }
        if (!tags.isEmpty()) {
            tags = tags.substring(0, tags.length() - 2);
        }
        if (!values.isEmpty()) {
            values = values.substring(0, values.length() - 1);
        }

        // @formatter:off
        return "function draw() {  $('#container').highcharts({ " +
                "chart: { type: 'column' }," +
                "title: { text: null }," +
                "yAxis: { title: { text:null }, labels: { formatter: function () { return this.value + '€';} } }," +
                "credits: { enabled: false }," +
                "plotOptions: { column: { dataLabels: { enabled: true, color: '#95a5a6', formatter: function () { return Highcharts.numberFormat(this.y,2) +'€'; } } } }," + 
                "tooltip: { enabled: true, pointFormat: \"<span style=\\\"color:{point.color}\\\">\\u25CF</span> <b>{point.y:,.2f} €</b><br/>\" }," +
                "legend: { enabled: false }," +
                "xAxis: { categories: [ " + tags  +" ] }," +
                "series: [{ name: 'Spendings', color: '#0093F9', data: [ " + values  +" ] }]" +
                "}); }";
        // @formatter:on
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}
