package io.github.magicquartz.engrok.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "engrok")
public class EngrokConfig implements ConfigData {
    public boolean enabled = true;
    public String ngrokAuthToken = "Insert your Ngrok auth token here";

    public enum regionSelectEnum {
        US, EU, AP, AU, SA, JP, IN, AUTO
    }

    public regionSelectEnum regionSelect = regionSelectEnum.AUTO;

    public String gitHubAuthToken = "";
    public String gistId = "";
}
