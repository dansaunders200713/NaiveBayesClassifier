import java.util.*;
import java.io.*;

public class NaiveBayesClassifier {
	ArrayList<Instance> trainInstances;
	ArrayList<Instance> testInstances;
	double repubProb = 1.0;
	double demoProb = 1.0;
	double[] repVoteProbs;
	double[] demVoteProbs;
	double[] yesProb;
	int rep, dem;
	int length;
	
	@SuppressWarnings(value = { "", "unchecked" })
	public NaiveBayesClassifier(ArrayList<ArrayList<String>> input) {
		getTrainingData(input.get(0));
		getTestingData(input.get(1));
		String output = "";
		for (Instance test : testInstances) {
			output += test.toString() + "\n";
		}
		System.out.println(output);
	}
	
	public static void main(String[] args) throws IOException { 
		NaiveBayesClassifier nbc = new NaiveBayesClassifier(parseInput(args[0], args[1]));
	}
	
	public static ArrayList<ArrayList<String>> parseInput(String train, String test) {
		try {
			File trainFile = new File(train);
			BufferedReader br = new BufferedReader(new FileReader(trainFile));
			ArrayList<String> trainingData = new ArrayList<String>();
			String line;
			while ((line = br.readLine()) != null) {
				trainingData.add(line);
			}
			
			File testFile = new File(test);
			BufferedReader br2 = new BufferedReader(new FileReader(testFile));
			ArrayList<String> testingData = new ArrayList<String>();
			while ((line = br2.readLine()) != null) {
				testingData.add(line);
			}
			ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
			input.add(trainingData);
			input.add(testingData);
			return input;
			}
		catch (IOException ex) {
			System.exit(0);
		}
		return null;
	}
	
	public void getTrainingData(ArrayList<String> train) {
		trainInstances = new ArrayList<Instance>();
		rep = 0;
		dem = 0;
		for (String instance : train) {
			Instance i = new Instance(instance);
			length = i.voteValues.length;
			trainInstances.add(i);
			if (i.classValue) {
				rep++;
			} else {
				dem++;
			}
		}
		
		if (rep == 0) {
			repubProb = Math.log(((double)1.0/trainInstances.size()));
		}
		else {
			repubProb = Math.log(((double)rep/trainInstances.size()));
		}
		if (dem == 0) {
			demoProb = Math.log(((double)1.0/trainInstances.size()));
		}
		else{
			demoProb = Math.log(((double)dem/trainInstances.size()));
		}
		
		repVoteProbs = new double[length];
		demVoteProbs = new double[length];
		yesProb = new double[length];
		for (int i = 0; i < length; i++) {
			int repYes = 0;
			int demYes = 0;
			for (Instance instance : trainInstances) {
				if (instance.voteValues[i] != null && instance.voteValues[i] && instance.classValue) {
					repYes++;
				}
				else {
					if (instance.voteValues[i] != null && instance.voteValues[i] && !instance.classValue) {
						demYes++;
					}
				}
			}
			
			repVoteProbs[i] = (double)repYes / rep;
			demVoteProbs[i] = (double)demYes / dem;
			if (repYes + demYes > 0) {
				yesProb[i] = (double)(repYes + demYes)/ trainInstances.size();
			}
			else {
				yesProb[i] = (double)1 / trainInstances.size();
			}
		}
	}
	
	public void getTestingData(ArrayList<String> test) {
		testInstances = new ArrayList<Instance>();
		for (String instance : test) {
			Instance i = new Instance(instance, 0);
			calculateRepProb(i);
			calculateDemProb(i);
			i.normalize();
			testInstances.add(i);
		}
	}
	
	public void calculateRepProb(Instance test) {
		double conditionProb = Math.log(1.0);
		double[] probs = new double[length];
		for (int i = 0; i < length; i++) {
			if (test.voteValues[i] == null) {
				probs[i] = -1.0;
				continue;
			}
			if (test.voteValues[i] == true) {
				if (repVoteProbs[i] > 0.0) {
					probs[i] = Math.log(repVoteProbs[i]);
				}
				else {
					probs[i] = Math.log((double)1.0 / trainInstances.size());
				}
			}
			else {
				if (repVoteProbs[i] == 1.0) {
					probs[i] = Math.log((double)1.0 / trainInstances.size());
				}
				else {
					probs[i] = Math.log(1.0 - repVoteProbs[i]);
				}
			}
		}
		for (double prob : probs) {
			if (prob != -1.0) conditionProb += prob;
		}
		test.repProb = Math.exp(repubProb) * Math.exp(conditionProb);
	}
	
	public void calculateDemProb(Instance test) {
		double conditionProb = Math.log(1.0);
		double probs[] = new double[length];
		for (int i = 0; i < length; i++) {
			if (test.voteValues[i] == null) {
				probs[i] = -1.0;
				continue;
			}
			if (test.voteValues[i] == true) { // P(X = y | Class)
				if (demVoteProbs[i] > 0.0) {
					probs[i] = Math.log(demVoteProbs[i]);
				}
				else {
					probs[i] = Math.log((double)1.0 / trainInstances.size());
				}
			}
			else { // P(X = n | Class)
				if (demVoteProbs[i] == 1.0) {
					probs[i] = Math.log((double)1.0 / trainInstances.size());
				}
				else {
					probs[i] = Math.log(1.0 - demVoteProbs[i]);
				}
			}
		}
		for (double prob : probs) {
			if (prob != -1.0) conditionProb += prob;
		}
		test.demProb = Math.exp(demoProb) * Math.exp(conditionProb);
	}
}
