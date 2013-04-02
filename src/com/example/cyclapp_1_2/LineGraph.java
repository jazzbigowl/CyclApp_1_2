package com.example.cyclapp_1_2;

import java.sql.Array;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

public class LineGraph {
	String speedsString;
	ArrayList<Integer> speeds;
	Cursor cursor;
	ArrayList<String> tmp;



	public LineGraph(String spds) {
		speedsString = spds;
		tmp = (ArrayList<String>) splitSpeeds(spds);
		speeds = new ArrayList<Integer>();
		for (int i = 0 ; i < tmp.size(); i++) {
			speeds.add((int) Double.parseDouble((tmp.get(i))));
		}
		//speeds = tmp.toArray();
	}

	public Intent getIntent(Context context) {


		int[] x = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		int[] y = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

		TimeSeries series = new TimeSeries("Speeds");
//		for (int i = 0; i < speeds.length; i++) {
//			series.add(speeds[i], i);
//		}
		
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
		mRenderer.setYTitle("Speed");
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setYLabelsColor(0, Color.BLACK);

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

}
