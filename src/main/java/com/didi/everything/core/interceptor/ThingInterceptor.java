package com.didi.everything.core.interceptor;

import com.didi.everything.core.model.Thing;

import java.io.File;

@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
