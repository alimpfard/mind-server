package ir.smmh.util.impl;

import ir.smmh.util.MutableSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class MutableHashSet<T> extends HashSet<T> implements MutableSet<T>, ir.smmh.util.Mutable.Injected {
    private final ir.smmh.util.Mutable injectedMutable = new MutableImpl(this);

    public MutableHashSet() {
        super();
    }

    public MutableHashSet(Collection<? extends T> c) {
        super(c);
    }

    @Override
    public @NotNull ir.smmh.util.Mutable getInjectedMutable() {
        return injectedMutable;
    }

    @Override
    public boolean add(T t) {
        preMutate();
        if (super.add(t)) {
            postMutate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        preMutate();
        if (super.remove(o)) {
            postMutate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        if (!isEmpty()) {
            preMutate();
            super.clear();
            postMutate();
        }
    }
}
