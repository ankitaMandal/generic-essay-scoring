package de.unidue.ltl.eduscoring.genericessayscoring;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;

import de.unidue.ltl.eduscoring.genericessayscoring.experiments.Experiments_ImplBase;
import de.unidue.ltl.eduscoring.genericessayscoring.experiments.FeatureSettings;
import de.unidue.ltl.eduscoring.genericessayscoring.io.GenericDatasetReader;

public class GenericEssayExperiments extends Experiments_ImplBase implements Constants {

	/*
	 * Experimental code should go in here. Can be analogous to
	 * de.unidue.ltl.escrito.examples.basicexamples.shortanswer.
	 * WekaClassificationCvExample and WekaClassificationCvExample
	 * 
	 * - use GenericReader (prepare basic data accordingly) - make DKPRO_HOME folder
	 * (see https://zoidberg.ukp.informatik.tu-darmstadt.de/jenkins/job/DKPro%20%
	 * 20Documentation%20%28GitHub%29/org.dkpro.tc%24dkpro-tc-doc/doclinks/1/ ) -
	 * run experiment with 10000 1-3grams - CV within prompt - also train/test
	 * between prompts - collect accuracy/kappa and confusion matrixes - prepare
	 * excel sheet (shared google spreadsheet) for results
	 * 
	 */
	public static void main(String[] args) throws Exception {
		
		 runEssayGradingBaselineExperiment(
		   "BaselineExperiment_EssayScoring_ICNALE_all_Features", 
		   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
		   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
		  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_noX.txt", 
			"en",4);
		 
		 runEssayGradingBaselineExperiment(
			   "BaselineExperiment_EssayScoring_ICNALE_PTJ_Features", 
			   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
			   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
			  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_PTJ_noX.txt", 
				"en",4);
		 
		 runEssayGradingBaselineExperiment(
			   "BaselineExperiment_EssayScoring_ICNALE_SMK_Features", 
			   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
			   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
			  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_SMK_noX.txt", 
				"en",4);
		

//		 runEssayGradingBaselineExperiment(
//		   "BaselineExperiment_EssayScoring_TestDaf_all_Features", 
//		   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//		   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//		  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/TestdafAllFT.txt", 
//			"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT02all_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all.txt", 
//					"de",4);
//				 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT03all_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_all_int.txt", 
//					"de",4);
//				 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT04all_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_all_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT02_1-1_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_1-1.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT02_1-2_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_1-2.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT02_2-1_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_2-1.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT02_2-2_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_2-2.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT03_3-1_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_3-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT03_3-2_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_3-2_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT03_4-1_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_4-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT03_4-2_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_4-2_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT04_1-1_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_1-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT04_1-2_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_1-2_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT04_5-1_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_5-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				   "BaselineExperiment_EssayScoring_TestDaf_FT04_5-2_Features", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				   //System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_5-2_int.txt", 
//					"de",4);
				 
		
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Falkoall_Feartures", 
//				   "BaselineExperiment_EssayScoring_Example_FALKO_all_Features", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_PTJ_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_FALKO_11_Features",  
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt",  
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_FALKO_12_Features",  
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt", 
//					"de",4);
//
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_PTJ_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_FALKO_13_Features", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt",  
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_FALKO_14_Features",  
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt", 
//					"de",4);
		 //		 
//		runEssayGradingBaselineExperiment(
//				// "BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams",
//				"BaselineExperiment_EssayScoring_Example_Falko_Test",
//				// System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt",
//				// System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt",
//				System.getenv("DKPRO_HOME") + "/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_3-1_neu.txt", "de", 4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_Test", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_1-1.txt", 
//					"de",4);
		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_TestdafTest_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_02_2-2_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_2-2.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_03_3-1_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_3-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_03_3-2_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_3-2_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_03_4-1_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_4-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_03_4-2_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_4-2_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_04_1-1_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_1-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_04_1-2_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_1-2_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_TestDaf_04_5-1_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				  System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_5-1_int.txt", 
//					"de",4);
//		 
//		 runEssayGradingBaselineExperiment(
//				 //"BaselineExperiment_EssayScoring_Example_ICNALE_SMK_NGrams", 
//				   "BaselineExperiment_EssayScoring_Example_ICNALE_PTJ_NGrams", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//				//System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/ICNALE_SMK.txt", 
//					System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_PTJ.txt", 
//					"de",4);
//		 
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_PTJ_on_SMK", 
//			System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_PTJ_noX.txt", 
//			System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_SMK_noX.txt",
//			"en",2);
//		 
//		 runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_SMK_on_PTJ", 
//			System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_SMK_noX.txt", 
//			System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/ICNALE/ICNALE_PTJ_noX.txt",
//			"en",2);
//		 		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Falkoall_on_Testdafall", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/TestdafAllFT_Umlautslos.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdafall_on_Falkoall", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/TestdafAllFT_Umlautslos.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf02_on_Falko11", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all_Umlautlos.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Falko11_on_Testdaf02", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all_Umlautlos.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf03_on_Falko13", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_Umlautslos.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Falko13_on_Testdaf03", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_Umlautslos.txt",
//				"de",2);

//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Falko_no11", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_no11.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt",
//				"de",2);
//
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Falko_no12", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_no12.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Falko_no13", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_no13.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Falko_no14", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_no14.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_no02", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/inter/TestdafFT_no02.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_no03", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/inter/TestdafFT_no03.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_all_int.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_no04", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/inter/TestdafFT_no04.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_all_int.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_02_on_03", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_all_int.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_02_on_04", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_all_int.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_03_on_02", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_all_int.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_03_on_04", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_all_int.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_all_int.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_04_on_02", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_all_int.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT02/Bereinigt/TestdafFT02_all.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Testdaf_04_on_03", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT04/Bereinigt/TestdafFT04_all_int.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/TestDaf/FT03/Bereinigt/TestdafFT03_all_int.txt",
//				"de",2);
	
		 
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_11_on_12", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt",
//				"de",2);
//			
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_11_on_13", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt", 					
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt",
//				"de",2);	
//		 
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_11_on_14", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_12_on_11", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_12_on_13", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_12_on_14", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_13_on_11", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_13_on_12", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_13_on_14", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_14_on_11", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_11.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_14_on_12", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_12.txt",
//				"de",2);
//		
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_FALKO_14_on_13", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_14.txt", 
//				System.getenv("DKPRO_HOME")+"/datasets/GenericAES/GenericAES/FALKO/FalkoEssays_13.txt",
//				"de",2);
		 
//		 runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Example", 
//					System.getenv("DKPRO_HOME")+"/datasets/EssaysBinCas/1-2/", 
//						"de",4);

	}

	// CV within prompt
	protected static void runEssayGradingBaselineExperiment(String experimentName, String trainData,
			String languageCode, Integer... questionIds) throws Exception {
		System.out.println(trainData);

		CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
				GenericDatasetReader.class, GenericDatasetReader.PARAM_INPUT_FILE, trainData,
				GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true, GenericDatasetReader.PARAM_ENCODING, "UTF-8",
				GenericDatasetReader.PARAM_LANGUAGE, languageCode, GenericDatasetReader.PARAM_SEPARATOR, "\t",
				GenericDatasetReader.PARAM_QUESTION_PREFIX, "Q", GenericDatasetReader.PARAM_TARGET_ANSWER_PREFIX, "TA",
				GenericDatasetReader.PARAM_CORPUSNAME, "dummy"

		);

//		  CollectionReaderDescription readerTrain =
//		  CollectionReaderFactory.createReaderDescription( BinaryCasReader.class,
//		  BinaryCasReader.PARAM_SOURCE_LOCATION,trainData,
//		  BinaryCasReader.PARAM_LANGUAGE, "de", BinaryCasReader.PARAM_PATTERNS, "*.bin"
//		 );

		runBaselineExperiment(experimentName + "_" + 1 + "", readerTrain, languageCode);

	}

	// Overloaded method to train test across prompts
	protected static void runEssayGradingBaselineExperiment(String experimentName, String trainData, String testData,
			String languageCode, Integer... questionIds) throws Exception {
		System.out.println(trainData);
		System.out.println(testData);

		CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
				GenericDatasetReader.class, GenericDatasetReader.PARAM_INPUT_FILE, trainData,
				GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true, GenericDatasetReader.PARAM_ENCODING, "UTF-8",
				GenericDatasetReader.PARAM_LANGUAGE, languageCode, GenericDatasetReader.PARAM_SEPARATOR, "\t",
				GenericDatasetReader.PARAM_QUESTION_PREFIX, "Q", GenericDatasetReader.PARAM_TARGET_ANSWER_PREFIX, "TA",
				GenericDatasetReader.PARAM_CORPUSNAME, "dummy"
		// GenericDatasetReader.PARAM_EXCLUDE_PROMPT_SET_ID,"1-1"

		);
		CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(
				GenericDatasetReader.class, GenericDatasetReader.PARAM_INPUT_FILE, testData,
				GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true, GenericDatasetReader.PARAM_ENCODING, "UTF-8",
				GenericDatasetReader.PARAM_LANGUAGE, languageCode, GenericDatasetReader.PARAM_SEPARATOR, "\t",
				GenericDatasetReader.PARAM_QUESTION_PREFIX, "Q", GenericDatasetReader.PARAM_TARGET_ANSWER_PREFIX, "TA",
				GenericDatasetReader.PARAM_CORPUSNAME, "dummy"

		);

		// new
//				Map<String, Object> dimReaders = new HashMap<String, Object>();
//				dimReaders.put(DIM_READER_TRAIN, readerTrain);
//				dimReaders.put(DIM_READER_TRAIN, readerTest);
//				
//				Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
//				Dimension<Map<String, Object>> learningsArgsDims = getWekaClassificationArgsDim();
//
//				ParameterSpace pSpace = null;
//				pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders), learningDims,
//						Dimension.create(DIM_FEATURE_MODE, FM_UNIT), FeatureSettings.getFeatureSetsDimBaseline(),
//						learningsArgsDims);
		// end new
		runBaselineExperiment(experimentName + "_" + 1 + "", readerTrain, readerTest, languageCode);

	}

	// Overloaded method to train test across prompts
	private static void runBaselineExperiment(String experimentName, CollectionReaderDescription readerTrain,
			CollectionReaderDescription readerTest, String languageCode) throws Exception {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		dimReaders.put(DIM_READER_TEST, readerTest);

		// Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE,
		// LM_REGRESSION);
		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		Dimension<Map<String, Object>> learningsArgsDims = getWekaClassificationArgsDim();
		// Dimension<TcFeatureSet> learningsArgsDims = getVeryBasicFeatureSetsDim();

		ParameterSpace pSpace = null;
		pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders), learningDims,
				Dimension.create(DIM_FEATURE_MODE, FM_UNIT), FeatureSettings.getFeatureSetsEssayFull(languageCode),
				learningsArgsDims);
		runTrainTest(pSpace, experimentName, getPreprocessing(languageCode));
	}

	// CV within prompt
	private static void runBaselineExperiment(String experimentName, CollectionReaderDescription readerTrain,
			String languageCode) throws Exception {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);

		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		Dimension<Map<String, Object>> learningsArgsDims = getWekaClassificationArgsDim();

		ParameterSpace pSpace = null;
		pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders), learningDims,
				Dimension.create(DIM_FEATURE_MODE, FM_UNIT), FeatureSettings.getFeatureSetsEssayFull(languageCode),
				learningsArgsDims);
		runCrossValidation(pSpace, experimentName, getPreprocessing(languageCode), 10);
		// runCrossValidation(pSpace, experimentName, getPreprocessingDummy(), 10);
	}

}