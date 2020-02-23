package de.unidue.ltl.eduscoring.genericessayscoring.experiments;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.PennTree;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.lab.Lab;
import org.dkpro.lab.task.BatchTask.ExecutionPolicy;
import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.ParameterSpace;
import org.dkpro.tc.api.features.TcFeature;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.api.features.TcFeatureSet;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.features.ngram.CharacterNGram;
import org.dkpro.tc.features.ngram.PosNGram;
import org.dkpro.tc.features.ngram.SkipWordNGram;
import org.dkpro.tc.features.ngram.WordNGram;
import org.dkpro.tc.ml.experiment.ExperimentCrossValidation;
import org.dkpro.tc.ml.experiment.ExperimentTrainTest;
//import org.dkpro.tc.ml.libsvm.LibsvmAdapter;
import org.dkpro.tc.ml.report.CrossValidationReport;
import org.dkpro.tc.ml.report.TrainTestReport;
import org.dkpro.tc.ml.weka.WekaAdapter;

import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.clusterers.SimpleKMeans;
import de.tudarmstadt.ukp.dkpro.core.berkeleyparser.BerkeleyParser;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpParser;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasWriter;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpChunker;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unidue.ltl.escrito.core.report.CvEvaluationReport;
import de.unidue.ltl.escrito.core.report.GradingEvaluationReport;
/*import de.unidue.ltl.eduscoring.testdaf.features.DifferenceTypesFeatureExtractor;
import de.unidue.ltl.eduscoring.testdaf.features.EditDifferencesFeatureExtractor;
import de.unidue.ltl.eduscoring.testdaf.features.EditDifferencesWithContextFeatureExtractor;
import de.unidue.ltl.eduscoring.testdaf.features.ErrorExtractor;
import de.unidue.ltl.eduscoring.testdaf.features.SourceNgramOverlapExtractor;*/
import de.unidue.ltl.escrito.examples.basics.FeatureSettings;
import de.unidue.ltl.escrito.features.length.NrOfTokens;
import de.unidue.ltl.escrito.features.similarity.PairwiseFeatureWrapper;

public abstract class Experiments_ImplBase extends de.unidue.ltl.escrito.examples.basics.Experiments_ImplBase
		implements Constants {

	public enum Setting {
		ngram_10,
		ngram_100,
		ngram_1000,
		ngram_10000,
		ngram_100000,
		full_1000,
		full_10000
	}

	public static final Boolean[] toLowerCase = new Boolean[] { true };

	public static final String stopwordList = "classpath:/stopwords/english_stopwords.txt";
	// public static final String stopwordList =
	// "classpath:/stopwords/english_empty.txt";

	public static final String SPELLING_VOCABULARY = "classpath:/vocabulary/en_US_dict.txt";


	@SuppressWarnings("unchecked")
	public static Dimension<Map<String, Object>> getWekaClassificationArgsDim() {
		Map<String, Object> config = new HashMap<String, Object>();
		config.put(DIM_CLASSIFICATION_ARGS, new Object[] { new WekaAdapter(), SMO.class.getName() });
		config.put(DIM_DATA_WRITER, new WekaAdapter().getDataWriterClass());
		config.put(DIM_FEATURE_USE_SPARSE, new WekaAdapter().useSparseFeatures());
		Dimension<Map<String, Object>> mlas = Dimension.createBundle("config", config);
		return mlas;
	}

//	@SuppressWarnings("unchecked")
//	public static Dimension<Map<String, Object>> getLibSvmClassificationArgsDim() {
//		Map<String, Object> config = new HashMap<String, Object>();
//		config.put(DIM_CLASSIFICATION_ARGS, new Object[] { new LibsvmAdapter(), "-s", "0", "-c", "1000" });
//		config.put(DIM_DATA_WRITER, new LibsvmAdapter().getDataWriterClass());
//		config.put(DIM_FEATURE_USE_SPARSE, new LibsvmAdapter().useSparseFeatures());
//		Dimension<Map<String, Object>> mlas = Dimension.createBundle("config", config);
//		return mlas;
//	}
	public static Dimension<Map<String, Object>> getStandardWekaRegressionArgsDim() {
		Map<String, Object> config = new HashMap<String, Object>();
		config.put(DIM_CLASSIFICATION_ARGS, new Object[] { new WekaAdapter(), LinearRegression.class.getName() });
		config.put(DIM_DATA_WRITER, new WekaAdapter().getDataWriterClass());
		config.put(DIM_FEATURE_USE_SPARSE, new WekaAdapter().useSparseFeatures());
		Dimension<Map<String, Object>> mlas = Dimension.createBundle("config", config);
		return mlas;
	}

	public static Dimension<TcFeatureSet> getFeatureSetsDim() {
		System.out.println(10);
		Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, new TcFeatureSet(
				getNrOfTokensFeature(), getNGramFeature(1, 3, 1000), getCharacterNGramFeature(2, 4, 1000)));
		return dimFeatureSets;
	}

	public static Dimension<?> getFeatureSetsDim(int numNGrams) {
		System.out.println(10);
		Dimension<TcFeatureSet> dimFeatureSets = Dimension.create(DIM_FEATURE_SET, new TcFeatureSet(
				getNrOfTokensFeature(), getNGramFeature(1, 3, numNGrams), getCharacterNGramFeature(2, 4, numNGrams)));
		return dimFeatureSets;
	}


	public static TcFeature getNrOfTokensFeature() {
		return TcFeatureFactory.create(NrOfTokens.class);
	}

	public static TcFeature getNGramFeature(int min_n, int max_n, int top_k) {
		return TcFeatureFactory.create(WordNGram.class, WordNGram.PARAM_NGRAM_MIN_N, 1, WordNGram.PARAM_NGRAM_MAX_N, 3,
				WordNGram.PARAM_NGRAM_USE_TOP_K, top_k);
	}

	public static TcFeature getSkipNGramFeature(int skip_size, int min_n, int max_n, int top_k) {
		return TcFeatureFactory.create(SkipWordNGram.class, SkipWordNGram.PARAM_SKIP_SIZE, skip_size,
				SkipWordNGram.PARAM_NGRAM_MIN_N, min_n, SkipWordNGram.PARAM_NGRAM_MAX_N, max_n,
				SkipWordNGram.PARAM_NGRAM_USE_TOP_K, top_k);
	}

	public static TcFeature getCharacterNGramFeature(int min_n, int max_n, int top_k) {
		return TcFeatureFactory.create(CharacterNGram.class, CharacterNGram.PARAM_NGRAM_MIN_N, 1,
				CharacterNGram.PARAM_NGRAM_MAX_N, 3, CharacterNGram.PARAM_NGRAM_USE_TOP_K, top_k);
	}

	public static TcFeature getPOSNGramFeature(int min_n, int max_n, int top_k) {
		return TcFeatureFactory.create(PosNGram.class, PosNGram.PARAM_NGRAM_MIN_N, min_n, PosNGram.PARAM_NGRAM_MAX_N,
				max_n, PosNGram.PARAM_NGRAM_USE_TOP_K, top_k);
	}

	public static TcFeature getStringSimilarityFeature(String corpusName, String targetanswerprefix) {
		return TcFeatureFactory.create(PairwiseFeatureWrapper.class, PairwiseFeatureWrapper.PARAM_AGGREGATION_METHOD,
				PairwiseFeatureWrapper.AggregationMethod.MAXIMUM,
				// PairwiseFeatureWrapper.PARAM_PAIRWISE_FEATURE_EXTRACTOR,
				// StringSimilarityFeatureExtractor.class.getName(),
				PairwiseFeatureWrapper.PARAM_TARGET_ANSWER_PREFIX, targetanswerprefix,
				PairwiseFeatureWrapper.PARAM_ADDITIONAL_TEXTS_LOCATION,
				System.getenv("DKPRO_HOME") + "/processedData/" + corpusName);
	}


	@SuppressWarnings("unchecked")
	public static void runClusteringExperiment(String experimentName, CollectionReaderDescription readerTrain,
			int numClustersMin, int numClustersMax, String languageCode) throws Exception {
		Map<String, Object> dimReaders = new HashMap<String, Object>();
		dimReaders.put(DIM_READER_TRAIN, readerTrain);

		Dimension<String> learningDims = Dimension.create(DIM_LEARNING_MODE, LM_SINGLE_LABEL);
		Dimension<List<String>> dimClusteringArgs = Dimension.create("clusteringArguments",
				Arrays.asList(new String[] { SimpleKMeans.class.getName() }));

		Dimension<Map<String, Object>> learningsArgsDims = getStandardWekaClassificationArgsDim(); // TODO What is this
																									// used for?

		ParameterSpace pSpace = new ParameterSpace(Dimension.createBundle("readers", dimReaders),
				Dimension.create("dimension_number_of_clusters_min", numClustersMin),
				Dimension.create("dimension_number_of_clusters_max", numClustersMax), dimClusteringArgs, learningDims,
				Dimension.create(DIM_FEATURE_MODE, FM_UNIT), FeatureSettings.getFeatureSetsDimBaseline(),
				learningsArgsDims);
		runClustering(pSpace, experimentName, languageCode);
	}

	// TODO: make preprocessing dependent on the feature extraction used
	public static AnalysisEngineDescription getPreprocessing(String languageCode)
			throws ResourceInitializationException {
		AnalysisEngineDescription segmenter = createEngineDescription(BreakIteratorSegmenter.class);
		AnalysisEngineDescription posTagger = createEngineDescription(OpenNlpPosTagger.class);
		AnalysisEngineDescription chunker = createEngineDescription(OpenNlpChunker.class);
		//AnalysisEngineDescription lemmatizer = createEngineDescription(MateLemmatizer.class);
		AnalysisEngineDescription lemmatizer = createEngineDescription(NoOpAnnotator.class);
		//AnalysisEngineDescription tagger = createEngineDescription(NoOpAnnotator.class);

//		AnalysisEngineDescription parserEN = createEngineDescription(NoOpAnnotator.class);
//		System.out.println("Setting up Berkeley Parser");
//		AnalysisEngineDescription parserDE = createEngineDescription(BerkeleyParser.class,
//				BerkeleyParser.PARAM_LANGUAGE,"de",BerkeleyParser.PARAM_WRITE_PENN_TREE,true, BerkeleyParser.PARAM_WRITE_POS, true 
//                , BerkeleyParser.PARAM_READ_POS, false);
//		AnalysisEngineDescription writer = createEngineDescription(
//				BinaryCasWriter.class, 
//				BinaryCasWriter.PARAM_FORMAT, "6+",
//				BinaryCasWriter.PARAM_OVERWRITE, true,
//				BinaryCasWriter.PARAM_TARGET_LOCATION, System.getenv("DKPRO_HOME")+"/datasets/EssaysBinCas/alle"
//				);
		posTagger = createEngineDescription(OpenNlpPosTagger.class, OpenNlpPosTagger.PARAM_LANGUAGE, languageCode);
		lemmatizer = createEngineDescription(MateLemmatizer.class);
//		//parserEN = createEngineDescription(ClearNlpParser.class, ClearNlpParser.PARAM_LANGUAGE, languageCode,
//				ClearNlpParser.PARAM_VARIANT, "ontonotes");
		//return createEngineDescription(createEngineDescription(ClearNlpSegmenter.class), posTagger, lemmatizer);
		
		if (languageCode.equals("en")) {
			return createEngineDescription(createEngineDescription(segmenter,lemmatizer,posTagger));
		} else if (languageCode.equals("de")) {
			return createEngineDescription(createEngineDescription(segmenter,posTagger,lemmatizer));
		} else {
			System.err.println("Unknown language code " + languageCode + ". We currently support: en, de");
			System.exit(-1);
		}
		return null;
	}
	// ######### EXPERIMENTAL SETUPS ##########
		// ##### TRAIN-TEST #####
		protected static void runTrainTest(ParameterSpace pSpace, String name, AnalysisEngineDescription aed)
				throws Exception {
			System.out.println("Running experiment " + name);
			ExperimentTrainTest batch = new ExperimentTrainTest(name + "-TrainTest");
			batch.setPreprocessing(aed);
			// batch.addInnerReport(GradingEvaluationReport.class);
			batch.setParameterSpace(pSpace);
			batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
			//batch.addReport(GradingEvaluationReport.class);
			batch.addReport(TrainTestReport.class);
			// Run
			Lab.getInstance().run(batch);
		}

		// ##### CV #####
		protected static void runCrossValidation(ParameterSpace pSpace, String name, AnalysisEngineDescription aed,
				int numFolds) throws Exception {
			ExperimentCrossValidation batch = new ExperimentCrossValidation(name + "-CV", numFolds);
			batch.setPreprocessing(aed);
			// TODO: adapt so that it also works from this slightly different context
			// batch.addInnerReport(GradingEvaluationReport.class);
			batch.setParameterSpace(pSpace);
			batch.setExecutionPolicy(ExecutionPolicy.RUN_AGAIN);
			batch.addReport(CrossValidationReport.class);
			batch.addReport(CvEvaluationReport.class);

			// Run
			System.out.println(batch);
			Lab.getInstance().run(batch);
		}

}