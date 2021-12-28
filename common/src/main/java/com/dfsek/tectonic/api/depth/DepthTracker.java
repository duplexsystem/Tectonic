package com.dfsek.tectonic.api.depth;

import com.dfsek.tectonic.api.config.Configuration;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public final class DepthTracker {
    private final List<Level> levels;
    private final Configuration configuration;

    @ApiStatus.Internal
    public DepthTracker(List<Level> levels, Configuration configuration) {
        this.levels = levels;
        this.configuration = configuration;
    }

    public DepthTracker with(Level level) {
        DepthTracker that = new DepthTracker(new ArrayList<>(levels), configuration);
        that.levels.add(level);
        return that;
    }

    public DepthTracker index(int index) {
        return with(new IndexLevel(index));
    }

    public DepthTracker entry(String entry) {
        return with(new EntryLevel(entry));
    }

    public String pathDescriptor() {
        StringBuilder builder = new StringBuilder();

        for(int depth = 0; depth < levels.size(); depth++) {
            Level level = levels.get(depth);
            if(depth > 0) {
                builder.append(level.joinDescriptor());
            }
            builder.append(level.identify());
        }
        return builder.toString();
    }

    public String getConfigurationName() {
        if(configuration.getName() == null) {
            return "Anonymous Configuration";
        } else {
            return configuration.getName();
        }
    }
}
