package com.roottony.gradle

/**
 *
 * @author Anton Rutkevich <roottony@gmail.com>
 */
class NoLogsExtension {

    /**
     * Whether this plugin is enabled
     */
    boolean enabled = true

    /**
     * Name of the logging class.
     * All calls of all methods of this class will be removed.
     */
    String logClass

    /**
     * Disables uncommenting logs.
     * Useful to see what happened under the hood.
     */
    boolean disableUncomment = false

    /**
     * Closure that will be called to determine if logs should be removed for given variant.
     *
     * @param variantData         variant data.
     *                              See http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Manipulating-tasks
     */
    Closure shouldRemoveLogs

}
