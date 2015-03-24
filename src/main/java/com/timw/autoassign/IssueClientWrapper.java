package com.timw.autoassign;

import com.timw.autoassign.exception.AssigneeNotConfiguredException;
import com.timw.autoassign.exception.IssueNotAssignedException;
import com.timw.autoassign.exception.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.issue.Issue;
import org.sonar.wsclient.issue.IssueClient;
import org.sonar.wsclient.issue.IssueQuery;
import org.sonar.wsclient.user.User;

import java.util.*;

/**
 * Created by twebster on 18/01/14.
 */
public class IssueClientWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(IssueClientWrapper.class);
    private final IssueClient issueClient;
    private final IssueAssignerProperties issueAssignerProperties;
    private final SonarClient sonarClient;
    private static final Map<String, User> USER_CACHE = new HashMap<String, User>();

    public IssueClientWrapper(final SonarClient sonarClient, final IssueAssignerProperties issueAssignerProperties) {
        this.sonarClient = sonarClient;
        this.issueClient = sonarClient.issueClient();
        this.issueAssignerProperties = issueAssignerProperties;
    }

    public List<Issue> getOpenIssuesCreatedToday() {
        final IssueQuery issueQuery = IssueQuery.create()
                .assigned(false)
                .componentRoots(this.issueAssignerProperties.getIncludeProjects())
                .statuses(org.sonar.api.issue.Issue.STATUS_OPEN)
                .createdAfter(yesterdayAsDate());

        return issueClient.find(issueQuery).list();
    }

    public void assignToSonarUser(final String issueKey, final String scmAuthor) throws UserNotFoundException {
        final String superAssignee = this.issueAssignerProperties.getOverrideAssignee();

        if (StringUtils.isNotEmpty(superAssignee)) {
            LOG.info("Super assignee is configured.");
            LOG.info("Assigning issue [" + issueKey + "] to super assignee [" + superAssignee + "]");
            this.assign(issueKey, superAssignee);
        }
        else {
            final String sonarUser = this.getSonarUser(scmAuthor);
            this.assign(issueKey, sonarUser);
        }
    }

    public void assignToDefaultAssignee(final Issue issue) throws AssigneeNotConfiguredException, IssueNotAssignedException {

        LOG.info("Attempting to assign issue [" + issue.key() + "] to default assignee.");
        final String defaultAssignee = this.issueAssignerProperties.getDefaultAssignee();

        if (StringUtils.isEmpty(defaultAssignee)) {
            LOG.error("Default assignee not configured, not assigning issue: [" + issue.key() + "]");
            throw new AssigneeNotConfiguredException("Default assignee not configured.");
        }

        try {
            final String defaultSonarAssignee = this.getSonarUser(defaultAssignee);
            LOG.info("Found default assignee [" + defaultSonarAssignee + "] in Sonar.");
            this.assign(issue.key(), defaultSonarAssignee);
        }
        catch (final UserNotFoundException e) {
            LOG.error("Default Sonar assignee [" + defaultAssignee + "] not found.");
            throw new IssueNotAssignedException(issue);
        }
    }

    private void assign(final String issueKey, final String sonarUser) {
        LOG.info("Assigning issue [" + issueKey + "] to assignee [" + sonarUser + "]");
        this.issueClient.assign(issueKey, sonarUser);
    }

    private Date yesterdayAsDate() {
        final Calendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        return yesterday.getTime();
    }

    private String getSonarUser(final String username) throws UserNotFoundException {
        User sonarUser = USER_CACHE.get(username);

        if (sonarUser == null) {
            final UserClientWrapper userClientWrapper = new UserClientWrapper(this.sonarClient);
            sonarUser = userClientWrapper.findUser(username);
            LOG.info("Found Sonar user: " + sonarUser.login());
            USER_CACHE.put(username, sonarUser);
        }
        else {
            LOG.info("Found sonar user [" + sonarUser.login() + "] in cache.");
        }

        return sonarUser.login();
    }
}
