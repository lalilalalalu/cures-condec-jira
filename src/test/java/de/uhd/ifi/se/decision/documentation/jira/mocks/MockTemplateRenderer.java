package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;

public class MockTemplateRenderer implements TemplateRenderer {

	@Override
	public void render(String templateName, Writer writer) throws RenderingException {

	}

	@Override
	public void render(String templateName, Map<String, Object> context, Writer writer)
			throws RenderingException {

	}

	@Override
	public String renderFragment(String fragment, Map<String, Object> context) throws RenderingException {
		return null;
	}

	@Override
	public boolean resolve(String templateName) {
		return false;
	}

}
