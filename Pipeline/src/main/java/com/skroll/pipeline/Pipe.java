package com.skroll.pipeline;

import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public interface Pipe<I,O> {

    public abstract O process(I input);
    public void setTarget(Pipe pipe);

    public void setConfig(List<Object> config);
}
