NoLogs plugin 
=========================

Removes logs **completely** from project before compillation, then brings them back.<br/>

Plugin is designed for Android apps that are built with [Gradle](http://www.gradle.org/) and 
[Android New Build System](http://tools.android.com/tech-docs/new-build-system/user-guide).

Why do I need this?
------------------------------
Release builds of Android applications should not contain any debug logs.

One of the approaches of removing Android logs in production is the following

    if (BuildConfig.DEBUG) {
        Log.d("TAG", "Some log");
    } 

This works pretty well but you must always add an extra _if_ statement.

Another approach if to remove logs with the following Proguard rule:

    -assumenosideeffects class android.util.Log {
        public static boolean isLoggable(java.lang.String, int);
        public static int v(...);
        public static int i(...);
        public static int w(...);
        public static int d(...);
        public static int e(...);
    }
    
This removes simple logs completely, but converts more complex logs (assuming _width_ and _height_ are fields of a class)

    Log.d("TAG", "width: " + width + "; height: " + height);
    
to something like 

    new StringBuilder("width: ").append(this.b).append("; height: ").append(this.c);
    
So, Log methods are removed, but the strings are left in the code.

Setup
-----------------
### Apply plugin: 


    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            // apply android gradle plugin here
            
            // Version may change, see the latest version on jcenter
            classpath 'com.roottony.gradle:nologs:0.9'
        }
    }
    apply plugin: 'com.roottony.nologs'
    
Note that this plugin must be applied **after** 

    apply plugin 'com.android.application' 
    
or 

    apply plugin 'com.android.library'
    
### Set what to remove
To remove calls of all android.util.Log methods, add this to build.gradle 

    nologs {
        logClass = 'Log'
    }
    
If you have your own logging class, add it instead

    nologs {
        logClass = 'MyLoggingClass'
    }

Select whether logs should be removed for particular variant with the following closure

    nologs {
        shouldRemoveLogs = { variant ->
            return variant.buildType.name == 'release'
        }
    }

### Other options
    
Disable plugin completely with 

    nologs {
        enabled = false
    }
    
Disable uncommenting logs (useful to understand what's happeing under the hood)

    nologs {
        disableUncomment = true
    }
    
How does it work?
------------------------------
No magic, just a regexp.

Before compillation occurs, all java source files are processes with the regexp

    '^\ *' + logClass + '\.[\s\S]+?(?=\)\ *;\ *$)\)\ *\;'
    
After compilation completes, java source files are processed with another regexp

    '\ *' + logClass + '\.[\s\S]+?(?=\)\ *;\ *)\)\ *\;'
    
where _logClass_ is the name of the class you specified.

See the details in the source code.

Is it safe?
------------------------------
Well, almost :)

Dangerous code constructions are:

    if (someCondition)
        MyLogClass.d("Some log");
        
    doSomethingElse();

as _doSomethingElse()_ call will be **removed**. Note that 

    if (someCondition) MyLogClass.d("Some log");
        
    doSomethingElse();

is OK as _if (someCondition) MyLogClass.d("Some log");_ line will not be touched at all.

In general, a log will **not** be removed if it is not the first statement in the line:

    doSomethingElse(); MyLogClass.d("Some log");

    


