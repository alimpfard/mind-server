package ir.smmh.mind.impl;

import ir.smmh.mind.*;
import ir.smmh.storage.Storage;
import ir.smmh.storage.impl.StorageImpl;
import ir.smmh.util.Comprehension;
import ir.smmh.util.JSONUtil;
import ir.smmh.util.Mutable;
import ir.smmh.util.impl.MutableHashSet;
import ir.smmh.util.impl.MutableImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Map.entry;

public class MutableIdeaImpl implements Idea.Mutable, Mutable.Injected {

    private static final Comprehension.Map<Property, String, Property> c = p -> entry(p.getName(), p);
    private final Mind mind;
    private final String name;
    private final Set<String> intensions;
    private final Map<String, Property> properties;
    private final Map<String, Property> staticProperties;
    private final ir.smmh.util.Mutable injectedMutable = new MutableImpl();
    private final Storage storage;
    private java.util.Set<Idea> intensionsCache;

    public MutableIdeaImpl(@NotNull Mind mind, @NotNull String name, @NotNull Set<String> intensions, @NotNull Iterable<Property> properties, @NotNull Iterable<Property> staticProperties) {
        this.mind = mind;
        this.name = name;
        this.intensions = intensions;
        this.properties = c.comprehend(properties);
        this.staticProperties = c.comprehend(staticProperties);
        this.storage = new StorageImpl(mind.getName());
        setup();
    }

    public MutableIdeaImpl(Mind mind, JSONObject object) {
        this.mind = mind;
        this.name = object.getString("name");
        this.intensions = JSONUtil.arrayOfStrings(object, "intensions", new MutableHashSet<>());
        this.properties = c.comprehend(JSONUtil.arrayOfObjects(object, "properties", new HashSet<>(), o -> new PropertyImpl(this, o)));
        this.staticProperties = c.comprehend(JSONUtil.arrayOfObjects(object, "static-properties", new HashSet<>(), o -> new PropertyImpl(this, o)));
        this.storage = new StorageImpl(mind.getName());
        setup();
    }

    private void setup() {
        intensions.addOnCleanListener(() -> intensionsCache = ((Comprehension.Set<String, Idea>) mind::findIdeaByName).comprehend(intensions));
    }

    @Override
    public Mind getMind() {
        return mind;
    }

    @Override
    public boolean hasDirectly(@NotNull String propertyName) {
        return properties.containsKey(propertyName);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @Nullable java.util.Set<Idea> getDirectIntensions() {
        intensions.clean();
        return intensionsCache;
    }

    @Override
    public java.util.Set<Property> getDirectProperties() {
        return new HashSet<>(properties.values());
    }

    @Override
    public @NotNull Instance instantiate() {
        return new InstanceImpl(this);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void become(String ideaName) {
        if (!intensions.contains(ideaName)) {
            intensions.add(ideaName);
            taint();
        }
    }

    @Override
    public Property possess(String name, String type, Supplier<Value> defaultValue) {
        if (!properties.containsKey(name)) {
            Property property = new PropertyImpl(this, name, type, defaultValue);
            properties.put(name, property);
            taint();
        }
        return properties.get(name);
    }

    @Override
    public Property reify(String name, String type, Value value) {
        if (!staticProperties.containsKey(name)) {
            Property property = new PropertyImpl(this, name, type, () -> value);
            staticProperties.put(name, property);
            taint();
        }
        return staticProperties.get(name);
    }

    @Override
    public @NotNull ir.smmh.util.Mutable getInjectedMutable() {
        return injectedMutable;
    }

    @Override
    public @NotNull Storage getStorage() {
        return storage;
    }

    @Override
    public void onClean() {
        Mutable.super.onClean();
    }

    @Override
    public @NotNull String serialize() {
        return encode();
        // TODO JSON serialize idea
    }
}
