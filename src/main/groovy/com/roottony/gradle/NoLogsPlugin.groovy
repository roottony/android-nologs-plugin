package com.roottony.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 *
 * @author Anton Rutkevich <roottony@gmail.com>
 */
public class NoLogsPlugin implements Plugin<Project> {

    static final String NO_LOGS_EXTENSION = "nologs"

    @Override
    void apply(Project project) {

        NoLogsExtension noLogs = project.extensions.create(NO_LOGS_EXTENSION, NoLogsExtension);

        project.afterEvaluate {
            enablePluginIfNecessary(project, noLogs)
        }
    }

    private static enablePluginIfNecessary(Project project, NoLogsExtension noLogs) {

        if (ConfigValidator.isPluginDisabled(noLogs)
                || !ConfigValidator.isConfigValid(project, noLogs)) {
            return;
        }

        if (project.plugins.hasPlugin("com.android.application")) {
            project.android.applicationVariants.all { variant ->
                if (shouldExecuteForVariant(noLogs, variant)) {
                    commentLogsBeforeCompile(project, variant, noLogs)
                }
            }
        } else if (project.plugins.hasPlugin('com.android.library')) {
            project.android.libraryVariants.all { variant ->
                if (shouldExecuteForVariant(noLogs, variant)) {
                    commentLogsBeforeCompile(project, variant, noLogs)
                }
            }
        }
    }

    private static boolean shouldExecuteForVariant(NoLogsExtension noLogs, variant) {
        noLogs.shouldRemoveLogs == null || noLogs.shouldRemoveLogs(variant)
    }

    private static commentLogsBeforeCompile(Project project, def variant, NoLogsExtension noLogs) {
        JavaCompile javaCompile = variant.javaCompile

        LogsProcessor logsProcessor = new LogsProcessor(project.ant);
        String logClass = noLogs.logClass

        javaCompile.doFirst {
            log(project, "Commenting all calls of '${logClass}' methods...")
            logsProcessor.commentDebugLogs(javaCompile.source, logClass)
        }

        if (!noLogs.disableUncomment) {
            javaCompile.doLast {
                log(project, "Reverting commented calls of '${logClass}' methods...")
                logsProcessor.uncommentDebugLogs(javaCompile.source, logClass)
            }
        }
    }

    private static log(Project project, String message) {
        project.logger.lifecycle(message);
    }

}
