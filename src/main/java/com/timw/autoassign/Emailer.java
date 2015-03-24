package com.timw.autoassign;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;


/**
 * Created by twebster on 18/01/14.
 */
public class Emailer {

        private void send(final String emailAddress, final String issueKey, final String sonarUrl) throws EmailException {
            final String issueUrl = sonarUrl + "/issue/show/" + issueKey;
            final Email email = new SimpleEmail();
            email.setHostName("mail.server.com");
            email.setSmtpPort(25);
            email.setFrom("nobody@host.com");
            email.setSubject("[SONARQUBE] new issue.");
            email.setMsg("You have been assigned an issue.  View it here: " + issueUrl);
            email.addTo(emailAddress);
            email.send();

            System.out.println("Notification sent to: " + emailAddress);
    }

}
