package de.uhd.ifi.se.decision.management.jira;

import java.util.ArrayList;
import java.util.Locale;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.jira.user.util.UserManager;

import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.velocity.VelocityManager;
import de.uhd.ifi.se.decision.management.jira.mocks.*;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestSetUp {
	private ProjectManager projectManager;
	private IssueManager issueManager;
	private ConstantsManager constantsManager;

	public void initialization() {
		projectManager = new MockProjectManager();
		issueManager = new MockIssueManagerSelfImpl();
		constantsManager = new MockConstantsManager();
		IssueService issueService = new MockIssueService();

		UserManager userManager = new MockUserManager();
		ApplicationUser user = new MockApplicationUser("NoFails");
		ApplicationUser user2 = new MockApplicationUser("WithFails");
		ApplicationUser user3 = new MockApplicationUser("NoSysAdmin");
		ApplicationUser user4 = new MockApplicationUser("SysAdmin");
		((MockUserManager) userManager).addUser(user);
		((MockUserManager) userManager).addUser(user2);
		((MockUserManager) userManager).addUser(user3);
		((MockUserManager) userManager).addUser(user4);

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager())
				.addMock(IssueService.class, issueService).addMock(ProjectManager.class, projectManager)
				.addMock(UserManager.class, userManager).addMock(ConstantsManager.class, constantsManager)
				.addMock(ProjectRoleManager.class, new MockProjectRoleManager())
				.addMock(VelocityManager.class, new MockVelocityManager())
				.addMock(VelocityParamFactory.class, new MockVelocitryParamFactory());

		creatingProjectIssueStructure();
	}

	private void creatingProjectIssueStructure() {
		Project project = new MockProject(1, "TEST");
		((MockProject) project).setKey("TEST");
		((MockProjectManager) projectManager).addProject(project);

		ArrayList<KnowledgeType> types = new ArrayList<>();

		types.add(KnowledgeType.OTHER);
		types.add(KnowledgeType.DECISION);
		types.add(KnowledgeType.QUESTION);
		types.add(KnowledgeType.ISSUE);
		types.add(KnowledgeType.GOAL);
		types.add(KnowledgeType.SOLUTION);
		types.add(KnowledgeType.ALTERNATIVE);
		types.add(KnowledgeType.CLAIM);
		types.add(KnowledgeType.CONTEXT);
		types.add(KnowledgeType.ALTERNATIVE);
		types.add(KnowledgeType.CONSTRAINT);
		types.add(KnowledgeType.IMPLICATION);
		types.add(KnowledgeType.ASSESSMENT);
		types.add(KnowledgeType.ARGUMENT);
		types.add(KnowledgeType.PROBLEM);

		for (int i = 2; i < types.size() + 2; i++) {
			if (types.get(i - 2).toString().equals("Problem")) {
				MutableIssue issue = new MockIssue(30, "TEST-" + 30);
				((MockIssue) issue).setProjectId(project.getId());
				issue.setProjectObject(project);
				IssueType issueType = new MockIssueType(i, types.get(i - 2).toString().toLowerCase(Locale.ENGLISH));
				((MockConstantsManager) constantsManager).addIssueType(issueType);
				issue.setIssueType(issueType);
				issue.setSummary("Test");
				((MockIssueManagerSelfImpl) issueManager).addIssue(issue);
			} else {
				MutableIssue issue = new MockIssue(i, "TEST-" + i);
				((MockIssue) issue).setProjectId(project.getId());
				issue.setProjectObject(project);
				IssueType issueType = new MockIssueType(i, types.get(i - 2).toString().toLowerCase(Locale.ENGLISH));
				((MockConstantsManager) constantsManager).addIssueType(issueType);
				issue.setIssueType(issueType);
				issue.setSummary("Test");
				((MockIssueManagerSelfImpl) issueManager).addIssue(issue);
				if (i > types.size() - 4) {
					issue.setParentId((long) 3);
				}
			}
		}
		MutableIssue issue = new MockIssue(50, "TEST-50");
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(50, "Class");
		((MockConstantsManager) constantsManager).addIssueType(issueType);
		issue.setIssueType(issueType);
		issue.setSummary("Test");
		((MockIssueManagerSelfImpl) issueManager).addIssue(issue);
		issue.setParentId((long) 3);
	}
}