package com.example.cyclapp_1_2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;

public class LineGraph {
	String speedsString, timesString, measurement;
	ArrayList<Integer> speeds;
	ArrayList<String> times;
	Cursor cursor;
	ArrayList<String> tmpSpeeds, tmpTimes;



	public LineGraph(String spds, String tms, String msr) {
		measurement = msr;
		speedsString = spds;
		timesString = tms;
		tmpSpeeds = (ArrayList<String>) splitSpeeds(spds);
		times = (ArrayList<String>) splitTimes(tms);
		speeds = new ArrayList<Integer>();
		if (measurement.equals("kph")) {
			for (int i = 0 ; i < tmpSpeeds.size(); i++) {
				speeds.add((int) (Double.parseDouble((tmpSpeeds.get(i)))* 1.60934));
			}
		} else {
			for (int i = 0 ; i < tmpSpeeds.size(); i++) {
				speeds.add((int) Double.parseDouble((tmpSpeeds.get(i))));
			}
		}
	}

	public Intent getIntent(Context context) {
		TimeSeries series = new TimeSeries("Speeds");

		for (int i = 0; i < speeds.size(); i++) {
			series.add(i, speeds.get(i));
		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);

		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.BLACK);
		renderer.setLineWidth(2);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);


		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setAxesColor(Color.BLACK);
		mRenderer.setXTitle("Time");
		mRenderer.setYTitle("Speed (" + measurement + ")");
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setYLabelsColor(0, Color.BLACK);
		mRenderer.setZoomButtonsVisible(true);

		mRenderer.setXLabels(0); 
		mRenderer.addXTextLabel(0, times.get(0));
		mRenderer.addXTextLabel(((times.size()- 1)/2), times.get((times.size()-1)/2));
		mRenderer.addXTextLabel((times.size()- 1), times.get(times.size()- 1));

		Intent intent = ChartFactory.getLineChartIntent(context, dataset, mRenderer, "Speed/Time");
		return intent;

	}

	public static List<String> splitSpeeds(String source) {
		Pattern p = Pattern.compile("\\b([0-9]+)\\.[0-9]+\\b");
		Matcher m = p.matcher(source);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}

	public static List<String> splitTimes(String source) {
		Pattern p = Pattern.compile("\\b(\\d\\d\\:\\d\\d\\:\\d\\d)\\b");
		Matcher m = p.matcher(source);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}



}
