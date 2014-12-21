package com.skroll.pipeline;

import java.util.List;
import java.util.Set;

/**
 * Created by sagupta on 12/14/14.
 */
public interface Pipe<I,O> {

    O process(I input);
    public void setTarget(Pipe pipe);

    public void setConfig(List<Object> config);
}
