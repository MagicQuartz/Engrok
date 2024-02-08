package io.github.magicquartz.engrok.initialization;

import io.github.magicquartz.engrok.config.EngrokConfig;

public interface LoadWorldInvoker{
    public void initialization(int port, EngrokConfig.regionSelectEnum region);
}
