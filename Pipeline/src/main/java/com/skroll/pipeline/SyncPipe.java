package com.skroll.pipeline;

import com.skroll.document.Document;

import java.util.List;

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
