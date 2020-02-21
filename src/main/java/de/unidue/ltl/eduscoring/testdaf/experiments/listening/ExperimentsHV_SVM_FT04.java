package de.unidue.ltl.eduscoring.testdaf.experiments.listening;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;

import de.unidue.ltl.eduscoring.testdaf.experiments.Experiments_ImplBase;
import de.unidue.ltl.eduscoring.testdaf.experiments.FeatureSettings;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataReader_FT04;
import de.unidue.ltl.escrito.io.generic.GenericDatasetReader;

public class ExperimentsHV_SVM_FT04 extends Experiments_ImplBase{

	public static final String ResourcesFilePath = System.getenv("DKPRO_HOME");


	public static void main(String[] args) 
			throws Exception
	{
		// To test with only one run
		runTestDaf(true, TestDafListeningDataReader_FT04.itemIds);
		//runTestDafLearningCurve(TestDafListeningDataReader_FT04.itemIds);
		
		//***** TRAIN - TEST Using LIBSVM *****
//		String dataTest    = ResourcesFilePath+"/datasets/TestDaF/HV/FT02vsFT04/Set1.tsv";
//		String dataTrain     = ResourcesFilePath+"/datasets/TestDaF/HV/FT02vsFT04/FT04_gesamt.tsv";
//		runTestDafLearningCurveTT(dataTrain, dataTest, TestDafListeningDataReader_FT04.itemIds);

	}

	
	private static void runTestDaf(boolean useCV, String ... questionIds) 
			throws Exception
	{
		String data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT04/FT04_Set 5/FT04_Set5_gesamt.tsv";
		String[] goldLabels = {"gesamt"};//, "b", "d", "i", "l", "r", "s"};
		for (String goldLabel : goldLabels){
			for (String id : questionIds) {
				System.out.println("Current QuestionId:"+id);
				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
						TestDafListeningDataReader_FT04.class,
						TestDafListeningDataReader_FT04.PARAM_INPUT_FILE, data,
						TestDafListeningDataReader_FT04.PARAM_QUESTION_ID, id,
						TestDafListeningDataReader_FT04.PARAM_GRADE_TYPE, goldLabel);
				runBaselineExperiment("TestDaf_"+id.replace(".", "")+"_"+goldLabel, readerTrain, readerTrain, useCV);
			}
		}
	}

	
	private static void runPckTT(boolean useCV, Set<String> promptIds, String strToFileTrain, String strToFileTest) 
			throws Exception
	{
		String[] goldLabels = {"Scoring"};
		for (String goldLabel : goldLabels){
			for (String promptId : promptIds) {
				//System.out.println(promptId +" " +specificPrompt);
				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
						GenericDatasetReader.class,
						GenericDatasetReader.PARAM_INPUT_FILE, strToFileTrain,
						GenericDatasetReader.PARAM_LANGUAGE, "de",
		                GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true,
		                GenericDatasetReader.PARAM_ENCODING, "UTF-8",
		                GenericDatasetReader.PARAM_LANGUAGE, "de",
		                GenericDatasetReader.PARAM_SEPARATOR, "\t",
						GenericDatasetReader.PARAM_CORPUSNAME, "TrainFT02Set1",
						GenericDatasetReader.PARAM_PROMPT_ID, promptId);	
				
				CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(
						GenericDatasetReader.class,
						GenericDatasetReader.PARAM_INPUT_FILE, strToFileTest,
						GenericDatasetReader.PARAM_LANGUAGE, "de",
		                GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true,
		                GenericDatasetReader.PARAM_ENCODING, "UTF-8",
		                GenericDatasetReader.PARAM_LANGUAGE, "de",
		                GenericDatasetReader.PARAM_SEPARATOR, "\t",
						GenericDatasetReader.PARAM_CORPUSNAME, "TestFT04Set1",
						GenericDatasetReader.PARAM_PROMPT_ID, promptId);		        
				
				runBaselineExperiment("PCK_"+promptId+"_"+goldLabel, readerTrain, readerTest, useCV);
			}
		}
	}

	
	private static void runBaselineExperiment(String experimentName, 
			CollectionReaderDescription readerTrain, 
			CollectionReaderDescription readerTest, 
			boolean useCV)
					throws Exception
	{     
		// configure training and test data reader dimension
		// train/test will use both, while cross-validation will only use the train part
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		dimReaders.put(DIM_READER_TEST, readerTest);


		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		//Dimension<Map<String, Object>> learningsArgsDims = getWekaClassificationArgsDim();
		Dimension<Map<String, Object>> learningsArgsDims = getLibSvmClassificationArgsDim();
		
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.createBundle("readers", dimReaders),
				learningDims,
				Dimension.create(DIM_FEATURE_MODE, FM_UNIT),
				getFeatureSetsDim(),
				learningsArgsDims
				);

		runCrossValidation(pSpace, experimentName, getPreprocessing("de"), 5);
	}
	

	//LEARNING-CURVE Experiments
	private static void runTestDafLearningCurve(String ... questionIds) 
			throws Exception
	{
		String languageCode = "de";
		String data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT04/FT04_Set 5/FT04_Set5_gesamt.tsv";
		String[] goldLabels = {"gesamt"};
		for (String goldLabel : goldLabels){
			for (String id : questionIds) {
				System.out.println(id);
				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
						TestDafListeningDataReader_FT04.class,
						TestDafListeningDataReader_FT04.PARAM_INPUT_FILE, data,
						TestDafListeningDataReader_FT04.PARAM_QUESTION_ID, id,
						TestDafListeningDataReader_FT04.PARAM_GRADE_TYPE, goldLabel);
				runLearningCurveExperiment("TestDaf_"+id.replace(".", "")+"_"+goldLabel, readerTrain,  readerTrain, languageCode);
			}
		}
	}

	
	private static void runTestDafLearningCurveTT(String strToFileTrain, String strToFileTest, String ... promptIds) 
			throws Exception
	{
		String languageCode = "de";
		String[] goldLabels = {"gesamt"};
		for (String goldLabel : goldLabels){
			for (String promptId : promptIds) {
				System.out.println(promptId);
				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
						TestDafListeningDataReader_FT04.class,
						TestDafListeningDataReader_FT04.PARAM_INPUT_FILE, strToFileTrain,
						TestDafListeningDataReader_FT04.PARAM_QUESTION_ID, promptId,
						TestDafListeningDataReader_FT04.PARAM_GRADE_TYPE, goldLabel);
				CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(
						TestDafListeningDataReader_FT04.class,
						TestDafListeningDataReader_FT04.PARAM_INPUT_FILE, strToFileTest,
						TestDafListeningDataReader_FT04.PARAM_QUESTION_ID, promptId,
						TestDafListeningDataReader_FT04.PARAM_GRADE_TYPE, goldLabel);
				
//				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
//						GenericDatasetReader.class,
//						GenericDatasetReader.PARAM_INPUT_FILE, strToFileTrain,
//						GenericDatasetReader.PARAM_LANGUAGE, "de",
//		                GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true,
//		                GenericDatasetReader.PARAM_ENCODING, "UTF-8",
//		                GenericDatasetReader.PARAM_LANGUAGE, "de",
//		                GenericDatasetReader.PARAM_SEPARATOR, "\t",
//						GenericDatasetReader.PARAM_CORPUSNAME, "TrainFT02Set1",
//						GenericDatasetReader.PARAM_PROMPT_ID, promptId);	
//				
//				CollectionReaderDescription readerTest = CollectionReaderFactory.createReaderDescription(
//						GenericDatasetReader.class,
//						GenericDatasetReader.PARAM_INPUT_FILE, strToFileTest,
//						GenericDatasetReader.PARAM_LANGUAGE, "de",
//		                GenericDatasetReader.PARAM_IGNORE_FIRST_LINE, true,
//		                GenericDatasetReader.PARAM_ENCODING, "UTF-8",
//		                GenericDatasetReader.PARAM_LANGUAGE, "de",
//		                GenericDatasetReader.PARAM_SEPARATOR, "\t",
//						GenericDatasetReader.PARAM_CORPUSNAME, "TestFT04Set1",
//						GenericDatasetReader.PARAM_PROMPT_ID, promptId);		        

				runLearningCurveExperiment("TestDaf_"+promptId.replace(".", "")+"_"+goldLabel, readerTrain,  readerTest, languageCode);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private static void runLearningCurveExperiment(String experimentName, 
			CollectionReaderDescription readerTrain, 
			CollectionReaderDescription readerTest,
			String languageCode)
					throws Exception
	{     
		// configure training and test data reader dimension
		// train/test will use both, while cross-validation will only use the train part
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		dimReaders.put(DIM_READER_TEST, readerTest);

		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		Dimension<Map<String, Object>> learningsArgsDims = getWekaLearningCurveClassificationArgsDim();
		//int[] NUMBER_OF_TRAINING_INSTANCES = new int[] {10,20,40,80,160,320};
		int[] NUMBER_OF_TRAINING_INSTANCES = new int[] {10,20,30,40,50,60,70,80,90,100,110,120,130,140,150};
		
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.createBundle("readers", dimReaders),
				learningDims,
				Dimension.create(DIM_FEATURE_MODE, FM_UNIT),
				Dimension.create("dimension_iterations", 10),
				Dimension.create("dimension_number_of_training_instances", NUMBER_OF_TRAINING_INSTANCES),
				FeatureSettings.getFeatureSetsDimBaseline(),
				learningsArgsDims
				);
		runLearningCurveCV(pSpace, experimentName, languageCode, 10);
	}

}
