package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.documentation.jira.persistence.ConfigPersistence;

/**
 * @description Renders the administration page to change the plug-in configuration of a single project
 */
public class SettingsOfSingleProject extends AbstractSettingsServlet {

	private static final long serialVersionUID = 8699708658914306058L;
	private static final String TEMPLATEPATH = "templates/settingsForSingleProject.vm";

	@Inject
	public SettingsOfSingleProject(@ComponentImport UserManager userManager,
								   @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport TemplateRenderer renderer) {
		super(userManager, loginUriProvider, renderer);
	}

	protected boolean isValidUser(HttpServletRequest request) {
		String projectKey = request.getParameter("projectKey");

		String username = userManager.getRemoteUsername(request);
		if (isProjectAdmin(username, projectKey)) {
			return true;
		}
		return false;
	}

	private boolean isProjectAdmin(String username, String projectKey) {
		if (username == null || projectKey == null) {
			LOGGER.error("Username or project key in SettingsOfSingleProject is null.");
			return false;
		}
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username);
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);

		ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
		Collection<ProjectRole> roles = projectRoleManager.getProjectRoles(user, project);
		if (roles == null) {
			LOGGER.error("User roles are not set correctly.");
			return false;
		}
		for (ProjectRole role : roles) {
			if (role.getName().equalsIgnoreCase("Administrators")) {
				return true;
			}
		}
		return false;
	}

	protected String getTemplatePath() {
		return TEMPLATEPATH;
	}

	protected Map<String, Object> getVelocityParameters(HttpServletRequest request) {
		String projectKey = request.getParameter("projectKey");
		boolean isActivated = ConfigPersistence.isActivated(projectKey);
		boolean isIssueStrategy = ConfigPersistence.isIssueStrategy(projectKey);

		Map<String, Object> velocityParameters = new HashMap<String, Object>();
		velocityParameters.put("requestUrl", request.getRequestURL());
		velocityParameters.put("projectKey", projectKey);
		velocityParameters.put("isActivated", isActivated);
		velocityParameters.put("isIssueStrategy", isIssueStrategy);
		return velocityParameters;
	}
}