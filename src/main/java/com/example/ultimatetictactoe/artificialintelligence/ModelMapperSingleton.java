package com.example.ultimatetictactoe.artificialintelligence;

import org.modelmapper.ModelMapper;

public class ModelMapperSingleton {
    private static final ModelMapper INSTANCE;
    static {
        INSTANCE = new ModelMapper();
//        INSTANCE.getConfiguration()
//                .setMatchingStrategy(MatchingStrategies.STRICT)
//                .setFieldMatchingEnabled(true)
//                .setMethodAccessLevel(Configuration.AccessLevel.PRIVATE);
//        TypeMap<Player, PlayerState> typeMap = INSTANCE.createTypeMap(Player.class, PlayerState.class);
//        typeMap.setProvider(request -> {
//            Player player = (Player)request.getSource();
//            PlayerState playerState = new PlayerState();
//            playerState.hasWonGame(player.hasWonGame());
//            return playerState;
//        });
//        typeMap.addMapping(player -> player.hasWonGame(), (playerState, v) -> playerState.hasWonGame((boolean) v));

//                .addMapping(Player::hasWonGame, (destination, value) -> destination.hasWonGame((boolean)value));
    }

    private ModelMapperSingleton() {}

    public static ModelMapper getInstance() {
        return INSTANCE;
    }
}
