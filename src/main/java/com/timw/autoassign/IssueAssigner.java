package com.timw.autoassign;

import com.timw.autoassign.exception.AssigneeNotConfiguredException;
import com.timw.autoassign.exception.AuthorNotFoundException;
import com.timw.autoassign.exception.IssueNotAssignedException;
import com.timw.autoassign.exception.ResourceNotFoundException;
import com.timw.autoassign.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.issue.Issue;

import java.util.List;

/**
 * Created by twebster on 16/01/14.
 */
public class IssueAssigner {

    private static final String NL = System.getProperty("line.separator");
    private static final Logger LOG = LoggerFactory.getLogger(IssueAssigner.class);
    private static IssueAssignerProperties PROPS;
    private final  IssueClientWrapper issueClientWrapper;
    private SonarClient sonarClient;

    public IssueAssigner() throws Exception {
        PROPS = new IssueAssignerProperties();

        sonarClient = SonarClient.builder()
                .url(PROPS.getSonarUrl())
                .login(PROPS.getSonarAdminUsername())
                .password(PROPS.getSonarAdminPassword())
                .build();

        this.issueClientWrapper = new IssueClientWrapper(this.sonarClient, PROPS);
    }

    public static void main(final String[] args) throws Exception {
        final IssueAssigner app = new IssueAssigner();
        app.run();
    }

    public void run() {

        final List<Issue> issues = issueClientWrapper.getOpenIssuesCreatedToday();
        LOG.info(issues.size() + " issues found.");

        if (issues.size() < 1) {
            LOG.info("No issues found.");
        }

        for (final Issue issue : issues) {

            this.logIssue(issue);

            try {
                final String scmAuthor = getScmAuthor(issue);
                this.issueClientWrapper.assignToSonarUser(issue.key(), scmAuthor);
            }
            catch (final AuthorNotFoundException e) {
                assignToDefaultUser(issue);
            }
            catch (final UserNotFoundException u) {
                assignToDefaultUser(issue);
            }
        }
    }

    private void assignToDefaultUser(final Issue issue) {
        try {
            issueClientWrapper.assignToDefaultAssignee(issue);
        }
        catch (final AssigneeNotConfiguredException a) {
            LOG.error("Issue not assigned.");
        } catch (final IssueNotAssignedException e) {
            LOG.error("Issue not assigned.");
        }
    }

    private String getScmAuthor(final Issue issue) throws AuthorNotFoundException, ResourceNotFoundException {
        final ResourceFinder resourceFinder = new ResourceFinder(PROPS);
        final String scmAuthor = resourceFinder.getIssueAuthor(issue);
        LOG.info("SCM author: " + scmAuthor);
        return scmAuthor;
    }

    private void logIssue(final Issue issue) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Found issue: ").append(NL)
                .append("Project: ").append(issue.projectKey()).append(NL)
                .append("Component: ").append(issue.componentKey()).append(NL)
                .append("Issue ID: ").append(issue.key()).append(NL)
                .append("Issue status: ").append(String.valueOf(issue.status())).append(NL)
                .append("Line: ").append(issue.line()).append(NL)
                .append("Created on: ").append(issue.creationDate()).append(NL);

        LOG.info(builder.toString());
    }
}
