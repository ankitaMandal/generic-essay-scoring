package de.unidue.ltl.eduscoring.testdaf.datasetanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;

public class AnalyseFieldTests {


	public static void main(String[] args) throws UIMAException, IOException{
		//		analyze(System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT00//FT00_standardized.tsv", "FT00");
		//		analyze(System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT01//FT01_standardized.tsv", "FT01");
		List<Task> FT02_Set1 = analyze(System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT02//FT02_Set1_standardized.tsv", "FT02_Set1");
		//		// TODO	analyze(System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT02//FT02_Set2_standardized.tsv", "FT02_Set2");
		List<Task> FT04_Set1 = analyze(System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT04//FT04_Set1_standardized.tsv", "FT04_Set1");
		compareTasks(FT02_Set1, FT04_Set1);
	}

	private static void compareTasks(List<Task> tasklist1, List<Task> tasklist2) {
		for (int i = 0; i<tasklist1.size(); i++){
			tasklist1.get(i).printComparison(tasklist2.get(i));
		}
	}

	//	public static void analyze(String data, String datasetname) throws IOException{
	public static List<Task> analyze(String data, String datasetname) throws IOException{
		List<Task> tasks = new ArrayList<Task>();
		BufferedReader br = new BufferedReader(new FileReader(data));
		String line = br.readLine();
		Task t = null;
		String lastQuestionId = "-1";
		while (line != null){
			if (line.startsWith("Teilnehmer")) {
				line = br.readLine();
			}
			//			System.out.println(line);
			String[] nextItem = line.split("\t");
			if (nextItem.length == 4) {
				String studentId = nextItem[0];
				String questionId = nextItem[1];
				String text = nextItem[2];
				int grade = Integer.parseInt(nextItem[3]);
				boolean correct = false;
				if (grade == 1){
					correct = true;
				} if (grade > 1){
					line = br.readLine();
					continue;
				}
				if (!lastQuestionId.equals(questionId)){
					System.out.println("\nNew prompt");
					if (t != null) { 
						t.printInfo();
						tasks.add(t);
					}
					t  = new Task(datasetname+"_"+questionId);
				}
				lastQuestionId = questionId;
				t.addItem(new LearnerAnswer(questionId, studentId, text, correct));
			} else {
				System.err.println("Unexpected line: "+line);
			}
			line = br.readLine();
		}
		tasks.add(t);
		t.printInfo();
		return tasks;
	}
}
