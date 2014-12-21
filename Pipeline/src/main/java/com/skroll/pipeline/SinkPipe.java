package com.skroll.pipeline;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;

import java.util.Set;

/**
 * Created by sagupta on 12/14/14.
 */
public class SinkPipe<I,O> extends SyncPipe<I,O> {


    @Override
    public O process(I input) {
        return (O)input;
    }

}
