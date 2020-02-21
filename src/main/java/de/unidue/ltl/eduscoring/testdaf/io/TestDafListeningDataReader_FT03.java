package de.unidue.ltl.eduscoring.testdaf.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.unidue.ltl.escrito.core.types.LearnerAnswer;
import de.unidue.ltl.evaluation.core.EvaluationData;
import de.unidue.ltl.evaluation.core.EvaluationEntry;
import de.unidue.ltl.evaluation.measures.agreement.QuadraticallyWeightedKappa;

public class TestDafListeningDataReader_FT03 extends JCasCollectionReader_ImplBase {

	public static final String DEFAULT_LANGUAGE = "de";

	public static final String PARAM_INPUT_FILE = "InputFile";
	@ConfigurationParameter(name = PARAM_INPUT_FILE, mandatory = true)
	public String inputFileString;

	protected URL inputFileURL;

	public static final String PARAM_LANGUAGE = "Language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false, defaultValue = DEFAULT_LANGUAGE)
	public String language;

	public static final String PARAM_ENCODING = "Encoding";
	@ConfigurationParameter(name = PARAM_ENCODING, mandatory = false, defaultValue = "UTF-8")
	public String encoding;

	public static final String PARAM_SEPARATOR = "Separator";
	@ConfigurationParameter(name = PARAM_SEPARATOR, mandatory = false, defaultValue = "\t")
	public String separator;

	public static final String PARAM_QUESTION_ID = "QuestionId";
	@ConfigurationParameter(name = PARAM_QUESTION_ID, mandatory = false, defaultValue = "-1")
	protected String requestedQuestionId;

	public static final String PARAM_GRADE_TYPE = "GradeType";
	@ConfigurationParameter(name = PARAM_GRADE_TYPE, mandatory = true, defaultValue = ConfigurationParameter.NO_DEFAULT_VALUE)
	protected GradeType requestedGradeType;

	public enum GradeType {
		gesamt
	}


	public static final String[] itemIds = {
			"1.1", "1.2", "1.3", "1.4", "1.5",
			"2.1", "2.2", "2.3", "2.4",
			"5.1", "5.2", "5.3", "5.4"};

	protected int currentIndex = 0;

	protected Queue<TestDafListeningItem> items;

	protected BufferedReader reader;

	public Queue<TestDafListeningItem> getItems() {
		return items;
	}

	public void setItems(Queue<TestDafListeningItem> items) {
		this.items = items;
	}

	public boolean hasNext() throws IOException {
		return !items.isEmpty();
	}

	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		readItems(aContext);
	}

	protected void readItems(UimaContext aContext) throws ResourceInitializationException {
		if (!requestedQuestionId.equals("-1") && !requestedQuestionId.matches("\\d\\.\\d")) {
			getLogger().warn("Invalid questionId " + requestedQuestionId + " - using all documents");
			requestedQuestionId = "-1";
			System.exit(-1);
		}
		int countEmptyAnswers = 0;
		try {
			System.out.println("Encoding: "+encoding);
			inputFileURL = ResourceUtils.resolveLocation(inputFileString, this, aContext);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputFileURL.openStream(), encoding));
			String nextLine;
			items = new LinkedList<TestDafListeningItem>();
			ArrayList<Integer> annosGesamt = new ArrayList<Integer>();
			ArrayList<Integer> annos1 = new ArrayList<Integer>();
			ArrayList<Integer> annos2 = new ArrayList<Integer>();
			ArrayList<Integer> annos3 = new ArrayList<Integer>();
			ArrayList<Integer> annos4 = new ArrayList<Integer>();
			ArrayList<Integer> annos5 = new ArrayList<Integer>();
			ArrayList<Integer> annos6 = new ArrayList<Integer>();
			
			while (reader.ready()) {
				nextLine = reader.readLine();
				// skip the header
				if (nextLine.startsWith("Teilnehmer\t")) {
					nextLine = reader.readLine();
				}

				String[] nextItem = nextLine.split(separator);
				//System.out.println(nextItem[4]+"\t"+nextItem[5]+"\t"+nextItem[6]+"\t"+nextItem[7]+"\t"+nextItem[8]);

				String studentId = null;
				String questionId = null;
				String text = null;
				int grade = -1;
				// default value if nothing is annotated
				int annoGesamt = 0;
				int anno1 = 0;
				int anno2 = 0;
				int anno3 = 0;
				int anno4 = 0;
				int anno5 = 0;
				int anno6 = 0;
				
				if (nextItem.length == 10 || nextItem.length == 11) {
					studentId = nextItem[0];
					questionId = nextItem[1] + "." + nextItem[2];
					
					text = cutQuotation(nextItem[3]);
					if (!nextItem[4].equals("")){
						annoGesamt = Integer.parseInt(nextItem[4]);
					}
					if (!nextItem[5].equals("")){
						anno1 = Integer.parseInt(nextItem[5]);
					}
					if (!nextItem[6].equals("")){
						anno2 = Integer.parseInt(nextItem[6]);
					}
					if (!nextItem[7].equals("")){
						anno3 = Integer.parseInt(nextItem[7]);
					}
					if (!nextItem[8].equals("")){
						anno4 = Integer.parseInt(nextItem[8]);
					}
					if (!nextItem[9].equals("")){
						anno5 = Integer.parseInt(nextItem[9]);
					}
					if (!nextItem[10].equals("")){
						anno6 = Integer.parseInt(nextItem[10]);
					}
					switch(requestedGradeType){
					case gesamt:
						grade = annoGesamt;
						break;
					case b:
						grade = anno1;
						break;
					case d:
						grade = anno2;
						break;
					case i:
						grade = anno3;
						break;
					case l:
						grade = anno4;
						break;
					case r:
						grade = anno5;
						break;
					case s:
						grade = anno6;
						break;
					default:
						System.err.println("false grade type");
						break;						
					}
				}   else {
					System.err.println("format error with line "+nextLine+". Line has not the expected length. Found "+nextItem.length+" columns");
					System.out.println(nextLine);
					continue;
				}
				// if validEssaySetId is set, then skip if not equal with
				// current
				//System.out.println(questionId);
				if (!requestedQuestionId.equals("-1") && !requestedQuestionId.equals(questionId)) {
					//System.out.println("There are " + countEmptyAnswers + " empty answers in " + requestedQuestionId);
					continue;
				}
				if (grade == 8 || grade == 9) {
					countEmptyAnswers++;
					continue;
				}
				if (text.trim().length()==0) {
					countEmptyAnswers++;
					continue;
				}
				TestDafListeningItem newItem = new TestDafListeningItem(studentId, questionId, text, grade);
				//	System.out.println(newItem);
				items.add(newItem);
				annosGesamt.add(annoGesamt);
				annos1.add(anno1);
				annos2.add(anno2);
				annos3.add(anno3);
				annos4.add(anno4);
				annos5.add(anno5);
				annos6.add(anno6);
			}
			reader.close();
			
//			computeKappa(annos1, annos2, "Anno1, Anno2:\t");
//			computeKappa(annos1, annos3, "Anno1, Anno3\t");
//			computeKappa(annos1, annos4, "Anno1, Anno4\t");
//			computeKappa(annos1, annos5, "Anno1, Anno5\t");
//			
//			computeKappa(annos2, annos3, "Anno2, Anno3:\t");
//			computeKappa(annos2, annos4, "Anno2, Anno4\t");
//			computeKappa(annos2, annos5, "Anno2, Anno5\t");
//			
//			computeKappa(annos3, annos4, "Anno3, Anno4\t");
//			computeKappa(annos3, annos5, "Anno3, Anno5\t");
//			
//			computeKappa(annos4, annos5, "Anno4, Anno5\t");			
//			
//			computeKappa(annos1, annos_finalized, "Anno1, final:\t");			
//			computeKappa(annos2, annos_finalized, "Anno2, final:\t");
//			computeKappa(annos3, annos_finalized, "Anno3, final:\t");			
//			computeKappa(annos4, annos_finalized, "Anno4, final:\t");
//			computeKappa(annos5, annos_finalized, "Anno5, final:\t");			
			
			
			//System.exit(-1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new ResourceInitializationException(e);
		}
		System.out.println("There are " + countEmptyAnswers + " empty answers");
	}

	private void computeKappa(ArrayList<Integer> annos1, ArrayList<Integer> annos2, String label) {
		Collection<EvaluationEntry<Double>> entries = new ArrayList<EvaluationEntry<Double>>();
		for (int i = 0; i<annos1.size(); i++){
			entries.add(new EvaluationEntry<Double>(1.0*annos1.get(i), 1.0*annos2.get(i)));
		}
		EvaluationData<Double> data = new EvaluationData<Double>(entries);
		System.out.println(label+new QuadraticallyWeightedKappa<Double>(data).getResult());	
	}

	public String cutQuotation(String text) {
		if (text.startsWith("\"")) {
			return cutQuotation(text.substring(1, text.length()));
		} else if (text.endsWith("\"")) {
			return text.substring(0, text.length()-1);
		} else
			return text;
	}

	public void getNext(JCas jcas) throws IOException, CollectionException {
		TestDafListeningItem item = items.poll();
		getLogger().debug(item);
		String itemId = item.getQuestionId() + "_" + item.getStudentId();
		// System.out.println("item id: " + itemId);

		try {
			if (language != null) {
				jcas.setDocumentLanguage(language);
			} else {
				jcas.setDocumentLanguage(DEFAULT_LANGUAGE);
			}
			jcas.setDocumentText(item.getText());

			DocumentMetaData dmd = DocumentMetaData.create(jcas);
			dmd.setDocumentId(itemId);
			dmd.setDocumentTitle(itemId);
			dmd.setDocumentUri(inputFileURL.toURI().toString());
			dmd.setCollectionId(itemId);
		}

		catch (URISyntaxException e) {
			throw new CollectionException(e);
		}

		LearnerAnswer learnerAnswer = new LearnerAnswer(jcas, 0, jcas.getDocumentText().length());
		learnerAnswer.setPromptId(String.valueOf(item.getQuestionId()));
		learnerAnswer.addToIndexes();
		
		TextClassificationTarget unit = new TextClassificationTarget(jcas, 0, jcas.getDocumentText().length());
		// will add the token content as a suffix to the ID of this unit
		unit.setSuffix(itemId);
		unit.addToIndexes();

		TextClassificationOutcome outcome = new TextClassificationOutcome(jcas, 0, jcas.getDocumentText().length());
		outcome.setOutcome(Integer.toString(item.getGrade()));
		outcome.addToIndexes();

		currentIndex++;
	}

	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(currentIndex, currentIndex, Progress.ENTITIES) };
	}
}
