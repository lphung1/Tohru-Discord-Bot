package Services;

import io.quickchart.QuickChart;

public class ChartService {

    // TODO when monitoring services are set up, implement methods
    public String getLineChart(int i) {
        QuickChart chart = new QuickChart();
        chart.setWidth(500);
        chart.setHeight(300);
        chart.setConfig("{"
                + "    type: 'bar',"
                + "    data: {"
                + "        labels: ['Q1', 'Q2', 'Q3', 'Q4'],"
                + "        datasets: [{"
                + "            label: 'Users',"
                + "            data: [50, 60, 70, 180]"
                + "        }]"
                + "    }"
                + "}"
        );

        return chart.getUrl();

    }

}
