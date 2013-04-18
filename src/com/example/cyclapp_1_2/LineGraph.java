/*
 * Author: Jeremy Bouchat
 * Year: 2013
 * Project Report: https://www.dropbox.com/s/8ba5y8kax3lqhz5/Report.docx
 */
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
		tmpSpeeds = (ArrayList<String>) splitSpeeds(spds); // get all speeds
		times = (ArrayList<String>) splitTimes(tms); // get all times
		speeds = new ArrayList<Integer>();
		if (measurement.equals("kph")) { // check if user wants miles or kilometres
			for (int i = 0 ; i < tmpSpeeds.size(); i++) {
				speeds.add((int) (Double.parseDouble((tmpSpeeds.get(i)))* 1.60934));
			}
		} else {
			for (int i = 0 ; i < tmpSpeeds.size(); i++) {
				speeds.add((int) Double.parseDouble((tmpSpeeds.get(i))));
			}
		}
	}

	/**
	 * Get the object's intent.
	 * Setting-up and drawing graph.
	 *
	 * @param  context  The object's context
	 * @return      The object's intent.
	 */
	public Intent getIntent(Context context) {
		TimeSeries series = new TimeSeries("Speeds");

		for (int i = 0; i < speeds.size(); i++) { // add x and y values to points on graph
			series.add(i, speeds.get(i));
		}

		// set up series
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);

		// set up renderer
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.BLACK);
		renderer.setLineWidth(2);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);

		// set up multiple renderer and make is look pretty and all that jazz
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

	private static List<String> splitSpeeds(String source) { // split big string into speeds
		Pattern p = Pattern.compile("\\b([0-9]+)\\.[0-9]+\\b");
		Matcher m = p.matcher(source);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}

	private static List<String> splitTimes(String source) { // split big string into times
		Pattern p = Pattern.compile("\\b(\\d\\d\\:\\d\\d\\:\\d\\d)\\b");
		Matcher m = p.matcher(source);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}



}
