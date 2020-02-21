package de.unidue.ltl.eduscoring.testdaf.tc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dkpro.lab.task.Dimension;
import org.dkpro.lab.task.impl.DimensionBundle;
import org.dkpro.lab.task.impl.DynamicDimension;
import org.dkpro.lab.task.impl.FoldDimensionBundle;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.ml.experiment.ExperimentCrossValidation;

public class ExperimentCrossValidationPlusX extends ExperimentCrossValidation{


	/**
	 * 
	 * @param aExperimentName
	 *            the experiment name
	 * @param aNumFolds
	 *            the number of folds
	 * @throws TextClassificationException
	 *             in case of errors
	 */
	public ExperimentCrossValidationPlusX(String aExperimentName, int aNumFolds)
			throws TextClassificationException
	{
		super(aExperimentName, aNumFolds, null);
	}





	// TODO Add fixed set of training data to each fold.
	/**
	 * 
	 * @param fileNames
	 *            the file names
	 * @return fold dimension bundle
	 */
	@Override
	protected DimensionBundle<Collection<String>> getFoldDim(String[] fileNames)
	{
		if (comparator != null) {
			return new FoldDimensionBundle<String>("files", Dimension.create("", fileNames),
					aNumFolds, comparator);
		}
		return new FoldDimensionBundlePlus<String>("files", Dimension.create("", fileNames), aNumFolds);
	}
}


class FoldDimensionBundlePlus<T> extends DimensionBundle<Collection<T>> implements DynamicDimension{

	private Dimension<T> foldedDimension;
	private List<T>[] buckets;
	private int validationBucket = -1;
	private int folds;


	public FoldDimensionBundlePlus(String aName, Dimension<T> aFoldedDimension, int aFolds)
	{
		super(aName, new Object[0] );
		foldedDimension = aFoldedDimension;
		folds = aFolds;
	}

	private void init()
	{
		buckets = new List[folds];
		for(int bucket=0;bucket<buckets.length;bucket++){
			buckets[bucket] = new ArrayList<T>();
		}

		// Capture all data from the dimension into buckets, one per fold
		foldedDimension.rewind();

		int i = 0;
		while (foldedDimension.hasNext()) {
			int bucket = i % folds;
			String item = (String) foldedDimension.next();
			//	System.out.println("Item: "+item);

			if (buckets[bucket] == null) {
				buckets[bucket] = new ArrayList<T>();
			}


			// add "Plus" item into all buckets
			if (item.endsWith("Plus.bin")){
				for(int b=0; b<folds; b++){
					buckets[b].add((T) item);
				}
			} else {
				buckets[bucket].add((T) item);	
				i++;
			}
		}


		if (i < folds) {
			throw new IllegalStateException("Requested [" + folds + "] folds, but only got [" + i
					+ "] values. There must be at least as many values as folds.");
		}

		String foldsAndSizes = "";
		for(int bucket=0;bucket<buckets.length;bucket++){
			foldsAndSizes = foldsAndSizes + " fold " + bucket + ": size " + buckets[bucket].size() + ".  ";
			if(buckets[bucket].size() == 0){
				throw new IllegalStateException("Detected an empty fold: " + bucket + ". " + 
						"Maybe your fold control is causing all of your instances to be put in very few buckets?  " + 
						"Previous folds and buckets: " + foldsAndSizes);
			}
		}
		System.out.println(foldsAndSizes);
//		System.exit(-1);
	}
	private void addToBucket(T newItem, int bucket){
		if (buckets[bucket] == null) {
			buckets[bucket] = new ArrayList<T>();
		}

		buckets[bucket].add(newItem);
	}

	@Override
	public boolean hasNext()
	{
		return validationBucket < buckets.length-1;
	}

	@Override
	public void rewind()
	{
		init();
		validationBucket = -1;
	}

	@Override
	public Map<String, Collection<T>> next()
	{
		validationBucket++;
		return current();
	}

	@Override
	public Map<String, Collection<T>> current()
	{
		List<T> trainingData = new ArrayList<T>();
		for (int i = 0; i < buckets.length; i++) {
			if (i != validationBucket) {
				trainingData.addAll(buckets[i]);
			}
		}

		Map<String, Collection<T>> data = new HashMap<String, Collection<T>>();
		data.put(getName()+"_training", trainingData);
		data.put(getName()+"_validation", buckets[validationBucket]);
		return data;
	}

	@Override
	public void setConfiguration(Map<String, Object> aConfig)
	{
		if (foldedDimension instanceof DynamicDimension) {
			((DynamicDimension) foldedDimension).setConfiguration(aConfig);
		}
	}

}

