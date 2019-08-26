package com.didi.everything.core.interceptor;

import com.didi.everything.core.model.Thing;

@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
