package com.timw.autoassign.exception;

import org.sonar.wsclient.issue.Issue;

/**
 * Created by twebster on 20/01/14.
 */
public class IssueNotAssignedException extends Throwable {

    private Issue issue;

    public IssueNotAssignedException(final Issue issue) {
        this.issue = issue;
    }

    public String getKey() {
        return this.issue.key();
    }
}
