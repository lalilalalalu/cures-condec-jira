package de.uhd.ifi.se.decision.management.jira.extraction.view.macros;

import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public abstract class AbstractKnowledgeClassificationMacro extends BaseMacro {

	@Override
	public String execute(Map<String, Object> parameters, String body, RenderContext renderContext)
			throws MacroException {
		return body;
	}

	protected String execute(Map<String, Object> parameters, String body, RenderContext renderContext,
			String knowledgeType, String colorCode) throws MacroException {
		if (!ConfigPersistenceManager.isKnowledgeExtractedFromIssues(getProjectKey(renderContext))) {
			return body;
		}
		if (Boolean.TRUE.equals(renderContext.getParam(IssueRenderContext.WYSIWYG_PARAM))) {
			return putTypeInBrackets(knowledgeType) + body + putTypeInBrackets(knowledgeType);
		}
		String newBody = reformatCommentBody(body);
		String icon = "<img src='" + ComponentGetter.getUrlOfImageFolder()
				+ addPrefixForArgumentsForArguments(knowledgeType) + ".png" + "'>";
		String contextMenuCall = getContextMenuCall(renderContext, newBody, WordUtils.capitalize(knowledgeType));
		return icon + "<span " + contextMenuCall + "style='background-color:" + colorCode + "'>" + newBody + "</span>";
	}

	private String addPrefixForArgumentsForArguments(String knowledgeType) {
		if (knowledgeType.equalsIgnoreCase("con") || knowledgeType.equalsIgnoreCase("pro")) {
			return "argument_" + knowledgeType;
		}
		return knowledgeType;
	}

	@Override
	public RenderMode getBodyRenderMode() {
		return RenderMode.allow(RenderMode.F_ALL);
	}

	@Override
	public boolean hasBody() {
		return true;
	}

	/**
	 * Static function for other Macro Classes
	 * 
	 * @param inputBody
	 * @return Body without html p tags
	 */
	public static String reformatCommentBody(String inputBody) {
		String body = inputBody.replace("<p>", "");
		body = body.replace("</p>", "");
		while (body.startsWith(" ")) {
			body = body.substring(1);
		}
		while (body.endsWith(" ")) {
			body = body.substring(0, body.length() - 1);
		}
		return body;
	}

	/**
	 * Static function for other Macro Classes
	 * 
	 * @param renderContext
	 * @return
	 */
	protected String getProjectKey(RenderContext renderContext) {
		return renderContext.getParams().get("jira.issue").toString().split("-")[0];
	}

	/**
	 * Static function for other Macro Classes
	 * 
	 * @param renderContext
	 * @param body
	 * @param type
	 * @return the js context menu call for comment tab panel
	 */
	protected String getContextMenuCall(RenderContext renderContext, String body, String type) {
		long id = 0;
		if (renderContext.getParams().get("jira.issue") instanceof IssueImpl) {
			id = ActiveObjectsManager.getIdOfSentenceForMacro(body.replace("<p>", "").replace("</p>", ""),
					((IssueImpl) (renderContext.getParams().get("jira.issue"))).getId(), type,
					getProjectKey(renderContext));
		}
		if (id == 0) {
			// LOGGER.debug("No sentence object found for: " + body);
			return "";
		}
		return "oncontextmenu=\"conDecContextMenu.createContextMenuForSentences(this.offsetLeft, this.offsetTop, " + id
				+ "); return false;\"";
	}

	protected String putTypeInBrackets(String type) {
		return "\\{" + type + "}";
	}
}