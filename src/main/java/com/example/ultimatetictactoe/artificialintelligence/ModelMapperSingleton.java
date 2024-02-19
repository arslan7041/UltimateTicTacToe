package com.example.ultimatetictactoe.artificialintelligence;

import org.modelmapper.ModelMapper;

public class ModelMapperSingleton {
    private static final ModelMapper INSTANCE = new ModelMapper();

    private ModelMapperSingleton() {}

    public static ModelMapper getInstance() {
        return INSTANCE;
    }
}
