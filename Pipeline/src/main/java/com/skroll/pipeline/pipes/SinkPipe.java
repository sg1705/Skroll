package com.skroll.pipeline.pipes;

import com.skroll.pipeline.SyncPipe;

/**
 * Created by sagupta on 12/14/14.
 */
public class SinkPipe<I,O> extends SyncPipe<I,O> {


    @Override
    public O process(I input) {
        return (O)input;
    }

}
