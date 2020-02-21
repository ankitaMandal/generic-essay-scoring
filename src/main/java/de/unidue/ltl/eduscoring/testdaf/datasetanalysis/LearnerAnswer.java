package de.unidue.ltl.eduscoring.testdaf.datasetanalysis;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;

public class LearnerAnswer {

	String text;
	String answerId;
	String promptId;
	boolean correct;
	Collection<String> tokens;

	static AnalysisEngine engine;

	static {
		try {
			engine = createEngine(createEngineDescription(BreakIteratorSegmenter.class));
		} catch (ResourceInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public String getText() {
		return text;
	}


	public String getAnswerId() {
		return answerId;
	}


	public String getPromptId() {
		return promptId;
	}


	public boolean isCorrect() {
		return correct;
	}


	public Collection<String> getTokens() {
		return tokens;
	}




	public LearnerAnswer(String promptId, String answerId, String text, boolean correct){
		this.promptId = promptId;
		this.answerId = answerId;
		this.text = text;
		this.correct = correct;
		try {
			JCas jcas = engine.newJCas();
			jcas.setDocumentLanguage("de");		
			jcas.setDocumentText(text);
			engine.process(jcas);
			tokens = new ArrayList<String>();
			for (Token token:  JCasUtil.select(jcas, Token.class)){
				tokens.add(token.getCoveredText());
			}
		} catch (ResourceInitializationException | AnalysisEngineProcessException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
