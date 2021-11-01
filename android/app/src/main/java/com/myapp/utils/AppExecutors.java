package com.myapp.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    public static final Executor IO = Executors.newCachedThreadPool();
}
