java -cp  ".;lib/*" -Dlog4j.configuration=log4j.xml ^
                    -Dsonar.url=%SONAR_URL% ^
                    -Dsonar.admin.username=%SONAR_ADMIN_USERNAME% ^
                    -Dsonar.admin.password=%SONAR_ADMIN_PASSWORD% ^
                    -Dinclude.projects=%INCLUDE_PROJECTS% ^
                    -Dassignee.override=%ASSIGNEE_OVERRIDE% ^
                    -Dassignee.default=%ASSIGNEE_DEFAULT% ^
                    com.timw.IssueAssigner
