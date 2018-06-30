package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.documentation.jira.persistence.AbstractPersistenceStrategy;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * Model class for a graph of decision knowledge elements
 */
@JsonAutoDetect
public class GraphImpl implements Graph {

	private AbstractPersistenceStrategy abstractPersistenceStrategy;
	private List<Long> linkIds;
	private DecisionKnowledgeElement rootElement;

	public GraphImpl() {
		linkIds = new ArrayList<>();
	}

	public GraphImpl(String projectKey) {
		this();
		StrategyProvider strategyProvider = new StrategyProvider();
		this.abstractPersistenceStrategy = strategyProvider.getStrategy(projectKey);
	}

	public GraphImpl(String projectKey, String rootElementKey) {
		this(projectKey);
		this.rootElement = abstractPersistenceStrategy.getDecisionKnowledgeElement(rootElementKey);
	}

	public GraphImpl(DecisionKnowledgeElement rootElement) {
		this(rootElement.getProjectKey());
		this.rootElement = rootElement;
	}

	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();
		linkedElements.addAll(this.getElementsLinkedWithOutwardLinks(element));
		linkedElements.addAll(this.getElementsLinkedWithInwardLinks(element));
		return linkedElements;
	}

	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();

		if (element == null) {
			return linkedElements;
		}

		List<Link> outwardIssueLinks = abstractPersistenceStrategy.getOutwardLinks(element);
		for (Link link : outwardIssueLinks) {
			if (!linkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationObject();
				if (outwardElement != null) {
					linkIds.add(link.getLinkId());
					linkedElements.add(outwardElement);
				}
			}
		}

		return linkedElements;
	}

	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();

		if (element == null) {
			return linkedElements;
		}

		List<Link> inwardIssueLinks = abstractPersistenceStrategy.getInwardLinks(element);
		for (Link link : inwardIssueLinks) {
			if (!linkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement inwardElement = link.getSourceObject();
				if (inwardElement != null) {
					linkIds.add(link.getLinkId());
					linkedElements.add(inwardElement);
				}
			}
		}

		return linkedElements;
	}

	public DecisionKnowledgeElement getRootElement() {
		return rootElement;
	}

	public void setRootElement(DecisionKnowledgeElement rootElement) {
		this.rootElement = rootElement;
	}
}