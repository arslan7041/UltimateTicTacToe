package com.example.ultimatetictactoe.artificialintelligence;

import javafx.event.Event;
import javafx.event.EventType;

public class MoveEvent extends Event {
    public static final EventType<MoveEvent> MOVE_COMPLETED = new EventType<>(Event.ANY, "MOVE_COMPLETED");

    public MoveEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
