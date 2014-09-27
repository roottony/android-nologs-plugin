package com.roottony.gradle

import org.gradle.api.file.FileTree
import org.gradle.api.AntBuilder

/**
 *
 * @author Anton Rutkevich <roottony@gmail.com>
 */
class LogsProcessor {

    /**
     * Insert special seed to uncomment only the comments we just made
     */
    private static final OPEN_COMMENT_PATTERN = '\\/\\* <<gradle '
    private static final CLOSE_COMMENT_PATTERN = ' gradle>> \\*\\/'

    private AntBuilder ant;

    LogsProcessor(AntBuilder ant) {
        this.ant = ant;
    }

    /**
     * Matches one-line and multiline logs.
     * Does not match line- or block-commented logs.
     *
     * Examine
     *
     * <p>     ^\ *Log\.[\s\S]+?(?=\)\ *;\ *$)\)\ *\; </p>
     *
     * regex on multiline expression
     *
     *  <p>
     *      <br/>Log.d("LogTag", "Some multiline log"
     *      <br/>    + " second line of the log" ) ;
     *      <br/>
     *      <br/> someOtherMethod("asdasd");
     *      <br/><br/>
     *  </p>
     *
     *
     * on https://www.debuggex.com
     * to understand how it works.
     */
    private static String getLogPattern(String logClass) {
        return '^\\ *' + logClass + '\\.[\\s\\S]+?(?=\\)\\ *;\\ *$)\\)\\ *\\;'
    }

    /**
     * We do not need start-line and end-line requirements
     * while removing comments we just made.
     */
    private static String getReverseLogPattern(String logClass) {
        return '\\ *' + logClass + '\\.[\\s\\S]+?(?=\\)\\ *;\\ *)\\)\\ *\\;'
    }

    void commentDebugLogs(FileTree files, String logClass) {
        String logPattern = getLogPattern(logClass)
        performReplacement(files,
                "($logPattern)", "$OPEN_COMMENT_PATTERN \\1 $CLOSE_COMMENT_PATTERN")
    }

    void uncommentDebugLogs(FileTree files, String logClass) {
        String reverseLogPattern = getReverseLogPattern(logClass)
        performReplacement(files,
                "($OPEN_COMMENT_PATTERN) ($reverseLogPattern) ($CLOSE_COMMENT_PATTERN)", "\\2")
    }

    void performReplacement(FileTree files, String match, String replace) {
        files.each { file ->
            ant.replaceregexp(
                    file: file,
                    match: match,
                    replace: replace,
                    flags: 'mg'  // m - multiline, g - global (replace all occurrences)
            )
        }
    }
}
