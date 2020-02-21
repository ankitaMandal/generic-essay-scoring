package de.unidue.ltl.eduscoring.testdaf.experiments.listening;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 
 * Basic script for handling experimental results
 * 
 * TODO
 * this should be handled in a different way:
 * write results directly into a specific folder and evaluate directly after experiment in the same class
 * 
 */


public class TestDafEvaluationHV {


	public static String evaluationFileName = "classification_results.txt";



	public static void main(String[] args) throws IOException{


	//	String[] conditions = {"basic", "phonetic", "basic_10000", "chars_1000"};
	//	String[] conditions = {"basic_teacher1", "basic_teacher2", "basic_adj", "basic_final", "phonetic_teacher1", "phonetic_teacher2", "phonetic_adj", "phonetic_final"};
		
		//FT00
		//String[] conditions = {"basic_adj", "basic_final", "basic_teacher1", "basic_teacher2", "phonetic_adj", "phonetic_final", "phonetic_teacher1", "phonetic_teacher2"};
		//name of folder which contains all files of experiment
		//String testName ="FT00_Weka";
		//String basefile = System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT00/Bewertungen_UTF8_final.tsv";
		
		
		//FT01
//		String[] conditions = {"SZ", "GK", "LS", "Kru", "DM", "gesamt"};
//		//name of folder which contains all files of experiment
//		//String testName ="Results_2019_03_06_FT01_Weka";
//		//String testName ="Results_2019_03_06_FT01_LibSvm";
//		//String testName ="Results_2019_03_06_FT01_Weka_Phon";
//		String testName ="Results_2019_03_06_FT01_LibSvm_Phon";
//		//basefile
//		String basefile = System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT01/FT01_annotiert_final.tsv";
		

		//FT02
		String[] conditions = {"gesamt"};
		//name of folder which contains all files of experiment
		//String testName ="Results_2019_03_06_FT01_Weka";
		//String testName ="Results_2019_03_06_FT01_LibSvm";
		//String testName ="Results_2019_03_06_FT01_Weka_Phon";
		String testName ="Results_2019_06_13_FT02_Set2_evaluation";
		//basefile
		String basefile = System.getenv("DKPRO_HOME")+"/datasets/TestDaf/HV/FT02/Automatic Scoring/Set2.tsv/";
		
		
		// read in base data file in order to extend it later
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(basefile)));
		String line;
		while ((line = br.readLine()) != null){
			lines.add(line);
		}
		br.close();


		String resultsDir = System.getenv("DKPRO_HOME")+"/org.dkpro.lab/TestDafExperiments/"+testName+"/";
		for (String condition : conditions){
			System.out.println("\n\n"+condition);
			
			String label = null;
			File inputFile = new File(resultsDir); 
			File[] fileArray = inputFile.listFiles();
			Double kappa = -1.0;
			Double acc = -1.0;
			List<String> results = new ArrayList<String>();
			Map<String, Map<String, Integer>> individualScores = new HashMap<String, Map<String, Integer>>();
			for(File f:fileArray){
				if (f.isDirectory() && f.getName().startsWith("ExperimentCrossValidation") && f.getName().contains(condition.toString()+"-CV")){
					//Pattern p = Pattern.compile(".*_(\\d+)_.*\\-CV.*");
					Pattern p = Pattern.compile(".*_(\\d+)_.*\\-CV.*");
					Matcher m = p.matcher(f.getName());
					m.matches();
					label = m.group(1);
					File[] resultsFileArray = f.listFiles();   
					for(File file : resultsFileArray){
						if (file.getName().endsWith("combined_classification_results.txt")){
						//if (file.getName().endsWith("classification_results.txt")){
							//label = file.getName().substring(0, file.getName().length()-"classification_results.txt".length()-1);
							br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
							while ((line = br.readLine()) != null){
								if (line.startsWith("accuracy=")){
									String[] parts = line.split("=");
									acc = Double.parseDouble(parts[1].trim());
								}
								if (line.startsWith("quadratically")){
									String[] parts = line.split("=");
									kappa = Double.parseDouble(parts[1].trim());
								}
							}
							br.close();
							results.add(label + "\t" + Math.round(1000.0 * acc) / 1000. + "\t" + Math.round(1000.0 * kappa) / 1000.);
						}
						if (file.getName().equals("combinedId2Outcome.txt")){
							//label = file.getName().substring(0, file.getName().length()-"classification_results.txt".length()-1);
							br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
							while ((line = br.readLine()) != null){
								if (line.startsWith("#")){
									continue;
								} else {
									String[] parts = line.split(";");
									String[] subparts = parts[0].split("=");
									String id = subparts[0];
									String[] idParts = id.split("_");
									String promptId = idParts[2];
									String studentId = idParts[3];
									String prediction = subparts[1];//.substring(2,3);
									//String gold = parts[1];
									System.out.println(promptId+" - "+studentId+": "+prediction);
									if (!individualScores.containsKey(promptId)){
										individualScores.put(promptId, new HashMap<String, Integer>());
									}
									individualScores.get(promptId).put(studentId, Integer.parseInt(prediction));
								}
							}
							//System.out.println(individualScores.get("50.4").size());
						}
					}				
				}			
			}		
		
		
		
		
//		for (String condition : conditions){
//			System.out.println("\n\n"+condition);
//			//String resultsDir = System.getenv("DKPRO_HOME")+"/org.dkpro.lab/TestDafExperiments/"+condition+"/";
//			String label = null;
//			File inputFile = new File(resultsDir);
//			File[] fileArray = inputFile.listFiles();    
//			Double kappa = -1.0;
//			Double acc = -1.0;
//			List<String> results = new ArrayList<String>();
//			Map<String, Map<String, Integer>> individualScores = new HashMap<String, Map<String, Integer>>();
//			for(File f:fileArray){
				
//				if (f.isDirectory() && f.getName().startsWith("Evaluation")){
//					Pattern p = Pattern.compile(".*_(\\d+)_.*\\-CV.*");
//					Matcher m = p.matcher(f.getName());
//					m.matches();
//					label = m.group(1);
//					File[] resultsFileArray = f.listFiles();   
//					for(File file : resultsFileArray){
//						if (file.getName().endsWith("classification_results.txt")){
//							//label = file.getName().substring(0, file.getName().length()-"classification_results.txt".length()-1);
//							br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
//							while ((line = br.readLine()) != null){
//								if (line.contains("accuracy")){
//									String[] parts = line.split("=");
//									acc = Double.parseDouble(parts[1].trim());
//								}
//								if (line.contains("quadratically weighted kappa")){
//									String[] parts = line.split("=");
//									kappa = Double.parseDouble(parts[1].trim());
//								}
//							}
//							br.close();
//							results.add(label + "\t" + Math.round(1000.0 * acc) / 1000. + "\t" + Math.round(1000.0 * kappa) / 1000.);
//						}
//					}
//				}
//				
//				if (f.isDirectory() && f.getName().startsWith("ExperimentCrossValidation")){
//					File[] resultsFileArray = f.listFiles();   
//					Pattern p = Pattern.compile(".*_(\\d+)_.*\\-CV.*");
//					Matcher m = p.matcher(f.getName());
//					m.matches();
//					label = m.group(1);
//					for(File file : resultsFileArray){
//						if (file.getName().equals("combinedId2Outcome.txt")){
//							//label = file.getName().substring(0, file.getName().length()-"classification_results.txt".length()-1);
//							br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
//							while ((line = br.readLine()) != null){
//								if (line.startsWith("#")){
//									continue;
//								} else {
//									String[] parts = line.split(";");
//									String[] subparts = parts[0].split("=");
//									String id = subparts[0];
//									String[] idParts = id.split("_");
//									String promptId = idParts[2];
//									String studentId = idParts[3];
//									String prediction = subparts[1].substring(2,3);
//									String gold = parts[1].substring(2,3);
//									System.out.println(promptId+" - "+studentId+": "+prediction);
//									if (!individualScores.containsKey(promptId)){
//										individualScores.put(promptId, new HashMap<String, Integer>());
//									}
//									individualScores.get(promptId).put(studentId, Integer.parseInt(prediction));
//								}
//							}
//						}						
//					}
//				}				
//			}
			
			BufferedWriter outResults = new BufferedWriter(new FileWriter(resultsDir + condition + ".txt"));
			Collections.sort(results);
			results.add(0,condition + "\t accuracy \t QWK" );
			for (String result : results){
				// for my German google doc
				//System.out.println(result.replaceAll("\\.", ","));
				outResults.write(result.replaceAll("\\.", ",")+"\n");
			}
			outResults.close();
			
			// add results to original input file
			ArrayList<String> linesNew = new ArrayList<String>();
			for (String scoreLine : lines){
				scoreLine = scoreLine.trim();
				String[] parts = scoreLine.split("\t");
				String promptId = parts[1]+"."+parts[2];
				String studentId = parts[0];
				System.out.println(promptId+" - "+studentId+": ");
				if (parts.length == 9){
					scoreLine+="\t";
				}
				if (scoreLine.startsWith("Teilnehmer")){
					scoreLine +="\t"+condition;
				}
				else if (individualScores.containsKey(promptId) && individualScores.get(promptId).containsKey(studentId)){
					scoreLine+="\t"+individualScores.get(promptId).get(studentId);
				} else {
					scoreLine+="\t";
				}
				linesNew.add(scoreLine);
			}
			lines = linesNew;		
			
		}		
		
		BufferedWriter out = new BufferedWriter(new FileWriter(resultsDir + "scoreLines.txt"));
		for (String scoreLine: lines){
		//	System.out.println(scoreLine);
			out.write(scoreLine+"\n");
		}
		out.close();
	}
}
