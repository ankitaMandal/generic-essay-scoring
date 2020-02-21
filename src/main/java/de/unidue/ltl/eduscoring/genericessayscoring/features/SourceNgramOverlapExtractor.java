package de.unidue.ltl.eduscoring.genericessayscoring.features;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.dkpro.tc.api.exception.TextClassificationException;
import org.dkpro.tc.api.features.Feature;
import org.dkpro.tc.api.features.FeatureExtractor;
import org.dkpro.tc.api.features.FeatureExtractorResource_ImplBase;
import org.dkpro.tc.api.features.FeatureType;
import org.dkpro.tc.api.type.TextClassificationTarget;
import org.dkpro.tc.features.ngram.util.NGramUtils;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringListIterable;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class SourceNgramOverlapExtractor 
extends FeatureExtractorResource_ImplBase
implements FeatureExtractor{

		
	public static final String PARAM_SOURCE_NGRAM_LOCATION = "locationOfSourceNgram";
	@ConfigurationParameter(name = PARAM_SOURCE_NGRAM_LOCATION, mandatory = true)
	private String locationOfSourceNgram;

	public static final String PARAM_LABEL_NAME = "LabelName";
	@ConfigurationParameter(name = PARAM_LABEL_NAME, mandatory = true)
	private String labelName;

	public static final String PARAM_MAX_N = "maxN";
	@ConfigurationParameter(name = PARAM_MAX_N, mandatory = true)
	private int maxN;

	public static final String PARAM_MIN_N = "minN";
	@ConfigurationParameter(name = PARAM_MIN_N, mandatory = true)
	private int minN;

	public static final String PARAM_USE_POS = "usePOS";
	@ConfigurationParameter(name = PARAM_USE_POS, mandatory = false)
	private boolean usePOS;
	
	public static final String PARAM_LOWERCASE_NGRAMS = "lowerNGRAMS";
	@ConfigurationParameter(name = PARAM_LOWERCASE_NGRAMS, mandatory = true)
	private static boolean lowerNGRAMS;
	

	public static final String PARAM_REQUIRED_POS = "contentPOS";
	@ConfigurationParameter(name = PARAM_REQUIRED_POS, mandatory = false)
	private static String requiredPOS;
	private static String contentPOS="ADJA,ADJD,ADV,NN,NE,VVFIN,VVIMP,VVINF,VVIZU,VVPP,VAFIN,VAIMP,VAINF,VAPP,VMFIN,VMINF,VMPP";
	
	public static final String PARAM_USE_LEMMA = "useLemmas";
	@ConfigurationParameter(name = PARAM_USE_LEMMA, mandatory = false)
	private static boolean useLemmas;
	
	private JCas sourceTextJcas; 
	private FrequencyDistribution<String> sourceFD;
  

	public String getSourceText()  throws IOException {
		BufferedReader br = new BufferedReader( new InputStreamReader(
                new FileInputStream(locationOfSourceNgram), "UTF8"));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }	
	}
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
			throws ResourceInitializationException {
	
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}
		//read the source text
		try {
		BufferedReader br = new BufferedReader( new InputStreamReader(
                new FileInputStream(locationOfSourceNgram), "UTF8"));
	    
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        String sourceText = sb.toString();
	        br.close();
	        AnalysisEngineDescription segmenter = createEngineDescription(BreakIteratorSegmenter.class);
			AnalysisEngineDescription posTagger = createEngineDescription(OpenNlpPosTagger.class);
			AnalysisEngineDescription lemmatizer = createEngineDescription(NoOpAnnotator.class);
			AnalysisEngineDescription description = createEngineDescription(segmenter,posTagger,lemmatizer);
			AnalysisEngine engine = createEngine(description); 	
			sourceTextJcas = engine.newJCas();
			sourceTextJcas.setDocumentLanguage("de");		
			sourceTextJcas.setDocumentText(sourceText);
			engine.process(sourceTextJcas);
			sourceFD= getDocumentNgrams(sourceTextJcas,minN, maxN); 
	    } 
	    catch (IOException | AnalysisEngineProcessException | TextClassificationException e) {
			e.printStackTrace();
		} 
		
	      
	    
		return true;
	}

	@Override
	public Set<Feature> extract(JCas aJCas, TextClassificationTarget aTarget) throws TextClassificationException {
		int i=0;
		double score1= 0.0;
		double score2= 0.0;
		try {
			
					
					FrequencyDistribution<String> learnerFD= getDocumentNgrams(aJCas,minN, maxN);
					Set learnerFDSet=learnerFD.getKeys();
					double learnerFDSetSize= learnerFDSet.size();
					Set sourceFDSet=sourceFD.getKeys();
					double sourceFDSetSize= sourceFDSet.size();
					SetView<String> overlap = Sets.intersection(learnerFDSet, sourceFDSet);
					double overlapSize= overlap.size();
//					System.out.println("Overlap:"+overlap);
//					System.out.println("overlap:"+overlap.size());
//					System.out.println("learnerFDSet:"+learnerFDSet.size());
//					System.out.println("sourceFDSet:"+sourceFDSet.size());
					if(sourceFDSetSize > 0)
						score1=(overlapSize/sourceFDSetSize);
					if(learnerFDSetSize > 0)
						score2=(overlapSize)/(learnerFDSetSize);
					Set<Feature> featList = new HashSet<Feature>();
					featList.add(new Feature("OVERLAP_RATIO_WITH_SOURCE",score1, FeatureType.NUMERIC));
					featList.add(new Feature("OVERLAP_RATIO_WITH_LEARNER_ESSAY",score2, FeatureType.NUMERIC));
					System.out.println(featList);					
					return featList;
		} catch (CASRuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static FrequencyDistribution<String> getDocumentNgrams(JCas jcas, int minN, int maxN) throws TextClassificationException{
		
		FrequencyDistribution<String> documentNgrams = new FrequencyDistribution<String>();
		for (Sentence s : JCasUtil.select(jcas, Sentence.class)) {
//			Set<String> pos_strings = new HashSet<String>();
			List<String> strings = new ArrayList<String>();;
			List<String> postags = new ArrayList<String>();;
			for (Token t : JCasUtil.select(jcas, Token.class))
			{
				try
				{
					postags.add(t.getPos().getPosValue());
					strings.add(t.getCoveredText());
				}
				catch (NullPointerException e) {
					System.err.println("Couldn't read lemma value for token \"" + t.getCoveredText() + "\"");
				}
			}
			
		
			for (List<String> ngram : new NGramStringListIterable(strings, minN, maxN)) { 
		
				 if(hasContentWord(ngram,strings,postags)) {											//consider ngrams with at least one content word
					String ngramString = StringUtils.join(ngram, "_");
					if(lowerNGRAMS == true)													
						ngramString.toLowerCase();
//					if(useLemmas == true)													
//						ngramString=ngramString.getLemmas().getLemmaValue();  							//Check this with Andrea
					documentNgrams.inc(ngramString);
				}
			}
		}
		return documentNgrams;
	}
	private static boolean hasContentWord(List<String> ngram, List<String> strings, List<String> postags) {
			int index=Collections.indexOfSubList(strings , ngram);
			int contentWords=0;
//			System.out.println("strings: "+strings);
//			System.out.println("postags: "+postags);
//			System.out.println("ngram: "+ngram);
//			System.out.println(index);
			for(int i=index;i<(index+ngram.size());i++){
				if(StringUtils.containsIgnoreCase(contentPOS, postags.get(i)))
					contentWords++;						
			}
//			System.out.println(contentWords);
//			
//			System.out.println(contentPOS);
			if(contentWords>0)
				return true;
			else
				return false;	
		
	}
}