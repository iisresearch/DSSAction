package ch.iisresear.dssa.action;

import java.util.Queue;

public class Constraint {
	protected String storyID;
	protected int orderNr;
	
	// the default question to ask when the constraint is chosen
	protected String defaultQuestion;
	
	// An ordered list of prompts. The first is the one that helps least...
	protected Queue<String> prompts;
	
	// A possible follow-up question to ask after Cs is satisfied (to keep the conversation going)
	protected String followUp;
	
	boolean mandatory = false;
	
	// relevance and satisfaction constraints:
	protected Condition Cr;
	protected Condition Cs;
	
	/**
	 * Constructor that initialises all fields	
	 * @param storyID
	 * @param orderNr
	 * @param defaultQuestion
	 * @param prompts
	 * @param followUp
	 * @param cr
	 * @param cs
	 */
	public Constraint(String storyID, int orderNr, String defaultQuestion, Queue<String> prompts, String followUp, boolean mandatory,
			Condition cr, Condition cs) {
		super();
		this.storyID = storyID;
		this.orderNr = orderNr;
		this.defaultQuestion = defaultQuestion;
		this.prompts = prompts;
		this.followUp = followUp;
		this.mandatory = mandatory;
		Cr = cr;
		Cs = cs;
	}
	
	
	public String getStoryID() {
		return storyID;
	}
	public int getOrderNr() {
		return orderNr;
	}
	public String getDefaultQuestion() {
		return defaultQuestion;
	}
	public String pollPrompt(){
		return prompts.poll();
	}
	public String getFollowUp() {
		return followUp;
	}
	public Condition getCr() {
		return Cr;
	}
	public Condition getCs() {
		return Cs;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}


	public String toString() {
		
		String ret = "==== Constraint " + storyID + "." + orderNr + " ===\n";
		ret	+= "Default question: " + defaultQuestion + "\n";
		ret += "Cr = " + Cr + "\nCs = " + Cs + "\n";
		ret += "prompts: " + prompts + "\n";
		ret += "follow-up question: " + followUp + "\n\n";
		
		return ret;
	}
}
