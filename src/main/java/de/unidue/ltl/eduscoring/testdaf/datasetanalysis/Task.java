package de.unidue.ltl.eduscoring.testdaf.datasetanalysis;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataPhoneticRepresentationReader_FT01;


public class Task {

	String datasetName;
	Collection<LearnerAnswer> learnerAnswers;
	
	
	public Task(String datasetName){
		this.datasetName = datasetName;
		this.learnerAnswers = new ArrayList<LearnerAnswer>();
	}
	
	public void addItem(LearnerAnswer item){
		this.learnerAnswers.add(item);
	}
	
	public double getSolutionProbability(){
		int correct = 0;
		for (LearnerAnswer item : learnerAnswers){
			if (item.isCorrect()){
				correct++;
			}
		}
		return 1.0*correct/learnerAnswers.size();
	}
	

	public double getAverageLengthInTokens(){
		List<Integer> lengths = new ArrayList<Integer>();
		for (LearnerAnswer item: this.learnerAnswers){
			lengths.add(item.getTokens().size());
		}
		return computeAverage(lengths);
	}
	
	public double getAverageLengthInCharacters(){
		List<Integer> lengths = new ArrayList<Integer>();
		for (LearnerAnswer item : learnerAnswers){
			lengths.add(item.getText().length());
		}
		return computeAverage(lengths);
	}

	public int getNumberOfUniqueAnswers(){
		Set<String> uniqueAnswers = new HashSet<String>();
		for (LearnerAnswer item : learnerAnswers){
			uniqueAnswers.add(item.getText());
		}
		return uniqueAnswers.size();
	}
	
	public int getNumberOfAnswers(){
		return this.learnerAnswers.size();
	}
	
	// TODO: this is the simplest way: how many different types and Tokens occur in this dataset
	// can probably done more elaborately.
	public double getTTR(){
		int numTokens = 0;
		Set<String> uniqueTokens = new HashSet<String>();
		for (LearnerAnswer item : learnerAnswers){
			for (String t : item.getTokens()){
				uniqueTokens.add(t);
			}
			numTokens+=item.getTokens().size();
		}
		return 1.0*uniqueTokens.size()/numTokens;
	}
	
	private double computeAverage(List<Integer> lengths) {
		int sum = 0;
		for(Integer length : lengths){
			sum += length; 
		}
		return 1.0*sum/lengths.size();
	}
	
	
	
	public int numberOfSharedUniqueAnswers(Task task2){
		Set<String> uniqueAnswers1 = new HashSet<String>();
		for (LearnerAnswer la : this.learnerAnswers){
			uniqueAnswers1.add(la.getText());
		}
		Set<String> uniqueAnswers2 = new HashSet<String>();
		for (LearnerAnswer la : task2.learnerAnswers){
			uniqueAnswers2.add(la.getText());
		}
		int overlap = 0;
		for (String answer1 : uniqueAnswers1){
			if (uniqueAnswers2.contains(answer1)){
				overlap++;
			}
		}
		return overlap;
	}
	
	// Then umber of answers in this dataset that is also contained in task2
	public int numberOfAnswersCoveredInOtherDataset(Task task2){
		List<String> answers1 = new ArrayList<String>();
		for (LearnerAnswer la : this.learnerAnswers){
			answers1.add(la.getText());
		}
		List<String> answers2 = new ArrayList<String>();
		for (LearnerAnswer la : task2.learnerAnswers){
			answers2.add(la.getText());
		}
		int overlap = 0;
		for (String answer1 : answers1){
			if (answers2.contains(answer1)){
				overlap++;
			}
		}
		return overlap;
	}
	
	
	/*
	 * for testing
	 */
	
	public static void main(String[] args) throws ResourceInitializationException{		
		Task t = new Task("Beispiel");
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biologie", false));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biololie", false));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", true));
		t.printInfo();
		
		Task t2 = new Task("Beispiel2");
		t2.addItem(new LearnerAnswer("dummy", "dummy", "Chemie", true));
		t2.addItem(new LearnerAnswer("dummy", "dummy", "Bio", true));
		t2.addItem(new LearnerAnswer("dummy", "dummy", "Biologie", false));
		t2.addItem(new LearnerAnswer("dummy", "dummy", "Biochemie", false));	
		System.out.println("Number of shared unique answers: "+t.numberOfSharedUniqueAnswers(t2));
		System.out.println("Number of shared unique answers: "+t2.numberOfSharedUniqueAnswers(t));
		System.out.println("Number of Answers also found in other dataset: "+t.numberOfAnswersCoveredInOtherDataset(t2));
		System.out.println("Number of Answers also found in other dataset: "+t2.numberOfAnswersCoveredInOtherDataset(t));
	}

	public void printInfo() {
		System.out.println("TASK: "+this.getName());
		System.out.println("# answers: "+this.getNumberOfAnswers());
		System.out.println("# unique answers: "+this.getNumberOfUniqueAnswers());
		System.out.println("probability of correct solution: "+Math.round(this.getSolutionProbability()*100.0)/100.0);
		System.out.println("average length in characters: "+Math.round(this.getAverageLengthInCharacters()*100.0)/100.0);
		System.out.println("average length in tokens: "+Math.round(this.getAverageLengthInTokens()*100.0)/100.0);
		System.out.println("TTR: "+Math.round(this.getTTR()*100.0)/100.0);	
	}
	
	public void printComparison(Task t2){
		System.out.println("Comapring task "+this.getName()+" and task "+t2.getName());
		System.out.println("Number of shared unique answers: "+this.numberOfSharedUniqueAnswers(t2));
		System.out.println("Number of answers in task 1 also found in task 2: "+this.numberOfAnswersCoveredInOtherDataset(t2));
		System.out.println("Number of answers in task 2 also found in task 1: "+t2.numberOfAnswersCoveredInOtherDataset(this));

	}

	private String getName() {
		return this.datasetName;
	}
	
	
}
