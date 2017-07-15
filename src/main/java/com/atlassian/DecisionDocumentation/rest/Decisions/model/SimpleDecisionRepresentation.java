package com.atlassian.DecisionDocumentation.rest.Decisions.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.atlassian.jira.issue.Issue;
/**
 * 
 * @author Ewald Rode
 * @description
 */
@XmlType (propOrder={"id","text"})
public class SimpleDecisionRepresentation {
	@XmlElement
    private Long id;
	
	@XmlElement
    private String text;
	
	public SimpleDecisionRepresentation(Issue issue){
		this.id = issue.getId();
		this.text = issue.getKey() + " / " + issue.getSummary() + " / " + issue.getIssueType().getName();
	}
}