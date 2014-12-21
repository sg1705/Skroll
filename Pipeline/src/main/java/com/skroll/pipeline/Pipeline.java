package com.skroll.pipeline;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by sagupta on 12/14/14.
 */
public class Pipeline<I,O> {


    private List<Pipe<I,O>> pipes = new ArrayList<Pipe<I,O>>();


    public static class Builder<I,O> {
        private List<Pipes> pipeNames = new ArrayList<Pipes>();
        private List<Pipe> pipes = new ArrayList<Pipe>();
        private HashMap<Pipes,List<Object>> configMap = new HashMap<Pipes, List<Object>>();

        public Builder add(Pipes pipe) {
            pipeNames.add(pipe);

            return this;
        }

        public Builder add(Pipes pipe, List<Object> config) {
            pipeNames.add(pipe);
            configMap.put(pipe, config);
            return this;
        }


        public Pipeline<I,O> build() {
            // stitch new pipes
            Pipes targetPipeName = Pipes.SINK_PIPE;
            Pipe<I,O> targetPipe = null;
            try {
                targetPipe = (Pipe<I,O>)(Class.forName(targetPipeName.getClassName()).newInstance());
                pipes.add(targetPipe);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            for(int ii = pipeNames.size() - 1; ii >= 0; ii--) {
                try {
                    Pipes newPipeName = pipeNames.get(ii);
                    Pipe<I,O> pipe = (Pipe<I,O>)(Class.forName(newPipeName.getClassName()).newInstance());
                    List<Object> config = configMap.get(newPipeName);
                    if (config != null) {
                        pipe.setConfig(config);
                    }
                    pipe.setTarget(targetPipe);
                    pipes.add(pipe);
                    targetPipe = pipe;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
            pipes = Lists.reverse(pipes);
            return new Pipeline(this);
        }


    }

    private Pipeline(Builder builder) {
        this.pipes = builder.pipes;
    }

    public O process(I input) {
        return pipes.get(0).process(input);
    }

    public int totalPipes() {
        return pipes.size();
    }
}
