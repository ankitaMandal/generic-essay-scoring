package de.unidue.ltl.eduscoring.testdaf.experiments.listening;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;

import de.unidue.ltl.eduscoring.testdaf.experiments.Experiments_ImplBase;
import de.unidue.ltl.eduscoring.testdaf.io.TestDafListeningDataReader_2017;

public class ExperimentsHV_Clustering_FT02 extends Experiments_ImplBase{


	public static final String ResourcesFilePath = System.getenv("DKPRO_HOME");


	public static void main(String[] args) 
			throws Exception
	{
		runTestDaf(10, 10, TestDafListeningDataReader_2017.itemIds_FT0203);
	}



	private static void runTestDaf(int numClustersMin, int numClustersMax, String ... questionIds) 
			throws Exception
	{
		String data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT02/FT02_Daten Set 1/Set1_step_b.csv";
		for (String id : questionIds) {
			System.out.println(id);
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
					TestDafListeningDataReader_2017.class,
					TestDafListeningDataReader_2017.PARAM_INPUT_FILE, data,
					TestDafListeningDataReader_2017.PARAM_QUESTION_ID, id);
			runClusteringExperiment("TestDaf_Set1_"+id.replace(".", ""), readerTrain, numClustersMin, numClustersMax, "de");
		}
		data           = ResourcesFilePath+"/datasets/TestDaF/HV/FT02/FT02_Daten Set 2/Set2_step_b.csv";
		for (String id : questionIds) {
			System.out.println(id);
			CollectionReaderDescription readerTrain = CollectionReaderFactory.createReaderDescription(
					TestDafListeningDataReader_2017.class,
					TestDafListeningDataReader_2017.PARAM_INPUT_FILE, data,
					TestDafListeningDataReader_2017.PARAM_QUESTION_ID, id);
			runClusteringExperiment("TestDaf_Set2_"+id.replace(".", ""), readerTrain, numClustersMin, numClustersMax, "de");
		}
	}


}
