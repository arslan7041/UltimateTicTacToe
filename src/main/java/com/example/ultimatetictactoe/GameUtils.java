package com.example.ultimatetictactoe;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.example.ultimatetictactoe.Constants.GLOW_SPREAD;
import static com.example.ultimatetictactoe.Constants.MINIGRID_BLACK_BORDER_WIDTH;

public class GameUtils {

    private static final DropShadow glowEffect = createGlowEffect();
    private static final Background player1MiniGridBackground = createPlayer1MiniGridBackground();
    private static final Background player2MiniGridBackground = createPlayer2MiniGridBackground();
    private static final Background transparentMiniGridBackground = createTransparentMiniGridBackground();

    private static List<GridPane> winningMiniGrids = null;

    private static DropShadow createGlowEffect() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.YELLOW);
        glow.setSpread(GLOW_SPREAD); // Set the width of the glow
        return glow;
    }

    private static Background createPlayer1MiniGridBackground(){
        return new Background(new BackgroundFill(Color.BLUE,
                CornerRadii.EMPTY,
                Insets.EMPTY));
    }

    private static Background createPlayer2MiniGridBackground(){
        return new Background(new BackgroundFill(Color.rgb(255, 127, 127),
                CornerRadii.EMPTY,
                Insets.EMPTY));
    }

    private static Background createTransparentMiniGridBackground(){
        return new Background(new BackgroundFill(Color.TRANSPARENT,
                CornerRadii.EMPTY,
                Insets.EMPTY));
    }

    public static Background getPlayer1MiniGridBackground(){
        return player1MiniGridBackground;
    }

    public static Background getPlayer2MiniGridBackground(){
        return player2MiniGridBackground;
    }

    public static Background getTransparentMiniGridBackground(){
        return transparentMiniGridBackground;
    }

    public static GaussianBlur getBlurEffect(){
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(5);
        return blur;
    }

    public static FadeTransition getFadeTransition(Label label){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.75), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        return fadeTransition;
    }

    public static void toggleMiniGridHighlighting(GridPane miniGrid, boolean flag){
        DropShadow glow = flag ? glowEffect : null;
        miniGrid.getChildren().forEach(child -> child.setEffect(glow));
    }

    public static void revertStateOfMiniGrid(GridPane miniGrid){
        miniGrid.setDisable(false);
        miniGrid.setEffect(null);
        miniGrid.setBackground(null);
        miniGrid.setStyle(String.format("-fx-border-color: black; -fx-border-width: %d;", MINIGRID_BLACK_BORDER_WIDTH));
    }

    public static void clearGridLines(GraphicsContext graphicsContext, GridPane grid){
        graphicsContext.clearRect(grid.getLayoutX(), grid.getLayoutY(), grid.getWidth(), grid.getHeight());
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

    public static void setWinningMiniGrids(GridPane mainGrid, List<WinningTriple> winningCoordinates){
        winningMiniGrids = new ArrayList<>();
        for (WinningTriple winningTriple : winningCoordinates) {
            for(WinningTriple.Coordinate coordinate: winningTriple.getCoordinates()){
                GridPane miniGrid = getGridPaneGivenIndices(mainGrid, coordinate.getX(), coordinate.getY());
                winningMiniGrids.add(miniGrid);
            }
        }
    }

    public static List<GridPane> getWinningMiniGrids(){
        return winningMiniGrids;
    }


    private static double computeXCoordinate(GridPane gridPane, int col) {
        double cellWidth = gridPane.getWidth() / 3;
        return gridPane.getLayoutX() + cellWidth * (col + 0.5);
    }

    private static double computeYCoordinate(GridPane gridPane, int row) {
        double cellHeight = gridPane.getHeight() / 3;
        return gridPane.getLayoutY() + cellHeight * (row + 0.5);
    }

    public static Canvas createOverlayCanvas(GridPane baseGridPane) {
        Canvas overlayCanvas = new Canvas();

        // Set canvas background to be transparent
        overlayCanvas.setStyle("-fx-background-color: transparent;");
        overlayCanvas.setMouseTransparent(true);

//        // Use Platform.runLater to ensure accurate dimensions and position
        Platform.runLater(() -> {
            overlayCanvas.setWidth(baseGridPane.getBoundsInLocal().getWidth());
            overlayCanvas.setHeight(baseGridPane.getBoundsInLocal().getHeight());
            overlayCanvas.setLayoutX(baseGridPane.getLayoutX());
            overlayCanvas.setLayoutY(baseGridPane.getLayoutY());
        });

        return overlayCanvas;
    }

}
