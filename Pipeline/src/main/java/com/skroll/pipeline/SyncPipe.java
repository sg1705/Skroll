package com.skroll.pipeline;

import java.util.List;
import java.util.Set;

/**
 * Created by sagupta on 12/14/14.
 *
 */
public abstract class SyncPipe<I,O> implements Pipe<I,O> {

    protected Pipe<I,O> target;
    protected List<Object> config;

    @Override
    public void setTarget(Pipe pipe) {
        this.target = pipe;
    }

    @Override
    public abstract O process(I input);

    @Override
    public void setConfig(List<Object> config) {
        this.config = config;
    }


}
