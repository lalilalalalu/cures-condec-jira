package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Class to connect to commits and code in git.
 */
public interface GitClient {

	/**
	 * @issue What is the best place to clone the git repo to?
	 * @issue To which directory does the Git integration for JIRA plug-in clone the
	 *        repo? Can we use this directory?
	 * @alternative APKeys.JIRA_PATH_INSTALLED_PLUGINS
	 */
	String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "repository" + File.separator;

	/**
	 * Retrieves the commits with the JIRA issue key in their commit message.
	 * 
	 * @param jiraIssueKey
	 *            JIRA issue key that is searched for in commit messages.
	 * @return commits with the issue key in their commit message as a list of
	 *         RevCommits.
	 */
	List<RevCommit> getCommits(String jiraIssueKey);

	/**
	 * Get a map of diff entries and the respective edit lists for a commit.
	 * 
	 * @param revCommit
	 *            commit as a RevCommit object.
	 * @return map of diff entries and respective edit lists.
	 */
	Map<DiffEntry, EditList> getDiff(RevCommit revCommit);

	/**
	 * Get a map of diff entries and the respective edit lists for all commits
	 * belonging to a JIRA issue.
	 * 
	 * @param revCommit
	 *            commit as a RevCommit object
	 * @return map of diff entries and respective edit lists
	 */
	Map<DiffEntry, EditList> getDiff(String jiraIssueKey);

	Map<DiffEntry, EditList> getDiff(List<RevCommit> commits);

	Map<DiffEntry, EditList> getDiff(RevCommit revCommitFirst, RevCommit revCommitLast);

	/**
	 * Get the jgit repository object.
	 * 
	 * @return jgit repository object.
	 */
	Repository getRepository();

	/**
	 * Get the path to the repository.
	 * 
	 * @return path to the repository as a File object.
	 */
	File getDirectory();

	/**
	 * Closes the repository.
	 */
	void closeRepo();

	/**
	 * Closes the repository and deletes its local files.
	 */
	void deleteRepo();

	/**
	 * Retrieves the JIRA issue key from a commit message
	 * 
	 * @param commitMessage
	 *            a commit message that should contain an issue key
	 * @return extracted JIRA issue key
	 * 
	 * @issue How to identify the JIRA issue key(s) in a commit message?
	 * @alternative This is a very simple method to detect the JIRA issue key as the
	 *              first word in the message and should be improved.
	 */
	static String getJiraIssueKey(String commitMessage) {
		if (commitMessage.contains(" ")) {
			String[] split = commitMessage.split("[:+ ]");
			return split[0];
		} else {
			return "";
		}
	}
}