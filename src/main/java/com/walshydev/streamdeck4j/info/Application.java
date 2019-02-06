package com.walshydev.streamdeck4j.info;

import lombok.Getter;

import javax.annotation.Nonnull;

@Getter(onMethod = @__({@Nonnull}))
public class Application {

    @Nonnull
    private String language;
    @Nonnull
    private String platform;
    @Nonnull
    private String version;

    public Application(@Nonnull String language, @Nonnull String platform, @Nonnull String version) {
        this.language = language;
        this.platform = platform;
        this.version = version;
    }
}
