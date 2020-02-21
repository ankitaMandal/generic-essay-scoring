package de.unidue.ltl.eduscoring.testdaf.experiments;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.core.Constants;

import de.unidue.ltl.eduscoring.testdaf.io.CombinedReader;
import de.unidue.ltl.escrito.examples.basics.FeatureSettings;
import de.unidue.ltl.escrito.io.shortanswer.Asap2Reader;
import de.unidue.ltl.escrito.io.shortanswer.PowerGradingReader;

public class EasyAdaptExample extends Experiments_ImplBase implements Constants {



	public static void main(String[] args) throws Exception{
		runPowergradingBaselineExperiment("PG_easyAdapt_Example", 
				System.getenv("DKPRO_HOME")+"/datasets/powergrading//train_70.txt", 
				System.getenv("DKPRO_HOME")+"/datasets/powergrading//test_30.txt", 
				"en", 
				"2");
	}


	protected static void runPowergradingBaselineExperiment(String experimentName
			, String trainData, String trainData2, 
			String languageCode, String... questionIds) throws Exception {
		for (String id : questionIds) {
			System.out.println("Prompt: "+id);
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(CombinedReader.class,
					CombinedReader.PARAM_INPUT_FILE_CV, trainData,
					CombinedReader.PARAM_INPUT_FILE_PLUS, trainData2,
					CombinedReader.PARAM_PROMPT_ID, id,
					CombinedReader.PARAM_LANGUAGE, "en",
					CombinedReader.PARAM_CORPUSNAME, "PG",
					CombinedReader.PARAM_ANWSER_ID_INDEX_CV, 0,
					CombinedReader.PARAM_ANWSER_ID_INDEX_PLUS, 0,
					CombinedReader.PARAM_PROMPT_ID_INDEX_CV, 1,
					CombinedReader.PARAM_PROMPT_ID_INDEX_PLUS, 1,
					CombinedReader.PARAM_TEXT_INDEX_CV, 2,
					CombinedReader.PARAM_TEXT_INDEX_PLUS, 2,
					CombinedReader.PARAM_SCORE_ID_INDEX_CV, 3,
					CombinedReader.PARAM_SCORE_ID_INDEX_PLUS, 3);

			runBaselineExperiment(experimentName + "_" + id + "", readerTrain, languageCode);
		}
	}

	private static void runBaselineExperiment(String experimentName, 
			CollectionReaderDescription readerTrain,
			String languageCode) throws Exception {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);
		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		Dimension<Map<String, Object>> learningsArgsDims = getWekaClassificationArgsEasyAdaptDim();

		ParameterSpace pSpace = null;
		pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders), learningDims,
				Dimension.create(DIM_FEATURE_MODE, FM_UNIT), 
				FeatureSettings.getFeatureSetsDimBaseline(),
				learningsArgsDims);
		runCrossValidationPlusX(pSpace, experimentName, getPreprocessing(languageCode), 5);
	}


	







}
