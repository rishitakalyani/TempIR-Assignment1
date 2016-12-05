package TempIr.AssignOne.SearchEngine;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

import java.util.List;

import org.apache.lucene.search.Explanation;

public class LMMercer extends LMSimilarity{
	 private final float lambda;
	 public LMMercer(float lambda) {
		    this.lambda = lambda;
		  }
	 @Override
	  protected float score(BasicStats stats, float freq, float docLen) {
	    float score= stats.getBoost() * (float)Math.log(
	            ((lambda) * freq) +
	            ((1-lambda) * ((LMStats)stats).getCollectionProbability()));
	    return score;
	  }
	  
	  @Override
	  protected void explain(List<Explanation> subs, BasicStats stats, int doc,
	      float freq, float docLen) {
	    if (stats.getBoost() != 1.0f) {
	      subs.add(Explanation.match(stats.getBoost(), "boost"));
	    }
	    subs.add(Explanation.match(lambda, "lambda"));
	    super.explain(subs, stats, doc, freq, docLen);
	  }
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}