package application;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SavingsCalculatorApplication extends Application {

    // set constant values here
    private static final int MAX_PERIOD_IN_YEARS = 30;
    private static final int MIN_SAVINGS_PER_MONTH = 0;
    private static final int MAX_SAVINGS_PER_MONTH = 250;

    private Map<Integer, Double> savings;
    private Map<Integer, Double> compoundSavings;
    private int monthlySavings;
    private int interestRate;
    private int years;
    private XYChart.Series savingsSeries;
    private XYChart.Series compoundSavingsSeries;


    @Override
    public void init() throws Exception {
        this.savings = new HashMap<>();
        this.monthlySavings = 25;
        this.interestRate = 5;
        this.years = 10;
        this.savingsSeries = new XYChart.Series<>();
        this.compoundSavingsSeries = new XYChart.Series<>();
        this.savingsSeries.setName("Savings without compound interest");
        this.compoundSavingsSeries.setName("Savings with compound interest");
    }

    @Override
    public void start(Stage stage) {

        // calculate savings without and with compound interest based on default data upon the program start
        this.savings = calculateSavings(this.monthlySavings, 0, this.years);
        this.compoundSavings = calculateSavings(this.monthlySavings, this.interestRate, this.years);

        // generate layouts
        BorderPane mainLayout = new BorderPane();
        VBox slidersLayout = new VBox();
        BorderPane savingsSliderLayout = new BorderPane();
        BorderPane rateSliderLayout = new BorderPane();
        BorderPane yearsSliderLayout = new BorderPane();

        // labels of the sliders (to be located to the left of the corresponding
        // sliders)
        Label savingsSliderLabel = new Label("Monthly savings");
        Label rateSliderLabel = new Label("Yearly interest rate");
        Label yearsSliderLabel = new Label("Years");

        // 'monthly savings' (upper) slider and its properties
        Slider savingsSlider = new Slider(MIN_SAVINGS_PER_MONTH, MAX_SAVINGS_PER_MONTH, this.monthlySavings);
        savingsSlider.setShowTickLabels(true);
        savingsSlider.setShowTickMarks(true);
        savingsSlider.setMajorTickUnit(25);
        savingsSlider.setMinorTickCount(5);
        savingsSlider.setBlockIncrement(1);

        // 'interest rate' (middle) slider and its properties
        Slider interestRateSlider = new Slider(0, 10, this.interestRate);
        interestRateSlider.setShowTickLabels(true);
        interestRateSlider.setShowTickMarks(true);
        interestRateSlider.setMajorTickUnit(1);
        interestRateSlider.setMinorTickCount(0);
        interestRateSlider.setBlockIncrement(1);

        // 'years' (lower) slider and its properties
        Slider yearsSlider = new Slider(0, MAX_PERIOD_IN_YEARS, this.interestRate);
        yearsSlider.setShowTickLabels(true);
        yearsSlider.setShowTickMarks(true);
        yearsSlider.setMajorTickUnit(1);
        yearsSlider.setMinorTickCount(0);
        yearsSlider.setBlockIncrement(1);

        // label displaying current value of the 'monthly savings' slider (to be located
        // to the right of the slider)
        Label savingsValueLabel = new Label(String.valueOf(savingsSlider.getValue()));

        // label displaying current value of the 'interest rate' slider (to be
        // located to the right of the slider)
        Label rateValueLabel = new Label(String.valueOf(interestRateSlider.getValue()));

        // label displaying current value of the 'interest rate' slider (to be
        // located to the right of the slider)
        Label yearsValueLabel = new Label(String.valueOf(yearsSlider.getValue()));

        // X and Y axis for the line chart
        NumberAxis xAxis = new NumberAxis(0, this.years, 1);
        NumberAxis yAxis = new NumberAxis();
        
        // titles for the axes
        xAxis.setLabel("Years");
        yAxis.setLabel("Savings");

        // event listener for change in 'monthly savings' slider. 
        // Upon change: 
        // 1) updates slider text value located to the right;
        // 2) recalculates both 'savings without compound interest' and 'savings with compound interest' datasets
        // 3) updates 'without compound' and 'with compound' linechart series
        savingsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.monthlySavings = newValue.intValue();
            savingsValueLabel.setText(String.valueOf(newValue.intValue()));
            this.savings = calculateSavings(this.monthlySavings, 0, this.years);
            this.compoundSavings = calculateSavings(this.monthlySavings, this.interestRate, this.years);
            populateSeries(this.savings, this.savingsSeries);
            populateSeries(compoundSavings, compoundSavingsSeries);
        });

        // event listener for change in 'monthly savings' slider. 
        // Upon change: 
        // 1) updates slider text value located to the right;
        // 2) recalculates both 'savings without compound interest' and 'savings with compound interest' datasets
        // 3) updates 'without compound' and 'with compound' linechart series
        interestRateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.interestRate = (int)Math.round(newValue.doubleValue());
            interestRateSlider.setValue(this.interestRate);
            rateValueLabel.setText(String.valueOf(this.interestRate));
            this.compoundSavings = calculateSavings(this.monthlySavings, this.interestRate, this.years);
            populateSeries(compoundSavings, compoundSavingsSeries);
        });

        yearsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.years = (int)Math.round(newValue.doubleValue());
            yearsSlider.setValue(this.years);
            yearsValueLabel.setText(String.valueOf(this.years));
            this.savings = calculateSavings(this.monthlySavings, 0, this.years);
            this.compoundSavings = calculateSavings(this.monthlySavings, this.interestRate, this.years);
            xAxis.setUpperBound(this.years);
            populateSeries(this.savings, this.savingsSeries);
            populateSeries(compoundSavings, compoundSavingsSeries);
        });

        // line chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Savings calculator");

        // populate 'savings' and 'compound savings' series with actual data
        populateSeries(this.savings, this.savingsSeries);
        populateSeries(this.compoundSavings, this.compoundSavingsSeries);

        lineChart.getData().add(this.savingsSeries);
        lineChart.getData().add(this.compoundSavingsSeries);

        // setting the UI layout
        savingsSliderLayout.setLeft(savingsSliderLabel);
        savingsSliderLayout.setCenter(savingsSlider);
        savingsSliderLayout.setRight(savingsValueLabel);

        rateSliderLayout.setLeft(rateSliderLabel);
        rateSliderLayout.setCenter(interestRateSlider);
        rateSliderLayout.setRight(rateValueLabel);

        yearsSliderLayout.setLeft(yearsSliderLabel);
        yearsSliderLayout.setCenter(yearsSlider);
        yearsSliderLayout.setRight(yearsValueLabel);


        slidersLayout.getChildren().addAll(savingsSliderLayout, rateSliderLayout, yearsSliderLayout);

        mainLayout.setTop(slidersLayout);
        mainLayout.setCenter(lineChart);

        Scene scene = new Scene(mainLayout, 1024, 768);
        stage.setScene(scene);
        stage.show();

    }

    private Map<Integer, Double> calculateSavings(int monthlySavings, int interestRate, int years) {
        Map<Integer, Double> savings = new HashMap<>();
        double totalSavings = 0;
        for (int i = 0; i <= years; i++) {
            savings.put(i, Double.valueOf(totalSavings));
            totalSavings = (totalSavings + monthlySavings * 12) * (1.0 + interestRate / 100.0);
        }
        return savings;
    }

    private void populateSeries(Map<Integer, Double> data, XYChart.Series series) {
        series.getData().clear();
        data.entrySet().stream().forEach(pair -> {
            series.getData().add(new XYChart.Data(pair.getKey(), pair.getValue()));
        });
    }

    public static void main(String[] args) {
        launch(SavingsCalculatorApplication.class);
    }

}
