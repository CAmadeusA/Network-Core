package com.camadeusa.utility.command.prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SRegex {
	public static final String ANSI_RESET = "\033[0m";
	public static final String ANSI_GREEN = "\033[42m";
	private Pattern regex;

	private String result;

	private int numResults;

	private ArrayList<String> results;

	public SRegex() {
		this.result = "";
		this.numResults = 0;
		this.results = new ArrayList();
	}

	public void find(String sample, String regex) {
		this.results = new ArrayList();
		String tempSample = sample;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(tempSample);
		this.regex = p;

		while (m.find()) {
			String result = m.group();
			this.results.add(result);
			tempSample = tempSample.substring(tempSample.indexOf(result) + result.length());
			m = p.matcher(tempSample);
		}

		if (this.results.size() < 1) {
			System.out.println("No result found");
		} else {
			this.numResults = this.results.size();
		}
	}

	public void find(String regex) {
		find(getDefaultSample(), regex);
	}

	public void test(String sample, String regex) {
		String tempSample = sample;
		find(regex);
		for (String string : this.results) {
			tempSample = tempSample.replace(string, "\033[42m" + string + "\033[0m");
		}
		this.result = tempSample;
		System.out.println(this);
	}

	public void test(String regex) {
		test(getDefaultSample(), regex);
	}

	public List<String> getResults() {
		return this.results;
	}

	public Pattern getRegex() {
		return this.regex;
	}

	public String getDefaultSample() {
		return "Sample text for testing:\nabcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789 _+-.,!@#$%^&*();\\/|<>\"'\n12345 -98.7 3.141 .6180 9,000 +42\n555.123.4567\t+1-(800)-555-2468\n\n[ABC] [abc] [Abc] [AbC]\n\n<ABC> <abc> <Abc> <AbC>\n\nfoo@demo.net\tbar.ba@test.co.uk\n";
	}

	public String toString() {
		String out = "Number of results: " + this.numResults + "\n\nResults: " + getResults();
		out = out + "\n\n" + this.result;
		return out;
	}
}
