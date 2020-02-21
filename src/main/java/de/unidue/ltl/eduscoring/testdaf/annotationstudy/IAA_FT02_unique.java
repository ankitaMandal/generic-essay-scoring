package de.unidue.ltl.eduscoring.testdaf.annotationstudy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import de.unidue.ltl.evaluation.core.EvaluationData;
import de.unidue.ltl.evaluation.measures.agreement.QuadraticallyWeightedKappa;

public class IAA_FT02 {


	//TODO: not ready yet
	
	public static void main (String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException{
		String filePath = System.getenv("DKPRO_HOME") + "/datasets/TestDaF/HV/FT01/Annotationsstudie/Gesamt";
		
		File folder = new File(filePath); 
		File[] files = folder.listFiles();
		Arrays.sort(files);
		for (File file : files) {
			if (file.getName().endsWith("xls")){
				System.out.println(file.getName()+"\t");
				processFile(file.getAbsolutePath());
			}
		}
		for (String key : itemsPerPrompt.keySet()){
			System.out.println(key+"\t"+itemsPerPrompt.get(key));
		}
	}

	private static Map<String, Integer> itemsPerPrompt = new HashMap<String, Integer>();


	private static void processFile(String filePath) throws EncryptedDocumentException, InvalidFormatException, IOException {
		Workbook workbook = WorkbookFactory.create(new File(filePath));
		Sheet sheet = workbook.getSheetAt(0);

		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();

		ArrayList<Integer> sz = new ArrayList<Integer>();
		ArrayList<Integer> gk = new ArrayList<Integer>();
		ArrayList<Integer> ls = new ArrayList<Integer>();
		ArrayList<Integer> kru = new ArrayList<Integer>();
		ArrayList<Integer> dm = new ArrayList<Integer>();
		ArrayList<Integer> gesamt = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> all = new ArrayList<ArrayList<Integer>>();

		int numAll = 0;
		int numAgree = 0;
		int numHalfHalf = 0;
		int numLSChanges = 0;

		for (Row row: sheet) {
			String content = "";
			for(Cell cell: row) {
				String cellValue = dataFormatter.formatCellValue(cell);
				content += cellValue + "\t";
			}
			content = content.trim();
			if (content.startsWith("Teilnehmer")){
				//nix
			} else {
				String[] parts = content.split("\t");
				if (parts.length == 10){
					if ((!parts[4].equals(""))
							&& (!parts[5].equals(""))
							&& (!parts[6].equals(""))
							&& (!parts[7].equals(""))
							&& (!parts[8].equals(""))
							&& (!parts[9].equals(""))
							){
						numAll++;
						int anno_sz = Integer.parseInt(parts[4]); 
						int anno_gk = Integer.parseInt(parts[5]); 
						int anno_ls = Integer.parseInt(parts[6]); 
						int anno_kru = Integer.parseInt(parts[7]); 
						int anno_dm = Integer.parseInt(parts[8]); 
						int anno_gold = Integer.parseInt(parts[9]); 
						sz.add(anno_sz);
						gk.add(anno_gk);
						ls.add(anno_ls);
						kru.add(anno_kru);
						dm.add(anno_dm);
						gesamt.add(anno_gold);
						if (anno_ls != anno_gold){
							numLSChanges++;
							System.out.println("gold: "+content);
						}
						if (anno_sz == anno_gk && anno_sz == anno_kru && anno_sz ==anno_dm){
							numAgree++;
						}
						if (
								(anno_sz == anno_gk && anno_sz != anno_kru && anno_sz != anno_dm)
								||	(anno_sz != anno_gk && anno_sz == anno_kru && anno_sz != anno_dm)
								||	(anno_sz != anno_gk && anno_sz != anno_kru && anno_sz == anno_dm)
								){
							numHalfHalf++;
							System.out.println("2:2: "+content);
						}
					}
				} else {
					//		System.err.println("Problem in line: "+content);
				}
			}
			//System.out.println(content);
		}
		//	System.out.println(gesamt.size()+" items for this prompt");
		itemsPerPrompt.put(filePath.substring(filePath.lastIndexOf("/")+1), gesamt.size());
		//		System.out.println();
		// TODO: Hier IAA parweise zwischen den Annotatoren einbauen
		all.add(sz);
		all.add(gk);
		all.add(ls);
		all.add(kru);
		all.add(dm);
		all.add(gesamt);
		ArrayList<String> names = new ArrayList<String>();
		names.add("sz");
		names.add("gk");
		names.add("ls");
		names.add("kru");
		names.add("dm");
		names.add("gesamt");
		for (int i = 0; i<all.size()-1; i++){
			for (int j = 1; j<all.size(); j++){
				if (i!=j){
					//					System.out.print(names.get(i)+" - "+names.get(j)+":\t");
					//					computeAgreement(all.get(i), all.get(j));
				}
			}
		}
		computeDifficulty(gesamt);
		System.out.println("Gesamt: "+numAll);
		System.out.println("Not all agree: "+(1.0-numAgree/(numAll*1.0)));
		System.out.println("2:2: "+numHalfHalf/(numAll*1.0));
		System.out.println("Gold standard changes "+numLSChanges/(numAll*1.0));	
		workbook.close();
	}

	private static void computeDifficulty(ArrayList<Integer> gesamt) {
		int sum = 0;
		for (int score: gesamt){
			sum+=score;
		}
	//	System.out.println("Probability of  correct solution: "+1.0*sum/gesamt.size());
	}

	private static void computeAgreement(ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2) {
		EvaluationData<Integer> evalData = new EvaluationData<Integer>();
		for (int i = 0; i<arrayList.size(); i++){
			evalData.register(arrayList.get(i), arrayList2.get(i));
		}
		QuadraticallyWeightedKappa qwk = new QuadraticallyWeightedKappa(evalData);
		System.out.println(qwk.getResult());
	}


}
