package ir.smmh.mind.impl;

import ir.smmh.mind.*;
import ir.smmh.nile.verbs.CanSerialize;
import ir.smmh.storage.Storage;
import ir.smmh.storage.impl.StorageImpl;
import ir.smmh.util.Comprehension;
import ir.smmh.util.JSONUtil;
import ir.smmh.util.Mutable;
import ir.smmh.util.MutableCollection;
import ir.smmh.util.impl.MutableCollectionImpl;
import ir.smmh.util.impl.MutableImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Map.entry;

@SuppressWarnings("ThrowsRuntimeException")
public class MutableIdeaImpl implements Idea.Mutable, Mutable.WithListeners.Injected, CanSerialize.JSON {

    private static final Comprehension.Map<PropertyImpl, String, PropertyImpl> c = p -> entry(p.getName(), p);
    private static final Comprehension.Map<StaticPropertyImpl, String, StaticPropertyImpl> sc = p -> entry(p.getName(), p);
    private final Mind mind;
    private final String name;
    private final MutableCollection<String> intensions;
    private final Map<String, PropertyImpl> properties;
    private final Map<String, StaticPropertyImpl> staticProperties;
    private final ir.smmh.util.Mutable.WithListeners injectedMutable = MutableImpl.blank();
    private final Storage storage;
    private java.util.Set<Idea> intensionsCache;

    public MutableIdeaImpl(@NotNull Mind mind, @NotNull String name, @NotNull MutableCollection<String> intensions, @NotNull Iterable<PropertyImpl> properties, @NotNull Iterable<StaticPropertyImpl> staticProperties) {
        super();
        this.mind = mind;
        this.name = name;
        this.intensions = intensions;
        this.properties = c.comprehend(properties);
        this.staticProperties = sc.comprehend(staticProperties);
        storage = StorageImpl.of(mind.getName());
        setup();
    }

    public MutableIdeaImpl(Mind mind, JSONObject object) throws JSONException {
        super();
        this.mind = mind;
        name = object.getString("name");
        intensions = JSONUtil.arrayOfStrings(object, "intensions", MutableCollectionImpl.of(new HashSet<>()));
        properties = c.comprehend(JSONUtil.arrayOfObjects(object, "properties", new HashSet<>(), o -> new PropertyImpl(this, o)));
        staticProperties = sc.comprehend(JSONUtil.arrayOfObjects(object, "static-properties", new HashSet<>(), o -> new StaticPropertyImpl(this, o)));
        storage = StorageImpl.of(mind.getName());
        setup();
    }

    @Override
    public final @NotNull JSONObject serializeJSON() throws JSONException {
        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("intensions", intensions);
            object.put("properties", properties.values()); // ((Comprehension.List<String, PropertyImpl>) properties::get).comprehend(properties.keySet()));
            object.put("static-properties", staticProperties.values()); // ((Comprehension.List<String, PropertyImpl>) staticProperties::get).comprehend(staticProperties.keySet()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private void setup() {
        setupStored();
        intensions.getOnCleanListeners().add(() -> intensionsCache = ((Comprehension.Set<String, Idea>) mind::findIdeaByName).comprehend(intensions));
    }

    @Override
    public final Mind getMind() {
        return mind;
    }

    @Override
    public final boolean hasDirectly(@NotNull String propertyName) {
        return properties.containsKey(propertyName);
    }

    @Override
    public final @NotNull String getName() {
        return name;
    }

    @Override
    public final @Nullable java.util.Set<Idea> getDirectIntensions() {
        intensions.clean();
        return intensionsCache;
    }

    @Override
    public final java.util.Set<Property> getDirectProperties() {
        return new HashSet<>(properties.values());
        // TODO optimize with caching
    }

    @Override
    public final @NotNull Instance instantiate() {
        return new InstanceImpl(this);
    }

    @NotNull
    @Override
    public final String toString() {
        return name;
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public final void become(String ideaName) {
        if (!intensions.contains(ideaName)) {
            preMutate();
            intensions.add(ideaName);
            postMutate();
        }
    }

    @Override
    public final Property possess(String propertyName, String type, Supplier<Value> defaultValue) {
        if (!properties.containsKey(propertyName)) {
            preMutate();
            PropertyImpl property = new PropertyImpl(this, propertyName, type, defaultValue);
            properties.put(propertyName, property);
            postMutate();
        }
        return properties.get(propertyName);
    }

    @Override
    public final StaticProperty reify(String propertyName, String type, Value value) {
        if (!staticProperties.containsKey(propertyName)) {
            preMutate();
            StaticPropertyImpl property = new StaticPropertyImpl(this, propertyName, type, value);
            staticProperties.put(propertyName, property);
            postMutate();
        }
        return staticProperties.get(propertyName);
    }

    @Override
    public final @NotNull ir.smmh.util.Mutable.WithListeners getInjectedMutable() {
        return injectedMutable;
    }

    @Override
    public final @NotNull Storage getStorage() {
        return storage;
    }
}
