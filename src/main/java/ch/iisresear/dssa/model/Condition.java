/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.model;

import java.util.Set;

/**
 * Specifies the behaviour of conditions that are used to formulate constraints
 * @author hansfriedrich.witsch
 *
 */
public interface Condition<T> {
	
	/**
	 * A condition can be satisfied by (elements in) a list of previously satisfied conditions, (optionally) in combination with an utterance 
	 * @param history
	 * @param utterance
	 * @return
	 */
	public boolean satisfiedBy(Set<T> history, String utterance);
	
	public String toString();

}

