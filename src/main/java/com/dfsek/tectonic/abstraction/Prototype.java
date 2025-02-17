package com.dfsek.tectonic.abstraction;

import com.dfsek.tectonic.abstraction.exception.AbstractionException;
import com.dfsek.tectonic.abstraction.exception.CircularInheritanceException;
import com.dfsek.tectonic.abstraction.exception.ParentNotFoundException;
import com.dfsek.tectonic.annotations.Default;
import com.dfsek.tectonic.annotations.Value;
import com.dfsek.tectonic.config.ConfigTemplate;
import com.dfsek.tectonic.config.Configuration;
import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a partially loaded config. A Prototype config has its ID, Abstract, and Extension keys (required by
 * abstraction) loaded, and nothing more.
 */
@SuppressWarnings("unused")
public class Prototype implements ConfigTemplate {
    private final List<Prototype> children = new ArrayList<>();
    private final Set<Integer> UIDs = new HashSet<>();
    private final Configuration config;
    private Prototype parent;
    private boolean isRoot = false;
    @Value("id")
    private String id;
    @SuppressWarnings("FieldMayBeFinal")
    @Value("extends")
    @Default
    private String extend = null;
    @SuppressWarnings("FieldMayBeFinal")
    @Value("abstract")
    @Default
    private boolean isAbstract = false;

    /**
     * Instantiate a Prototype with a configuration. This will load the prototype using a freshly constructed
     * {@link ConfigLoader}. There are no custom data types in this class, so using a new ConfigLoader prevents
     * pollution.
     *
     * @param config Config to load Prototype from
     * @throws ConfigException If the config contains invalid data.
     */
    public Prototype(Configuration config) throws ConfigException {
        this.config = config;
        new ConfigLoader().load(this, config);
    }

    /**
     * Returns the {@link Configuration} assigned to this Prototype.
     *
     * @return Configuration.
     */
    @NotNull
    public Configuration getConfig() {
        return config;
    }

    /**
     * Returns whether this Prototype is abstract.
     *
     * @return True if this Prototype is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Build this Prototype's inheritance from an AbstractPool
     *
     * @param pool     AbstractPool to search for parent configs.
     * @param chainUID Unique identifier for this inheritance tree. Used to check for circular inheritance.
     * @throws AbstractionException if invalid abstraction data is found.
     */
    protected void build(AbstractPool pool, int chainUID) throws AbstractionException {
        if(UIDs.contains(chainUID))
            throw new CircularInheritanceException("Circular inheritance detected in config: \"" + getID() + "\", extending \"" + extend + "\", UID: " + chainUID);
        UIDs.add(chainUID);
        if(extend != null) {
            Prototype parent = pool.get(extend);
            if(parent == null)
                throw new ParentNotFoundException("No such config \"" + extend + "\". Specified as parent of \"" + id + "\"");
            this.parent = parent;
            this.parent.build(pool, chainUID); // Build the parent, to recursively build the entire tree.
        } else isRoot = true;
    }

    /**
     * Gets the ID of this Prototype.
     *
     * @return ID of Prototype.
     */
    @NotNull
    public String getID() {
        return id;
    }

    /**
     * Get this Prototype's parent, if it exists.
     *
     * @return This Prototype's parent. {@code null} if the parent does not exist, or has not been loaded.
     * @see #build
     */
    @Nullable
    public Prototype getParent() {
        return parent;
    }

    /**
     * Returns whether this Prototype is a root (Has no parents)
     *
     * @return Whether this is a root in the inheritance tree.
     */
    public boolean isRoot() {
        return isRoot;
    }
}
