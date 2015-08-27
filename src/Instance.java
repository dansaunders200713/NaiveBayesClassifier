
public class Instance {
	Boolean classValue;
	Boolean[] voteValues;
	double repProb;
	double demProb;
	
	public Instance(String line) {
		String[] votes = line.split(",");
		classValue = (votes[0].equals("republican"));
		voteValues = new Boolean[votes.length-1];
		for (int i = 1; i < votes.length; i++) {
			if(votes[i].equals("?")) {
				voteValues[i-1] = null;
			}
			else {
				voteValues[i-1] = (votes[i].equals("y"));
			}
		}
	}
	
	public Instance(String line, int zero) {
		String[] votes = line.split(",");
		voteValues = new Boolean[votes.length];
		for (int i = 0; i < votes.length; i++) {
			if(votes[i].equals("?")) {
				voteValues[i] = null;
			}
			else {
				voteValues[i] = (votes[i].equals("y"));
			}
		}
	}
	
	public void normalize() {
		double sum = repProb + demProb;
		repProb = repProb / sum;
		demProb = demProb / sum;
	}
	
	public String toString() {
		String toStr = "";
		if (repProb >= 0.5) {
			toStr += "republican," + repProb;
		}
		else {
			toStr += "democrat," + demProb;
		}
		return toStr;
	}
}
