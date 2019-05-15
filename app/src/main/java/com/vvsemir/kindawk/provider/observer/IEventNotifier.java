package com.vvsemir.kindawk.provider.observer;

public interface IEventNotifier {
    void registerObserver(final IEventObserver observer);
    void unregisterObserver(final IEventObserver observer);
    void notifyObservers(@IEvent final Integer event);
}
