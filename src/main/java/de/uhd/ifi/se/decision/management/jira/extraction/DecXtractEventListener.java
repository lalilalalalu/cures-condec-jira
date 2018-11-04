package de.uhd.ifi.se.decision.management.jira.extraction;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.connector.ViewConnector;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;

/**
 * Triggers the decXtract related function when some changes to comments are
 * made.
 */
@Component
public class DecXtractEventListener implements InitializingBean, DisposableBean {

	@JiraImport
	private final EventPublisher eventPublisher;

	private String projectKey;

	private IssueEvent issueEvent;

	@Autowired
	public DecXtractEventListener(@JiraImport EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Called when the plugin has been enabled.
	 * 
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}

	/**
	 * Called when the plugin is being disabled or removed.
	 * 
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

	@EventListener
	public void onIssueEvent(IssueEvent issueEvent) {
		this.issueEvent = issueEvent;
		this.projectKey = issueEvent.getProject().getKey();
		Long eventTypeId = issueEvent.getEventTypeId();
		if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
			handleNewComment(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));
		}
		if (eventTypeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
			handleDeleteComment(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));
		}
		if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
			handleEditComment(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));
		}
		if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
			handleDeleteIssue(new DecisionKnowledgeElementImpl(issueEvent.getIssue()));
		}
	}
	
	private void handleDeleteIssue(DecisionKnowledgeElementImpl decisionKnowledgeElement) {
		ActiveObjectsManager.cleanSentenceDatabaseForProject(this.projectKey);
		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId() + "");
	}

	private void handleEditComment(DecisionKnowledgeElementImpl decisionKnowledgeElement) {
		ActiveObjectsManager.checkIfCommentBodyHasChangedOutsideOfPlugin(new CommentImpl(issueEvent.getComment()));
		new ViewConnector(this.issueEvent.getIssue(), false);
		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId() + "");
	}

	private void handleDeleteComment(DecisionKnowledgeElementImpl decisionKnowledgeElement) {
		ActiveObjectsManager.cleanSentenceDatabaseForProject(this.projectKey);
		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId() + "");
	}

	private void handleNewComment(DecisionKnowledgeElementImpl decisionKnowledgeElement) {
		new ViewConnector(this.issueEvent.getIssue(), false);
		ActiveObjectsManager.createLinksForNonLinkedElementsForIssue(decisionKnowledgeElement.getId() + "");
	}

}