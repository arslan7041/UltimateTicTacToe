package com.example.ultimatetictactoe;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static com.example.ultimatetictactoe.Constants.GLOW_SPREAD;
import static com.example.ultimatetictactoe.Constants.MINIGRID_BLACK_BORDER_WIDTH;

public class GameUtils {

    private static final DropShadow glowEffect = createGlowEffect();
    private static List<GridPane> winningMiniGrids = null;

    private static DropShadow createGlowEffect() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.YELLOW);
        glow.setSpread(GLOW_SPREAD); // Set the width of the glow
        return glow;
    }

    public static void toggleMiniGridHighlighting(GridPane miniGrid, boolean flag){
        DropShadow glow = flag ? glowEffect : null;
        for(Node node : miniGrid.getChildren()){
            node.setEffect(glow);
        }
    }

    public static Background getPlayer1MiniGridBackground(){
        return new Background(new BackgroundFill(Color.BLUE,
                CornerRadii.EMPTY,
                Insets.EMPTY));
    }

    public static Background getPlayer2MiniGridBackground(){
        return new Background(new BackgroundFill(Color.rgb(255, 127, 127),
                CornerRadii.EMPTY,
                Insets.EMPTY));
    }

    public static GaussianBlur getBlurEffect(){
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(5);
        return blur;
    }

    public static void revertStateOfMiniGrid(GridPane miniGrid){
        miniGrid.setDisable(false);
        miniGrid.setEffect(null);
        miniGrid.setBackground(null);
        miniGrid.setStyle(String.format("-fx-border-color: black; -fx-border-width: %d;", MINIGRID_BLACK_BORDER_WIDTH));
    }

    public static GridPane findParentGridPane(Node node) {
        Parent parent = node.getParent();
        while (parent != null && !(parent instanceof GridPane)) {
            parent = parent.getParent();
        }
        return (GridPane) parent;
    }

    public static GridPane getGridPaneGivenIndices(GridPane mainGrid, int row, int col) {
        return (GridPane) mainGrid.getChildren().get(col * 3 + row);
    }

    public static void setWinningMiniGrids(GridPane mainGrid, List<List<Integer>> winningCoordinates){
        winningMiniGrids = new ArrayList<>();
        for (List<Integer> coordinates : winningCoordinates) {
            int row = coordinates.get(0);
            int col = coordinates.get(1);
            GridPane miniGrid = getGridPaneGivenIndices(mainGrid, row, col);
            winningMiniGrids.add(miniGrid);
        }
    }

    public static List<GridPane> getWinningMiniGrids(){
        return winningMiniGrids;
    }
}
