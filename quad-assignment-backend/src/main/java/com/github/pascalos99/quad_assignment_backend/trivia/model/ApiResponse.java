package com.github.pascalos99.quad_assignment_backend.trivia.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponse(int response_code, List<QuestionAndAnswer> results) {	
	
	//// Response Codes:
	/** Returned results successfully. */
	public static final int SUCCESS 		= 0;
	/** Could not return results. The API doesn't have enough questions for your query.
	 * (Ex. Asking for 50 Questions in a Category that only has 20.) */
	public static final int NO_RESULTS 		= 1;
	/** Contains an invalid parameter. Arguments passed in aren't valid. (Ex. Amount = Five) */
	public static final int INVALID 		= 2;
	/** Session Token does not exist. */
	public static final int TOKEN_INVALID 	= 3;
	/** Session Token has returned all possible questions for the specified query.
	 * Resetting the Token is necessary. */
	public static final int TOKEN_EMPTY 	= 4;
	/** Too many requests have occurred. Each IP can only access the API once every 5 seconds. */
	public static final int TOO_FAST 		= 5;
}
