package com.example.ultimatetictactoe;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import static com.example.ultimatetictactoe.Constants.*;

public class GameUtils {

    private static final int cellSize = CELL_BUTTON_SIZE;

    private static DropShadow glowEffect;
    private static GaussianBlur blurEffect;
    private static Background player1MiniGridBackground;
    private static Background player2MiniGridBackground;

    private static final List<GridPane> winningMiniGrids = new ArrayList<>();
    private static GraphicsContext graphicsContext = null;
    private static Timeline timeline = null;
    private static Background coloredBackground = null;

    private static DropShadow getGlowEffect() {
        if(glowEffect == null){
            glowEffect = new DropShadow();
            glowEffect.setColor(Color.YELLOW);
            glowEffect.setSpread(GLOW_SPREAD);
        }
        return glowEffect;
    }

    public static GaussianBlur getBlurEffect(){
        if(blurEffect == null){
            blurEffect = new GaussianBlur();
            blurEffect.setRadius(BLUR_RADIUS);
        }
        return blurEffect;
    }

    public static Background getPlayer1MiniGridBackground(){
        if(player1MiniGridBackground == null){
            player1MiniGridBackground = new Background(new BackgroundFill(Color.BLUE,
                    CornerRadii.EMPTY,
                    Insets.EMPTY));
        }
        return player1MiniGridBackground;
    }

    public static Background getPlayer2MiniGridBackground(){
        if(player2MiniGridBackground == null){
            player2MiniGridBackground = new Background(new BackgroundFill(Color.rgb(255, 127, 127),
                    CornerRadii.EMPTY,
                    Insets.EMPTY));
        }
        return player2MiniGridBackground;
    }

    public static FadeTransition getFadeTransition(Label label){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.75), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        return fadeTransition;
    }

    public static void toggleMiniGridHighlighting(GridPane miniGrid, boolean flag){
        DropShadow glow = flag ? getGlowEffect() : null;
        miniGrid.getChildren().forEach(child -> child.setEffect(glow));
    }

    public static void revertStateOfMiniGrid(GridPane miniGrid){
        miniGrid.setDisable(false);
        miniGrid.setEffect(null);
        miniGrid.setBackground(null);
        miniGrid.setStyle(String.format("-fx-border-color: black; -fx-border-width: %d;", MINIGRID_BLACK_BORDER_WIDTH));
    }

    public static void clearGridLines(GridPane grid){
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
        winningMiniGrids.clear();
        for (WinningTriple winningTriple : winningCoordinates) {
            for(WinningTriple.Coordinate coordinate: winningTriple.getCoordinates()){
                GridPane miniGrid = getGridPaneGivenIndices(mainGrid, coordinate.getX(), coordinate.getY());
                winningMiniGrids.add(miniGrid);
            }
        }
    }

    public static Canvas createOverlayCanvas(GridPane baseGridPane) {
        Canvas overlayCanvas = new Canvas();
        overlayCanvas.setStyle("-fx-background-color: transparent;");
        overlayCanvas.setMouseTransparent(true);

        Platform.runLater(() -> {
            overlayCanvas.setWidth(baseGridPane.getBoundsInLocal().getWidth());
            overlayCanvas.setHeight(baseGridPane.getBoundsInLocal().getHeight());
            overlayCanvas.setLayoutX(baseGridPane.getLayoutX());
            overlayCanvas.setLayoutY(baseGridPane.getLayoutY());
        });

        return overlayCanvas;
    }

    public static void setGraphicsContextObject(Canvas overlayCanvas){
        graphicsContext = overlayCanvas.getGraphicsContext2D();
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(WIN_LINE_WIDTH);
    }

    public static void drawWinningLine(GridPane miniGrid, List<WinningTriple.Coordinate> coordinates){
        int startRow = coordinates.get(0).getX();
        int endRow = coordinates.get(2).getX();
        int startCol = coordinates.get(0).getY();
        int endCol = coordinates.get(2).getY();

        if(startRow == endRow){
            drawHorizontalLine(miniGrid, startRow, endRow, startCol, endCol);
        }else if(startCol == endCol){
            drawVerticalLine(miniGrid, startRow, endRow, startCol, endCol);
        }else if(endRow > startRow){
            drawDownDiagonal(miniGrid, startRow, endRow, startCol, endCol);
        }else{
            drawUpDiagonal(miniGrid, startRow, endRow, startCol, endCol);
        }
    }

    private static void drawHorizontalLine(GridPane miniGrid, int startRow, int endRow, int startCol, int endCol) {
        graphicsContext.strokeLine(
                miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.2),
                miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.5),
                miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.8),
                miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.5) );
    }

    private static void drawVerticalLine(GridPane miniGrid, int startRow, int endRow, int startCol, int endCol) {
        graphicsContext.strokeLine(
                miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.5),
                miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.2),
                miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.5),
                miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.8) );
    }

    private static void drawDownDiagonal(GridPane miniGrid, int startRow, int endRow, int startCol, int endCol) {
        graphicsContext.strokeLine(
                miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.2),
                miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.2),
                miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.8),
                miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.8) );
    }

    private static void drawUpDiagonal(GridPane miniGrid, int startRow, int endRow, int startCol, int endCol) {
        graphicsContext.strokeLine(
                miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.2),
                miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.8),
                miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.8),
                miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.2) );
    }

    public static void flashBackGrounds(Background background){
        coloredBackground = background;
        List<KeyFrame> keyFrameList = new ArrayList<>();

        for(GridPane miniGrid : winningMiniGrids){
            keyFrameList.add(new KeyFrame(Duration.seconds(0.5), e -> miniGrid.setBackground(coloredBackground)));
            keyFrameList.add(new KeyFrame(Duration.seconds(1), e -> miniGrid.setBackground(null)));
        }

        timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void stopBackGroundFlashing(){
        if(timeline != null){
            timeline.stop();
            timeline = null;
            for(GridPane miniGrid : winningMiniGrids){
                miniGrid.setBackground(coloredBackground);
            }
        }
    }
}
