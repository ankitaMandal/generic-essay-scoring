package de.unidue.ltl.eduscoring.testdaf.experiments.listening;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;

import de.unidue.ltl.eduscoring.testdaf.experiments.Experiments_ImplBase;
import de.unidue.ltl.eduscoring.testdaf.experiments.FeatureSettings;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataPhoneticRepresentationReader_FT01;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataReader_FT00;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataReader_FT01;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataReader_FT02;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataReader_FT02.GradeType;

public class ExperimentsHV_SVM_FT02 extends Experiments_ImplBase{

	public static final String ResourcesFilePath = System.getenv("DKPRO_HOME");


	public static void main(String[] args) 
			throws Exception
	{
		// To test with only one run
		//  runTestDaf(true, "2.4");
		//	runTestDafPhonetic(true, "2.3");
		//runTestDaf(true, TestDafListeningDataReader_FT02.itemIds);
		//runTestDafPhonetic(true, TestDafListeningDataReader_FT02.itemIds); //not implemented PresentationRader_FT02 you can use FT01 instead
		runTestDafLearningCurve(TestDafListeningDataReader_FT02.itemIds);
	}

	private static void runTestDaf(boolean useCV, String ... questionIds) 
			throws Exception
	{
		String data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT02/Automatic Scoring/Set1.tsv";
		String[] goldLabels = {"gesamt"};//, "b", "d", "i", "l", "r", "s"};
		for (String goldLabel : goldLabels){
			for (String id : questionIds) {
				System.out.println("Current QuestionId:"+id);
				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
						TestDafListeningDataReader_FT02.class,
						TestDafListeningDataReader_FT02.PARAM_INPUT_FILE, data,
						TestDafListeningDataReader_FT02.PARAM_QUESTION_ID, id,
						TestDafListeningDataReader_FT02.PARAM_GRADE_TYPE, goldLabel);
				runBaselineExperiment("TestDaf_"+id.replace(".", "")+"_"+goldLabel, readerTrain, readerTrain, useCV);
			}
		}
	}


//	private static void runTestDafPhonetic(boolean useCV, String ... questionIds) 
//			throws Exception
//	{
//		String data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT02/Automatic Scoring/Set2.tsv";
//		String[] goldLabels = {"gesamt", "b", "d", "i", "l", "r", "s"};
//		for (String goldLabel : goldLabels){
//			for (String id : questionIds) {
//				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
//						TestDafListeningDataPhoneticRepresentationReader_FT02.class,
//						TestDafListeningDataPhoneticRepresentationReader_FT02.PARAM_INPUT_FILE, data,
//						TestDafListeningDataPhoneticRepresentationReader_FT02.PARAM_QUESTION_ID, id,
//						TestDafListeningDataReader_FT02.PARAM_GRADE_TYPE, goldLabel);
//
//				runBaselineExperiment("TestDafPhonetic_"+id.replace(".", "")+"_"+goldLabel, readerTrain, readerTrain, useCV);
//			}
//		}
//	}


	private static void runTestDafLearningCurve(String ... questionIds) 
			throws Exception
	{
		String languageCode = "de";
		String data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT02/Automatic Scoring/Set1.tsv";
		//String[] goldLabels = {"FINAL", "TEACHER1", "TEACHER2", "ADJUDICATED"};
		String[] goldLabels = {"gesamt"};
		for (String goldLabel : goldLabels){
			for (String id : questionIds) {
				System.out.println(id);
				CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
						TestDafListeningDataReader_FT02.class,
						TestDafListeningDataReader_FT02.PARAM_INPUT_FILE, data,
						TestDafListeningDataReader_FT02.PARAM_QUESTION_ID, id,
						TestDafListeningDataReader_FT02.PARAM_GRADE_TYPE, goldLabel);
				runLearningCurveExperiment("TestDaf_"+id.replace(".", "")+"_"+goldLabel, readerTrain, languageCode);
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
	
	
	@SuppressWarnings("unchecked")
	private static void runLearningCurveExperiment(String experimentName, 
			CollectionReaderDescription readerTrain, 
			String languageCode)
					throws Exception
	{     
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);

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
