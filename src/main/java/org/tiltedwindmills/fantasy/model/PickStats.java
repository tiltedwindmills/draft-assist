package org.tiltedwindmills.fantasy.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.tiltedwindmills.fantasy.mfl.model.AbstractObject;

import com.google.common.primitives.Doubles;


public class PickStats extends AbstractObject {

	private static final long serialVersionUID = 5862337276486120514L;

	private String franchiseId;

	private String franchiseName;

	private int predrafts;

	private List<Integer> minutesForPicks;

	public double getStandardDeviation() {

		double[] doubles = Doubles.toArray(getMinutesForPicks());
		StandardDeviation sd = new StandardDeviation();
		return sd.evaluate(doubles);
	}

	public String getAverageTime() {

		int sum = 0;

		for (Integer interval : getMinutesForPicks()) {

			if (interval != null) {
				sum += interval;
			}
		}

		double average = (double) sum / (double) getPicksCount();

		StringBuffer returnVal = new StringBuffer();

		int hours = (int) average / 60;
		if (hours > 0) {
			returnVal.append(hours);
			returnVal.append(" hours ");
		}

		int minutes = (int) average % 60;
		if (minutes > 0) {
			returnVal.append(minutes);
			returnVal.append(" minutes");
		}


		return returnVal.toString();
	}

	public int getPicksCount() {
		return getMinutesForPicks().size();
	}

	public String getFranchiseId() {
		return franchiseId;
	}


	public void setFranchiseId(String franchiseId) {
		this.franchiseId = franchiseId;
	}


	public String getFranchiseName() {
		return franchiseName;
	}


	public void setFranchiseName(String franchiseName) {
		this.franchiseName = franchiseName;
	}


	public int getPredrafts() {
		return predrafts;
	}


	public void setPredrafts(int predrafts) {
		this.predrafts = predrafts;
	}


	public List<Integer> getMinutesForPicks() {
		if (minutesForPicks == null) {
			minutesForPicks = new ArrayList<>();
		}
		return minutesForPicks;
	}


	public void setMinutesForPicks(List<Integer> minutesForPicks) {
		this.minutesForPicks = minutesForPicks;
	}
}
