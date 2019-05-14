package com.cybrosis.catdea.tests;

import java.util.String;
import java.lang.StringBuilder;
import android.util.Log;
import android.annotation.SuppressLint;

class Logger {
    private static final String PRETAG = "PRETAG";

    public static void info(String tag, String msg) {
        if (msg != null) {
            Log.i(PRETAG, "[" + tag + "] " + msg);
        }
    }
}

class Base {
    public Base(String s) {}
    void f(String ... x) {}
    void f(String x, int y) {}
}


public class Test extends Base {
    private static final String TAG = Test.class.getSimpleName();

    //region Valid
    void foo(String x) {
        Log.i(TAG + "2", "foo = " + x);
    }

    void bar(String x) {
        Logger.info(TAG, "bar(" + x + ")");
    }

    void boo(String x) {
        Logger.info(TAG, String.format("boo(%s)", x));
    }

    private Object o = new Object() {
        @Override
        public String toString() {
            final String s = super.toString();
            Log.d(TAG, "o = " + s);
            return s;
        }
    };
    //endregion

    //region Invalid
    void baz() {
        f("baz", 0);
    }

    IndexTest(String s) {
        super("super");
    }

    IndexTest(String s) {
        this("this");
    }

    void foz() {
        throw new RuntimeException("exception");
    }

    void fof() {
        Base base = new Base("base");

        StringBuilder builder = new StringBuilder("lazy").append("fox");
        builder.append("jumps").append("over");
    }

    void bar() {
        f(new String[]{"A", "B", "C"});
    }

    void baar() {
        f("a", "b", "c");
    }

    String qux() {
        return "qux";
    }

    @SuppressLint({"NewApi"})
    void quux() {
    }

    void quuz(String s) {
        if ("quuz".equals(s)) return;
    }

    void waldo(String s) {
        String.format("waldo: %s %d", s, 1337)
    }
    //endregion
}

