package TempIr.AssignOne.SearchEngine;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.lucene.search.Explanation;

public class LMDirichlet extends LMSimilarity{

	 
	  private final float mu;
	  
	 
	  
	  // Instantiates the similarity with the provided &mu; parameter.
	  public LMDirichlet(float mu) {
	    this.mu = mu;
	  }

	 
	  @Override
	  protected float score(BasicStats stats, float freq, float docLen) {
	    float score =  stats.getBoost()*(float)(Math.log(1000*(freq +
	        (mu * ((LMStats)stats).getCollectionProbability()))/(docLen + mu)));
	    return score;
	  }
	  
	  @Override
	  protected void explain(List<Explanation> subs, BasicStats stats, int doc,
	      float freq, float docLen) {
	    if (stats.getBoost() != 1.0f) {
	      subs.add(Explanation.match(stats.getBoost(), "boost"));
	    }

	    subs.add(Explanation.match(mu, "mu"));
	    Explanation weightExpl = Explanation.match(
	        (float)(Math.log(freq +
	    	        (mu * ((LMStats)stats).getCollectionProbability())-Math.log(docLen + mu))),
	        "term weight");
	    subs.add(weightExpl);
	    
	    super.explain(subs, stats, doc, freq, docLen);
	  }

	  /** Returns the &mu; parameter. */
	  public float getMu() {
	    return mu;
	  }
	  
	  @Override
	  public String getName() {
	    return String.format(Locale.ROOT, "Dirichlet(%f)", getMu());
	  }
	  

	
}