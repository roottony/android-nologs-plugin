package com.roottony.gradle

import org.gradle.api.Project

/**
 *
 * @author Anton Rutkevich <roottony@gmail.com>
 */
class ConfigValidator {

    public static boolean isPluginDisabled(NoLogsExtension noLogs) {
        return !noLogs.enabled;
    }

    public static boolean isConfigValid(Project project, NoLogsExtension noLogs) {
        if (!isLogClassValid(noLogs.logClass)) {

            project.logger.lifecycle( """
        ${NoLogsPlugin.NO_LOGS_EXTENSION}: No log class set. Set one with

        ${NoLogsPlugin.NO_LOGS_EXTENSION} {
            logClass = 'LogClassName'
        }
        """)

            return false
        }

        return true
    }


    private static boolean isLogClassValid(String logClass) {
        return logClass != null && !logClass.isEmpty()
    }
}
