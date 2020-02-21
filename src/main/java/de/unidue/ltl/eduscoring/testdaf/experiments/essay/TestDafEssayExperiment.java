package de.unidue.ltl.eduscoring.testdaf.experiments.essay;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.core.Constants;

import de.unidue.ltl.eduscoring.testdaf.experiments.Experiments_ImplBase;
import de.unidue.ltl.eduscoring.testdaf.experiments.FeatureSettings;
import de.unidue.ltl.eduscoring.testdaf.io.GenericDatasetReader;


public class TestDafEssayExperiment extends Experiments_ImplBase implements Constants  {

	/*
	 * Experimental code should go in here. 
	 * Can be analogous to 
	 * de.unidue.ltl.escrito.examples.basicexamples.shortanswer.WekaClassificationCvExample 
	 * and WekaClassificationCvExample           
	 * 
	 * - use GenericReader (prepare basic data accordingly)
	 * - make DKPRO_HOME folder (see https://zoidberg.ukp.informatik.tu-darmstadt.de/jenkins/job/DKPro%20%20Documentation%20%28GitHub%29/org.dkpro.tc%24dkpro-tc-doc/doclinks/1/ )
	 * - run experiment with 10000 1-3grams
	 * - CV within prompt
	 * - also train/test between prompts
	 * - collect accuracy/kappa and confusion matrixes
	 * - prepare excel sheet (shared google spreadsheet) for results
	 * 
	 */
	public static void main(String[] args) throws Exception{
		 runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Example", 
			System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/2-1.txt", 
				"de",2);
//		runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Example", 
//				System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/alle.txt", 
//					System.getenv("DKPRO_HOME")+"/datasets/Experimental_Datasets_EssayScoring/1-1.txt",
//				"de",2);
		 
//		 runEssayGradingBaselineExperiment("BaselineExperiment_EssayScoring_Example", 
//					System.getenv("DKPRO_HOME")+"/datasets/EssaysBinCas/1-2/", 
//						"de",2);
		
	}
	//CV within prompt
	protected static void runEssayGradingBaselineExperiment(String experimentName, String trainData, 
			String languageCode, Integer... questionIds) throws Exception {
		System.out.println(trainData);

		  CollectionReaderDescription readerTrain=CollectionReaderFactory.createReaderDescription(
	                GenericDatasetReader.class,
	                GenericDatasetReader.PARAM_INPUT_FILE,trainData,
	                GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true,
	                GenericDatasetReader.PARAM_ENCODING, "UTF-8",
	                GenericDatasetReader.PARAM_LANGUAGE, "de",
	                GenericDatasetReader.PARAM_SEPARATOR, "\t",
	                GenericDatasetReader.PARAM_QUESTION_PREFIX, "Q",
	                GenericDatasetReader.PARAM_TARGET_ANSWER_PREFIX, "TA",
	                GenericDatasetReader.PARAM_CORPUSNAME, "dummy"
	            
	        );
		
//		  CollectionReaderDescription readerTrain =
//		  CollectionReaderFactory.createReaderDescription( BinaryCasReader.class,
//		  BinaryCasReader.PARAM_SOURCE_LOCATION,trainData,
//		  BinaryCasReader.PARAM_LANGUAGE, "de", BinaryCasReader.PARAM_PATTERNS, "*.bin"
//		 );
		 

			runBaselineExperiment(experimentName + "_" + 1 + "", readerTrain, languageCode);
		
	}

 //Overloaded method to train test across prompts
	protected static void runEssayGradingBaselineExperiment(String experimentName, String trainData,  String testData,
			String languageCode, Integer... questionIds) throws Exception {
		System.out.println(trainData);
		System.out.println(testData);
		
			  CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
		                GenericDatasetReader.class,
		                GenericDatasetReader.PARAM_INPUT_FILE,trainData,
		                GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true,
		                GenericDatasetReader.PARAM_ENCODING, "UTF-8",
		                GenericDatasetReader.PARAM_LANGUAGE, "pt",
		                GenericDatasetReader.PARAM_SEPARATOR, "\t",
		                GenericDatasetReader.PARAM_QUESTION_PREFIX, "Q",
		                GenericDatasetReader.PARAM_TARGET_ANSWER_PREFIX, "TA",
		                GenericDatasetReader.PARAM_CORPUSNAME, "dummy",
		                GenericDatasetReader.PARAM_EXCLUDE_PROMPT_SET_ID,"1-1"
		            
		        );
			  CollectionReaderDescription readerTest=CollectionReaderFactory.createReaderDescription(
		                GenericDatasetReader.class,
		                GenericDatasetReader.PARAM_INPUT_FILE,testData,
		                GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true,
		                GenericDatasetReader.PARAM_ENCODING, "UTF-8",
		                GenericDatasetReader.PARAM_LANGUAGE, "pt",
		                GenericDatasetReader.PARAM_SEPARATOR, "\t",
		                GenericDatasetReader.PARAM_QUESTION_PREFIX, "Q",
		                GenericDatasetReader.PARAM_TARGET_ANSWER_PREFIX, "TA",
		                GenericDatasetReader.PARAM_CORPUSNAME, "dummy"
		            
		        );
			runBaselineExperiment(experimentName + "_" + 1 + "", readerTrain, readerTest,languageCode);
		
	}
	 //Overloaded method to train test across prompts
	private static void runBaselineExperiment(String experimentName, 
			CollectionReaderDescription readerTrain,CollectionReaderDescription readerTest,
		 String languageCode) throws Exception {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		dimReaders.put(DIM_READER_TEST, readerTest);
		
		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_REGRESSION);
		Dimension<Map<String, Object>> learningsArgsDims = getStandardWekaRegressionArgsDim();

		ParameterSpace pSpace = null;
			pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders), learningDims,
					Dimension.create(DIM_FEATURE_MODE, FM_UNIT), 
					FeatureSettings.getFeatureSetsEssayFull(),
					learningsArgsDims);
			runTrainTest(pSpace, experimentName, getPreprocessing(languageCode));
	}
	//CV within prompt
	private static void runBaselineExperiment(String experimentName, 
			CollectionReaderDescription readerTrain,
		 String languageCode) throws Exception {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		
		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		Dimension<Map<String, Object>> learningsArgsDims = getStandardWekaClassificationArgsDim();

		ParameterSpace pSpace = null;
			pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders), learningDims,
					Dimension.create(DIM_FEATURE_MODE, FM_UNIT), 
					FeatureSettings.getFeatureSetsEssayFull(),
					learningsArgsDims);
			runCrossValidation(pSpace, experimentName, getPreprocessing(languageCode), 10);
	}


	
	
}