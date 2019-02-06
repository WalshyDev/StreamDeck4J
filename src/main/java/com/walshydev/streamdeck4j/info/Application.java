package com.walshydev.streamdeck4j.info;

import lombok.Getter;

import javax.annotation.Nonnull;

@Getter(onMethod_ = {@Nonnull})
public class Application {

    private String language;
    private String platform;
    private String version;

    public Application(@Nonnull String language, @Nonnull String platform, @Nonnull String version) {
        this.language = language;
        this.platform = platform;
        this.version = version;
    }
}
