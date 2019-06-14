Catdea Plugin
===========

Catdea Plugin intended to match Android Logcat log entries with the source code that emit them, 
thereby providing navigation and interactivity, in order to simplify debugging or reverse engineering.

Plugin uses pre-compile-time calculations to identify log emitters in the source code, 
thus, no RegExp patterns required.

### Installation
Install from [JetBrains Plugins Repository](https://plugins.jetbrains.com/plugin/12241-catdea) or 
get from [latest release](https://github.com/Cybr0sis/Catdea/releases/latest) and choose it in IDE 
(`File` &rarr; `Settings` &rarr; `Plugins` &rarr; `Install Plugin from Disk`).

Features
------------
* ##### Logcat Monitor Tool Window
In this tool window, you can view and analyze Logcat output with syntax highlighting and navigation to the source code.

![Logcat feature screencast](images/catdea_logcat.gif)

* ##### Navigation 
Provides navigation from _log entry_ to the _source code_ that emit it, and vice versa.

Click `Navigate` &rarr; `Declaration` menu on log entry to go to the emitter.
Click `Navigate` &rarr; `Related symbol...` menu on the call in source code, 
or click gutter icon ![Gutter navigation icon](src/main/resources/icons/gutter.svg) to go to the log entry.

![Navigation feature screencast](images/navigation.gif)

* ##### Folding
Collapse and expand log entry's package name or tag.
Use `Code` &rarr; `Folding` menu or shortcuts.

![Folding feature screencast](images/folding.gif)

* ##### Highlighting
Highlight log entries with the same tag and log entries, that do not match to the code 

![Highlighting feature screencast](images/highlighting.gif)

* ##### Log files support
Save Logcat output to file with extensions `.log`, `.logcat` or `.logdump` for later analysis in offline mode.
 
* ##### Flexible log format
Only _log level_, _tag_ and _message_ are required to provide navigation.

* ##### Syntax highlighting and colors settings
Tune your color preferences (`File` &rarr; `Settings` &rarr; `Editor` &rarr; `Color Scheme` &rarr; `Catdea`).

* ##### Log wrappers support
Custom classes that _wraps_ Android Log functionality will be recognized as log emitters automatically.

Consider the following class `SecLog`, which is a _wrapper_ on the `android.util.Log`:
```java
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public final class SecLog {
    public static final String TAG = "["+ SecLog.class.getSimpleName() + "]";

    public static void i(@NonNull String tag, @Nullable String msg) {
        if (msg != null) {
            Log.i(TAG, tag + ": " + msg);
        }
    }
}
```
Then call of `SecLog.i` method like this:
```java
SecLog.i(TAG, "onSignup() called with: isSuccess = [" + isSuccess + "]");
```
will be recognized as log emitter. 

Gutter icon ![Gutter navigation icon](src/main/resources/icons/gutter.svg) will appear and provide navigation to the log entry.
If emitter is identified, but there are no log entries matched it - icon ![Gutter not found icon](src/main/resources/icons/gutter_none.svg) will be shown.

* ##### Format strings support
Catdea is able to identify log emitter that uses `String.format` and others.
```java
Log.d(TAG, String.format("generatePassword(%d) = \"%s\"", length, password));
```

* ##### (Un)comment log entry
Use `Code` &rarr; `Comment with Line Comment` menu or shortcut on the log entry.

* ##### Logs View in Project-View
Logs view groups all Logcat dump files into one place for better management. 

![Project view screenshot](images/project_view.png)

Changelog
------------
* 1.1 Android Logcat Monitor Tool Window
* 1.0 Initial release