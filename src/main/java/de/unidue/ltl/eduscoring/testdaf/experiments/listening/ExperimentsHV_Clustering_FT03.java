package de.unidue.ltl.eduscoring.testdaf.experiments.listening;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;

import de.unidue.ltl.eduscoring.testdaf.experiments.Experiments_ImplBase;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataReader_2017;

public class ExperimentsHV_Clustering_FT03 extends Experiments_ImplBase{

	// TODO: run experiments with word/character ngrams on original and sound-transformed data
	// compare and add results to an overview file, where we see classification per instance (i.e. extend original data filer)
	public static final String ResourcesFilePath = System.getenv("DKPRO_HOME");


	public static void main(String[] args) 
			throws Exception
	{
		runTestDaf(10, "5.1");
		//		runTestDaf(20, "5.1");
		//		runTestDaf(10, TestDafListeningDataReader_2017.itemIds);
	}





	private static void runTestDaf(int numClusters, String ... questionIds) 
			throws Exception
	{
		String data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT03/FT03_Daten Set 3/set3.tsv";
		for (String id : questionIds) {
			System.out.println(id);
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
					TestDafListeningDataReader_2017.class,
					TestDafListeningDataReader_2017.PARAM_INPUT_FILE, data,
					TestDafListeningDataReader_2017.PARAM_QUESTION_ID, id);
			runClusteringExperiment("TestDaf_Set3_"+id.replace(".", ""), readerTrain, numClusters, numClusters, "de");
		}
		data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT03/FT03_Daten Set 4/set4.tsv";
		for (String id : questionIds) {
			System.out.println(id);
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
					TestDafListeningDataReader_2017.class,
					TestDafListeningDataReader_2017.PARAM_INPUT_FILE, data,
					TestDafListeningDataReader_2017.PARAM_QUESTION_ID, id);
			runClusteringExperiment("TestDaf_Set4_"+id.replace(".", ""), readerTrain, numClusters, numClusters, "de");
		}
	}




}
