package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.extraction.TestCommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateDecisionKnowledgeElementSentenceUser extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeSentenceNull() {
		assertFalse(new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(null, null));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeSentenceElementNull() {
		Sentence sentence = new SentenceImpl();
		sentence.setId(1000);
		assertFalse(new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType2() {
		List<Sentence> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		Sentence sentence = comment.get(0);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3() {
		List<Sentence> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("ALTERNATIVE"));
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTagged() {
		List<Sentence> comment = TestCommentSplitter.getSentencesForCommentText("{issue} testobject {issue}");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		// Important that sentence object has no tags
		assertEquals("testobject", element.getDescription().trim());
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("{alternative} testobject {alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("some sentence in front. {issue} testobject {issue}");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(element.getDescription().trim(), "testobject");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {alternative} testobject {alternative}",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2() {
		List<Sentence> comment = TestCommentSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);
		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.ALTERNATIVE);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(element.getDescription(), " testobject ");
		assertEquals(element.getTypeAsString(), KnowledgeType.ALTERNATIVE.toString());
		assertEquals("some sentence in front. {alternative} testobject {alternative} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeTypeWithManualTaggedAndMoreSentences2AndArgument() {
		List<Sentence> comment = TestCommentSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);
		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(oldElement.getType(), KnowledgeType.ISSUE);

		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertEquals(" testobject ", element.getDescription());
		assertEquals(element.getTypeAsString(), "Pro");

		assertEquals("some sentence in front. {pro} testobject {pro} some sentence in the back.",
				ComponentAccessor.getCommentManager().getMutableComment((long) 0).getBody());
	}

	@Test
	@NonTransactional
	public void testUpdateKnowledgeType3WithArgument() {
		List<Sentence> comment = TestCommentSplitter.getSentencesForCommentText("first Comment");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);

		Sentence oldElement = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		oldElement.setType(KnowledgeType.PRO);
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(oldElement, null);
		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);
		assertTrue(element.getTypeAsString().equalsIgnoreCase("Pro"));
	}

	@Test
	@NonTransactional
	public void testUpdateSentenceBodyWhenCommentChanged() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("First sentences of two. Sencond sentences of two.");
		long id = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(0), null);
		long id2 = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);

		Sentence sentence = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id);

		sentence.setDescription("secondComment with more text");
		new JiraIssueCommentPersistenceManager("").updateDecisionKnowledgeElement(sentence, null);

		Sentence element = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(id2);
		assertTrue(element.getEndSubstringCount() != comment.get(1).getEndSubstringCount());
	}
}
