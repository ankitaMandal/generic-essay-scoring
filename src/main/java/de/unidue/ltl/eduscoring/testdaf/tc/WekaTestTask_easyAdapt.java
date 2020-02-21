package de.unidue.ltl.eduscoring.testdaf.tc;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.dkpro.lab.engine.TaskContext;
import org.dkpro.lab.storage.StorageService.AccessMode;
import org.dkpro.tc.core.Constants;
import org.dkpro.tc.ml.weka.core.MekaTrainer;
import org.dkpro.tc.ml.weka.core.WekaTrainer;
import org.dkpro.tc.ml.weka.core._eka;
import org.dkpro.tc.ml.weka.task.WekaFeatureSelector;
import org.dkpro.tc.ml.weka.task.WekaOutcomeHarmonizer;
import org.dkpro.tc.ml.weka.task.WekaTestTask;
import org.dkpro.tc.ml.weka.util.MultilabelResult;

import meka.core.Result;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

public class WekaTestTask_easyAdapt 
extends WekaTestTask {


	String source = "CV";
	String target = "Plus";


	@Override
	public void execute(TaskContext aContext) throws Exception
	{
		super.execute(aContext);
		boolean multiLabel = learningMode.equals(Constants.LM_MULTI_LABEL);

		File arffFileTrain = getFile(aContext, TEST_TASK_INPUT_KEY_TRAINING_DATA,
				Constants.FILENAME_DATA_IN_CLASSIFIER_FORMAT, AccessMode.READONLY);
		File arffFileTest = getFile(aContext, TEST_TASK_INPUT_KEY_TEST_DATA,
				Constants.FILENAME_DATA_IN_CLASSIFIER_FORMAT, AccessMode.READONLY);

		Instances originalTrainData = _eka.getInstances(arffFileTrain, multiLabel);
		Instances originalTestData = _eka.getInstances(arffFileTest, multiLabel);

		WekaOutcomeHarmonizer harmonizer = new WekaOutcomeHarmonizer(originalTrainData, originalTestData,
				learningMode);
		originalTestData = harmonizer.harmonize();

		Instances copiedTrainData = new Instances(originalTrainData);
		Instances copiedTestData = new Instances(originalTestData);
		copiedTrainData = _eka.removeInstanceId(originalTrainData, multiLabel);
		copiedTestData = _eka.removeInstanceId(originalTestData, multiLabel);



		Instance instance;
		Attribute attribute;

		//		System.out.println("Attributes before (Train): ");
		//		Enumeration<Instance> instancesBefore = originalTrainData.enumerateInstances();			
		//		while(instancesBefore.hasMoreElements()) {
		//			System.out.println();
		//			System.out.println("NEW INSTANCE: ");
		//			instance = instancesBefore.nextElement();
		//			Enumeration<Attribute> attributesBefore = instance.enumerateAttributes();
		//			while(attributesBefore.hasMoreElements()) {
		//				attribute = attributesBefore.nextElement();
		//				System.out.println(attribute.name()+" + "+instance.value(attribute));
		//			}
		//		}
		//		System.out.println();
		//		System.out.println("Attributes before (Test): ");
		//		instancesBefore = originalTestData.enumerateInstances();			
		//		while(instancesBefore.hasMoreElements()) {
		//			System.out.println();
		//			System.out.println("NEW INSTANCE: ");
		//			instance = instancesBefore.nextElement();
		//			Enumeration<Attribute> attributesBefore = instance.enumerateAttributes();
		//			while(attributesBefore.hasMoreElements()) {
		//				attribute = attributesBefore.nextElement();
		//				System.out.println(attribute.name()+" + "+instance.value(attribute));
		//			}
		//		}
		//		System.out.println();

		int noOfAttributes = originalTrainData.numAttributes() - 1;
		//		System.out.println(noOfAttributes);

		//first round: add source and target features:
		Enumeration<Attribute> attributes = originalTrainData.enumerateAttributes();
		int afterIdIndexSource = 0;
		int afterIdIndexTarget = 0;
		while(attributes.hasMoreElements()) {
			attribute = attributes.nextElement();
			if(attribute.name().equals(Constants.ID_FEATURE_NAME)) {
				afterIdIndexSource += -1;
				afterIdIndexTarget += -2;
				System.out.println("Found ID feature.");
			}
			else {
				originalTrainData.insertAttributeAt(new Attribute(attribute.name()+"_source"), attribute.index() + afterIdIndexSource + noOfAttributes);
				originalTrainData.insertAttributeAt(new Attribute(attribute.name()+"_target"), 2*attribute.index() + noOfAttributes + 1 + afterIdIndexTarget);
				originalTestData.insertAttributeAt(new Attribute(attribute.name()+"_source"), attribute.index() + afterIdIndexSource + noOfAttributes);
				originalTestData.insertAttributeAt(new Attribute(attribute.name()+"_target"), 2*attribute.index() + noOfAttributes + 1 + afterIdIndexTarget);
				//		System.out.println("Added source and target features for: "+attribute.index()+"\t"+attribute.name()); 
			}
		}

		System.out.println("Done adding features");

		String task = "dummy";
		//fill attributes with values: train
		Enumeration<Instance> instancesTrain = originalTrainData.enumerateInstances();
		while(instancesTrain.hasMoreElements()) {
			instance = instancesTrain.nextElement();
//			System.out.println(instance.attribute(0).name());
			String id = instance.stringValue(0);
			task = id.substring(id.lastIndexOf("_")+1);
//			System.out.println("task: "+task);
			
			attributes = copiedTrainData.enumerateAttributes();
			int beforeIdIndex = 0;
			while(attributes.hasMoreElements()) {
				attribute = attributes.nextElement();
				if(attribute.name().equals(Constants.ID_FEATURE_NAME)) {
					beforeIdIndex = -1;
				}
				else {
					//					System.out.println(attribute.index()+"\t"+attribute.name());
					if(task.equals(source)) {
						//					System.out.println("Is source essay.");
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex, instance.value(attribute));
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex+noOfAttributes-1, 0.0);
					}
					else if(task.equals(target)) {
						//					System.out.println("Is target essay.");
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex, 0.0);
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex+noOfAttributes-1, instance.value(attribute));
					}
					else {
						System.err.println("No task found for id: "+ id);
						System.exit(-1);
					}
					//				System.out.println("Train: filled attribute: "+attribute.index() +"\t"+attribute.name());
				}
			}
			System.out.println("Train: filled attributes for id "+id);
		}

		System.out.println("Train: done filling features with values.");

		//fill attributes with values: test
		Enumeration<Instance> instancesTest = originalTestData.enumerateInstances();
		while(instancesTest.hasMoreElements()) {
			instance = instancesTest.nextElement();
//			System.out.println(instance.attribute(0).name());
			String id = instance.stringValue(0);
			task = id.substring(id.lastIndexOf("_")+1);
//			System.out.println("task: "+task);
			
			attributes = copiedTestData.enumerateAttributes();
			int beforeIdIndex = 0;
			while(attributes.hasMoreElements()) {
				attribute = attributes.nextElement();
				if(attribute.name().equals(Constants.ID_FEATURE_NAME)) {
					beforeIdIndex = -1;
				}
				else {
					if(task.equals(source)) {
						//					System.out.println("Is source essay.");
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex, instance.value(attribute));
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex+noOfAttributes-1, 0.0);
					}
					else if(task.equals(target)) {
						//					System.out.println("Is target essay.");
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex, 0.0);
						instance.setValue(attribute.index()+noOfAttributes+beforeIdIndex+noOfAttributes-1, instance.value(attribute));
					}
					else {
						System.err.println("No task found for id: "+ id);
						System.exit(-1);
					}
					//				System.out.println("Test: filled attribute: "+attribute.index() +"\t"+attribute.name());
				}
			}
			System.out.println("Test: filled attributes for id "+id);
		}

		System.out.println("Test: done filling features with values.");


		WekaFeatureSelector selector = new WekaFeatureSelector(copiedTrainData, copiedTestData, multiLabel,
				attributeEvaluator, featureSearcher, labelTransformationMethod, numLabelsToKeep,
				aContext.getFile("featureSelection", AccessMode.READWRITE));
		selector.apply();
		copiedTrainData = selector.getTrainingInstances();
		copiedTestData = selector.getTestingInstances();

		// file to hold prediction results
		File evalOutput = getFile(aContext, "", evaluationBin, AccessMode.READWRITE);
		File model = aContext.getFile(MODEL_CLASSIFIER, AccessMode.READWRITE);
		// evaluation & prediction generation
		if (multiLabel) {
			// we don't need to build the classifier - meka does this
			// internally

			MekaTrainer trainer = new MekaTrainer(false);
			Classifier cl = trainer.train(copiedTrainData, model, getParameters(classificationArguments));

			Result r = getEvaluationMultilabel(cl, copiedTrainData, copiedTestData, threshold);
			writeMlResultToFile(
					new MultilabelResult(r.allTrueValues(), r.allPredictions(), threshold),
					evalOutput);
			copiedTestData = getPredictionInstancesMultiLabel(copiedTestData, cl,
					getMekaThreshold(threshold, r, copiedTrainData));
			copiedTestData = _eka.addInstanceId(copiedTestData, originalTestData, true);
		}
		else {

			WekaTrainer trainer = new WekaTrainer();
			Classifier classifier = trainer.train(copiedTrainData, model,
					getParameters(classificationArguments));

			createWekaEvaluationObject(classifier, evalOutput, copiedTrainData, copiedTestData);

			copiedTestData = getPredictionInstancesSingleLabel(copiedTestData, classifier);
			copiedTestData = _eka.addInstanceId(copiedTestData, originalTestData, false);
		}

		// Write out the prediction - the data sink expects an .arff ending file so we game it a bit
		// and rename the file afterwards to .txt
		File predictionFile = getFile(aContext, "", Constants.FILENAME_PREDICTIONS,
				AccessMode.READWRITE);
		File arffDummy = new File(predictionFile.getParent(), "prediction.arff");
		DataSink.write(arffDummy.getAbsolutePath(), copiedTestData);
	//	FileUtils.moveFile(arffDummy, predictionFile);
	}


	private Instances getPredictionInstancesMultiLabel(Instances testData, Classifier cl,
			double[] thresholdArray)
					throws Exception
	{
		int numLabels = testData.classIndex();

		// get predictions
		List<double[]> labelPredictionList = new ArrayList<double[]>();
		for (int i = 0; i < testData.numInstances(); i++) {
			labelPredictionList.add(cl.distributionForInstance(testData.instance(i)));
		}

		// add attributes to store predictions in test data
		Add filter = new Add();
		for (int i = 0; i < numLabels; i++) {
			filter.setAttributeIndex(Integer.toString(numLabels + i + 1));
			filter.setNominalLabels("0,1");
			filter.setAttributeName(
					testData.attribute(i).name() + "_" + WekaTestTask.PREDICTION_CLASS_LABEL_NAME);
			filter.setInputFormat(testData);
			testData = Filter.useFilter(testData, filter);
		}

		// fill predicted values for each instance
		for (int i = 0; i < labelPredictionList.size(); i++) {
			for (int j = 0; j < labelPredictionList.get(i).length; j++) {
				testData.instance(i).setValue(j + numLabels,
						labelPredictionList.get(i)[j] >= thresholdArray[j] ? 1. : 0.);
			}
		}
		return testData;
	}

}
