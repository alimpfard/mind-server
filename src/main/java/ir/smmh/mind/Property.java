package ir.smmh.mind;

import ir.smmh.util.Named;

import java.util.function.Supplier;

public interface Property extends Named {

    Idea getOrigin();

    String getType();

    Supplier<Value> getDefaultValue();

    default String encode() {
        return "has " + getName() + " as " + getType();
    }
}
