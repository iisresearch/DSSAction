/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.model;

import ch.iisresear.dssa.service.GateService;
import gate.util.GateException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Story {
	private List<Constraint> allConstraints;
	private Set<Constraint> coveredConstraints = new HashSet<Constraint>();
	private Set<String> coveredConcepts = new HashSet<String>();
	private GateService annotator;
	
	// this variable is used to memorize a situation where a constraint has not yet been satisfied, and we wish to help the student satisfy it using the prompts.
	private Constraint curConstraint = null;
	
	/**
	 * Constructor that reads the story from a csv file.
	 * @param storyFile
	 */
	public Story(File storyFile, GateService annotator) {
		
		this.annotator = annotator;
		allConstraints = new ArrayList<Constraint>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(storyFile));
			String strLine = br.readLine();
						
			while ((strLine = br.readLine()) != null)   {
				
				// each line contains full information about exactly one constraint:
				String[] parts = strLine.split(";");
				String storyID = parts[0];
				int orderNr = Integer.parseInt(parts[1]);
				boolean mandatory = parts[2].equals("mandatory");
				String defaultQuestion = parts[3];
				String prompt1 = parts[4];
				String prompt2 = parts[5];
				Queue<String> prompts = new LinkedList<String>();
				prompts.add(prompt1); prompts.add(prompt2);
				String followUp = "";
				if(parts.length > 10)followUp = parts[10];
				
				// relevance and satisfaction conditions:
				String CrStr = parts[6];
				Set<String> CrClues = new HashSet<String>(Arrays.asList(CrStr.split(",")));
				if(CrClues.contains(""))CrClues.remove("");
				boolean CrAll = parts[7].equals("all");
				SimpleCondition Cr = new SimpleCondition(annotator, CrClues, CrAll);
				
				String CsStr = parts[8];
				Set<String> CsClues = new HashSet<String>(Arrays.asList(CsStr.split(",")));
				if(CsClues.contains(""))CsClues.remove("");
				boolean CsAll = parts[9].equals("all");
				SimpleCondition Cs = new SimpleCondition(annotator, CsClues, CsAll);
				
				Constraint curConstraint = new Constraint(storyID, orderNr, defaultQuestion, prompts, followUp, mandatory, Cr, Cs);
				allConstraints.add(curConstraint);
			}
		}catch(Exception e) {e.printStackTrace();}
		
		// before we start a dialog: move the first mandatory constraint into queue
		for(Constraint constraint : allConstraints) {
			if(constraint.isMandatory() && curConstraint == null)curConstraint = constraint;
		}
	}
	
	/**
	 * goes through the constraints and identifies the next "curConstraint". It will be the one with smalles order number which
	 * a) has not yet been covered
	 * b) has its relevance condition satisfied by the concepts covered so far OR 
	 * c) is mandatory
	 * The next constraint may also be a mandatory constraint whose relevance condition is not satisfied; such constraints are only considered if no other constraints are available
	 */
	private Constraint updateCurConstraint() {
		
		// first run: try to find one whose relevance condition is satisfied by the coveredConcepts
		for(Constraint c : allConstraints) {
			if(!coveredConstraints.contains(c) && c.getCr().satisfiedBy(coveredConcepts, ""))return c; 
		}
		
		// if we did not find any constraint that way, we take the first mandatory constraint that has not yet been covered:
		for(Constraint c : allConstraints) {
			if(!coveredConstraints.contains(c) && c.isMandatory())return c; 
		}
		
		// if not even a mandatory constraint is left, we return null (which will end the conversation):
		return null;
	}
	
	public String getNextResponse(String utterance) {
		
		String ret = "";
		
		// for the further flow of the conversation, add the concepts found in the current utterance to our memory
		if(utterance.length()>0) {
			
			try {
				coveredConcepts.addAll(annotator.getConcepts(utterance));
			}catch (GateException e) {
	            e.printStackTrace();
	        }
		}
		
		// if we are currently trying to satisfy a constraint, we first check if the current utterance satisfies that constraint:
		if(curConstraint != null) {
			
			// if user has now satisfied the constraint, set curConstraint to null, move on to next constraint
			if(utterance.length() == 0)return curConstraint.getDefaultQuestion();
			
			if(curConstraint.getCs().satisfiedBy(new HashSet<SimpleCondition>(), utterance)) {
				coveredConstraints.add(curConstraint);
				
				// add the clue concepts of Cr and Cs to coveredConcepts
				coveredConcepts.addAll(((SimpleCondition)curConstraint.getCr()).getClues());
				coveredConcepts.addAll(((SimpleCondition)curConstraint.getCs()).getClues());
				
				if(curConstraint.getFollowUp() != null && curConstraint.getFollowUp().length() > 0) {
					String followUp = curConstraint.getFollowUp();
					curConstraint = null;
					
					try {
						coveredConcepts.addAll(annotator.getConcepts(followUp));
					}catch (GateException e) {
			            e.printStackTrace();
			        }
					return followUp;
				}
				curConstraint = null;
			}
			else {
		
				// if not, and we still have prompts, we return the next prompt
				// otherwise we go to the next constraint...
				String nextPrompt = curConstraint.pollPrompt();
				if(nextPrompt != null && nextPrompt.length() > 0)ret = nextPrompt;
				
				// if no prompts are left: go to the next constraint...
				else {
					coveredConstraints.add(curConstraint);
					curConstraint = null;
				}
			}
		}
		
		// get the next contraint; see updateCurConstraint() for details
		if(curConstraint == null){
			
			// retrieve the next constraint:
			curConstraint = updateCurConstraint();
			
			if(curConstraint != null) {
				ret =  curConstraint.getDefaultQuestion();
			}
			else {
				ret = "Thanks for talking to me. Is there something that you want to tell my creators, regarding how I might be improved?";
			}
		}
		
		return ret;
	}
}
