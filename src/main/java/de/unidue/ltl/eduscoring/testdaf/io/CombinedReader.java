package de.unidue.ltl.eduscoring.testdaf.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.unidue.ltl.escrito.core.types.LearnerAnswerWithReferenceAnswer;
import de.unidue.ltl.escrito.io.generic.GenericDatasetItem;
import de.unidue.ltl.escrito.io.util.Utils;

/**
 * We expect a generic data format with one item per line, and at least 4 columns: 
 *  1: promptid (can be the same for all items),
 *  2: answerid, 
 *  3: answer text and 
 *  4: score.
 *  if the come in a different order, please parametrize.
 * The score has to be either numeric or categorial.
 * If it is numeric, then is should be mappable to an integer, to ensure that the values are equally spaced. 
 * (a suitable factor is needed in that case, e.g. 2, if the scores in the dataset are 0.0, 0.5, 1.0 etc)
 * Optional, a reference answer can be provided in column 5, and the question string in column 6 in which case you also have to set the parameters
 * corpusName, TargetAnswerPrefix and questionPrefix.
 * These parameters have also be handed over to any similarity-based feature extractor.
 * We expect that there is only one reference answer and one question for all answers that belong to the same prompt.
 *
 *
 */


// TODO: accept also categorical scores


public class CombinedReader  extends JCasCollectionReader_ImplBase{


	public static final String PARAM_INPUT_FILE_CV = "InputFileCV";
	@ConfigurationParameter(name = PARAM_INPUT_FILE_CV, mandatory = true)
	protected String inputFileStringCV;
	protected URL inputFileURLCV;



	public static final String PARAM_INPUT_FILE_PLUS = "InputFilePlus";
	@ConfigurationParameter(name = PARAM_INPUT_FILE_PLUS, mandatory = true)
	protected String inputFileStringPlus;
	protected URL inputFileURLPlus;

	public static final String PARAM_LANGUAGE = "Language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true  )
	protected String language;

	public static final String PARAM_ENCODING = "Encoding";
	@ConfigurationParameter(name = PARAM_ENCODING, mandatory = false, defaultValue = "UTF-8")
	private String encoding;

	public static final String PARAM_SEPARATOR = "Separator";
	@ConfigurationParameter(name = PARAM_SEPARATOR, mandatory = false, defaultValue = "\t")
	private String separator;

	public static final String PARAM_SCORE_FACTOR = "ScoreMultiplicationFactor";
	@ConfigurationParameter(name = PARAM_SCORE_FACTOR, mandatory = false, defaultValue = "1")
	private Integer scoreMultiplicationFactor;

	public static final String PARAM_IGNORE_FIRST_LINE = "ignoreFirstLine";
	@ConfigurationParameter(name = PARAM_IGNORE_FIRST_LINE, mandatory = false, defaultValue = "false")
	private Boolean ignoreFirstLine;

	public static final String PARAM_QUESTION_PREFIX = "QuestionPrefix";
	@ConfigurationParameter(name = PARAM_QUESTION_PREFIX, mandatory = false, defaultValue = "Q")
	private String questionPrefix;

	public static final String PARAM_TARGET_ANSWER_PREFIX = "TargetAnswerPrefix";
	@ConfigurationParameter(name = PARAM_TARGET_ANSWER_PREFIX, mandatory = false, defaultValue = "TA")
	private String targetAnswerPrefix;

	public static final String PARAM_PROMPT_ID = "PromptID";
	@ConfigurationParameter(name = PARAM_PROMPT_ID, mandatory = false, defaultValue = "-1")
	protected String requestedPromptId;

	public static final String PARAM_EXCLUDE_PROMPT_SET_ID = "ExcludePromptSetId";
	@ConfigurationParameter(name = PARAM_EXCLUDE_PROMPT_SET_ID, mandatory = false)
	protected String excludedPromptSetId; 

	public static final String PARAM_CORPUSNAME = "corpusName";
	@ConfigurationParameter(name = PARAM_CORPUSNAME, mandatory = true)
	protected String corpusName;


	public static final String PARAM_PROMPT_ID_INDEX_CV = "promptIdIndexCv";
	@ConfigurationParameter(name = PARAM_PROMPT_ID_INDEX_CV, mandatory = false, defaultValue = "0")
	protected int promptIdIndexCv;

	public static final String PARAM_ANWSER_ID_INDEX_CV = "answerIdIndexCv";
	@ConfigurationParameter(name = PARAM_ANWSER_ID_INDEX_CV, mandatory = false, defaultValue = "1")
	protected int answerIdIndexCv;

	public static final String PARAM_TEXT_INDEX_CV = "textIndexCv";
	@ConfigurationParameter(name = PARAM_TEXT_INDEX_CV, mandatory = false, defaultValue = "2")
	protected int textIndexCv;

	public static final String PARAM_SCORE_ID_INDEX_CV = "scoreIndexCv";
	@ConfigurationParameter(name = PARAM_SCORE_ID_INDEX_CV, mandatory = false, defaultValue = "3")
	protected int scoreIndexCv;


	public static final String PARAM_PROMPT_ID_INDEX_PLUS = "promptIdIndexPlus";
	@ConfigurationParameter(name = PARAM_PROMPT_ID_INDEX_PLUS, mandatory = false, defaultValue = "0")
	protected int promptIdIndexPlus;

	public static final String PARAM_ANWSER_ID_INDEX_PLUS = "answerIdIndexPlus";
	@ConfigurationParameter(name = PARAM_ANWSER_ID_INDEX_PLUS, mandatory = false, defaultValue = "1")
	protected int answerIdIndexPlus;

	public static final String PARAM_TEXT_INDEX_PLUS = "textIndexPlus";
	@ConfigurationParameter(name = PARAM_TEXT_INDEX_PLUS, mandatory = false, defaultValue = "2")
	protected int textIndexPlus;

	public static final String PARAM_SCORE_ID_INDEX_PLUS = "scoreIndexPlus";
	@ConfigurationParameter(name = PARAM_SCORE_ID_INDEX_PLUS, mandatory = false, defaultValue = "3")
	protected int scoreIndexPlus;

	String cvMarker = "_CV";
	String plusMarker = "_Plus";

	protected int currentIndex;    

	protected Queue<GenericDatasetItem> items;

	private Map<String, String> questions;
	private Map<String, String> targetAnswers;


	int promptIndex = 1;
	int answerIdIndex = 0;
	int textIndex = 2;
	int scoreIndex = 3;


	private Set<String> promptAnswerIds;
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException
	{
		items = new LinkedList<GenericDatasetItem>();
		questions = new HashMap<String, String>();
		targetAnswers = new HashMap<String, String>();
		promptAnswerIds = new HashSet<String>();

		try {
			inputFileURLCV = ResourceUtils.resolveLocation(inputFileStringCV, this, aContext);
			extract(inputFileURLCV, true);   
			inputFileURLPlus = ResourceUtils.resolveLocation(inputFileStringPlus, this, aContext);
			extract(inputFileURLPlus, false);   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentIndex = 0;
	}

	private void extract(URL inputFileURL, boolean isCV) throws UnsupportedEncodingException, IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						inputFileURL.openStream(),
						encoding
						)
				);
		int promptIndex = promptIdIndexCv;
		int answerIdIndex = answerIdIndexCv;
		int textIndex = textIndexCv;
		int scoreIndex = scoreIndexCv;
		if (isCV){
			promptIndex = promptIdIndexPlus;
			answerIdIndex = answerIdIndexPlus;
			textIndex = textIndexPlus;
			scoreIndex = scoreIndexPlus;
		}

		String nextLine;
		int counter = 0;
		if (ignoreFirstLine) {
			nextLine = reader.readLine();
		}			
		while ((nextLine = reader.readLine()) != null) {
			//	System.out.println("line: "+nextLine);
			String[] nextItem = nextLine.split(separator);
			String promptId = null;
			String answerId = null;
			String text      = null;
			String score    = "-1";

			if (nextItem.length>=4) {
				GenericDatasetItem newItem = null ;
				promptId  = nextItem[promptIndex];
				answerId = nextItem[answerIdIndex];
				if (isCV){
					answerId += cvMarker;
				} else {
					answerId += plusMarker;
				}

				text       = nextItem[textIndex];
				if (scoreMultiplicationFactor != 1){
					double scoreNumeric = 0.0;
					try {
						scoreNumeric = Double.parseDouble(nextItem[scoreIndex]);
					}						
					catch (NumberFormatException e){
						System.err.println("We cannot handle categorical values and scoremultiplication at the same time. please remove the score multiplication factor.");
						System.exit(-1);
					}
					double rawScore    = scoreMultiplicationFactor*scoreNumeric;
					if (rawScore % 1 == 0)	{
						score = String.valueOf((int) rawScore);
					} else {
						System.err.println("Problem processing score "+rawScore+" with multiplication factor " + scoreMultiplicationFactor
								+ "Your scores are not integers and you did not provide a suitable multiplication factor.");
						System.exit(-1);
					}
				} else {
					score = nextItem[3];
				}
				text = Utils.cleanString(text);
				if (nextItem.length >=5){
					targetAnswers.put(promptId, Utils.cleanString(nextItem[4]));
				}
				if (nextItem.length >=6){
					questions.put(promptId, Utils.cleanString(nextItem[5]));
				}
				if (excludedPromptSetId != null && excludedPromptSetId.equals(promptId)) {
					continue;
				}
				if (!requestedPromptId.equals("-1") && !requestedPromptId.equals(promptId)) {
					//System.out.println("There are " + countEmptyAnswers + " empty answers in " + requestedQuestionId);
					continue;
				}
				newItem = new GenericDatasetItem(promptId, answerId, text, score, promptId);
				//		System.out.println(newItem.toString());

				String promptAnswerId = promptId+"_"+answerId;
				if (promptAnswerIds.contains(promptAnswerId)){
					System.err.println("You cannot have two answers with the same id. Please check answer "+promptAnswerId);
					System.exit(-1);
				} else {
					promptAnswerIds.add(promptAnswerId);
					items.add(newItem);
					counter++;
				}
			}
		}
		System.out.println("Read "+counter+" items");
		System.out.println("Read "+items.size()+" items overall");
	}

	public boolean hasNext()
			throws IOException 
	{
		return !items.isEmpty();
	}

	@Override
	public void getNext(JCas jcas)
			throws IOException, CollectionException
	{
		GenericDatasetItem item = items.poll();
		getLogger().debug(item);
		String itemId = String.valueOf(item.getPromptId()+"_"+item.getAnswerId());	
		//	System.out.println("itemId: "+itemId);
		URL inputFileURL = inputFileURLCV;
		if (itemId.endsWith(plusMarker)){
			inputFileURL = inputFileURLPlus;
		}
		try
		{
			jcas.setDocumentLanguage(language);
			jcas.setDocumentText(item.getText());        	        	
			DocumentMetaData dmd = DocumentMetaData.create(jcas);
			dmd.setDocumentId(itemId); 
			dmd.setDocumentTitle(item.getText());
			dmd.setDocumentUri(inputFileURL.toURI().toString());
			dmd.setCollectionId(itemId);

		} 
		catch (URISyntaxException e) {
			throw new CollectionException(e);
		}

		LearnerAnswerWithReferenceAnswer learnerAnswer = new LearnerAnswerWithReferenceAnswer(jcas, 0, jcas.getDocumentText().length());
		learnerAnswer.setPromptId(item.getPromptId());
		StringArray ids = new StringArray(jcas, 1);
		// We only have one exactly target answer per learner, so we use the same id as for the prompt
		ids.set(0, String.valueOf(item.getPromptId()));
		learnerAnswer.setReferenceAnswerIds(ids);
		learnerAnswer.addToIndexes();

		TextClassificationTarget unit = new TextClassificationTarget(jcas, 0, jcas.getDocumentText().length());
		// will add the token content as a suffix to the ID of this unit 
		unit.setSuffix(itemId);
		unit.addToIndexes();      
		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas, 0, jcas.getDocumentText().length());
		outcome.setOutcome(item.getGrade());
		outcome.addToIndexes();
		currentIndex++;
	}



	public Progress[] getProgress()
	{
		return new Progress[] { new ProgressImpl(currentIndex, currentIndex, Progress.ENTITIES) };
	}


}

