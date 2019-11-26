package ch.iisresear.dssa.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.formula.functions.T;

import ch.iisresear.dssa.service.GateService;
import gate.util.GateException;

public class SimpleCondition implements Condition<String> {

	protected Set<String> clues;
	protected boolean all = false;
	GateService annotator = null;
	
	/**
	 * Constructor
	 * @param clues
	 * @param all
	 */
	public SimpleCondition(GateService a, Set<String> clues, boolean all) {
		this.clues = clues;
		this.all = all;
		this.annotator = a;
	}
	
	/**
	 * In SimpleCondition, we just check whether utterances contain the "clue" concepts (all or any, depending on the value of "all"
	 */
	public boolean satisfiedBy(Set<String> history, String utterance) {
		
		// empty conditions are always satisfied:
		if(clues.size()==0)return true;
		
		Set<String> utteranceConcepts = new HashSet<String>();
		if(utterance.length() > 0) {
			try {
				utteranceConcepts = annotator.getConcepts(utterance);
			}catch (GateException e) {
	            e.printStackTrace();
	        }
		}
		utteranceConcepts.addAll(history);
	
		// modify the list of uttered concepts such that it only contains the condition's clues (i.e. it is the intersection of both sets)
		utteranceConcepts.retainAll(clues);
		
		// if policy is "any": one match is enough; otherwise, size of the two sets must be equal
		if(!all)return (utteranceConcepts.size() > 0);
		else return (utteranceConcepts.size() == clues.size());
	}

	public Set<String> getClues() {
		return clues;
	}
	
	public String toString() {
		String ret = "any of ";
		if(all)ret = "all of ";
		ret += clues;
		return ret;
	}
}
