package com.example.ultimatetictactoe;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.ultimatetictactoe.Constants.*;

public class UltimateTicTacToe extends Application {

    private UltimateTicTacToeBackEndGame game;
    private GridPane mainGrid;
    private Canvas overlayCanvas;
    private GraphicsContext graphicsContext;
    private Set<Node> clickableMiniGrids;
    private Label turnLabel;
    private Label resultLabel;
    private Button player1UndoButton;
    private Button player2UndoButton;
    private LastMove lastMove;
    private Timeline timeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new UltimateTicTacToeBackEndGame();
        initializeGrid();
        clickableMiniGrids = new HashSet<>(mainGrid.getChildren());

        overlayCanvas = GameUtils.createOverlayCanvas(mainGrid);
        graphicsContext = overlayCanvas.getGraphicsContext2D();
        StackPane stackPane = new StackPane(mainGrid, overlayCanvas);
        stackPane.setStyle("-fx-background-color:transparent;");
        stackPane.setPadding(new Insets(0, 50, 0, 0)); // padding to the right of grid

        initializeTurnLabel();
        initializeResultLabel();

        player1UndoButton = createUndoButton(game.getPlayer1());
        player2UndoButton = createUndoButton(game.getPlayer2());
        player1UndoButton.setDisable(true);
        player2UndoButton.setDisable(true);
        player1UndoButton.setOnAction(e -> handleUndoButtonClick());
        player2UndoButton.setOnAction(e -> handleUndoButtonClick());

        lastMove = null;

        VBox undoButtonsVerticalBox = new VBox();
        undoButtonsVerticalBox.getChildren().addAll(player1UndoButton, player2UndoButton);
        undoButtonsVerticalBox.setSpacing(10);
        undoButtonsVerticalBox.setPadding(new Insets(50, 0, 0, 0));

        Button startNewGameButton = new Button(START_NEW_GAME_BUTTON_TEXT);
        startNewGameButton.setStyle(String.format("-fx-border-color: black; -fx-border-width: %d; -fx-font-weight: bold; -fx-font-size: %fem;",
                START_NEW_GAME_BUTTON_BORDER_WIDTH, START_NEW_GAME_BUTTON_FONT_SIZE));
        startNewGameButton.setOnAction(e -> handleStartNewGameButtonClick());

        VBox verticalBox = new VBox();
        verticalBox.getChildren().addAll(resultLabel, startNewGameButton, undoButtonsVerticalBox);
        verticalBox.setPadding(new Insets(245, 0, 0, 0)); // padding above to push resultLabel and startNewGameButton down
        VBox.setMargin(startNewGameButton, new Insets(30, 0, 0, 0));

        HBox horizontalBox = new HBox(stackPane, verticalBox);

        VBox rootVerticalBox = new VBox(turnLabel, horizontalBox);
        rootVerticalBox.setPadding(new Insets(0, 0, 0, 50)); // pad left of root

        Scene scene = new Scene(rootVerticalBox, SCROLLPANE_WIDTH, SCROLLPANE_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Ultimate Tic Tac Toe");
        primaryStage.show();
    }

    private void handleUndoButtonClick() {
        game.player1Turn = !game.player1Turn;
        toggleTurnLabel();
        Button lastButton = lastMove.getButton();
        GridPane lastMiniGrid = lastMove.getMiniGrid();
        Set<Node> lastClickableMiniGrids = lastMove.getClickableMiniGrids();

        clearClickableMiniGridsHighlighting();
        clickableMiniGrids = lastClickableMiniGrids;
        if(clickableMiniGrids.size() < 9){
            for(Node node : clickableMiniGrids){
                GameUtils.toggleMiniGridHighlighting((GridPane) node, true);
            }
        }

        lastButton.setGraphic(null);
        game.undoMove(lastButton, lastMiniGrid);

        if(lastMiniGrid.isDisabled()){
            GameUtils.revertStateOfMiniGrid(lastMiniGrid);
            GameUtils.clearGridLines(graphicsContext, lastMiniGrid);
            game.undoMiniGridCompletionAndWin(lastMiniGrid);
        }

        if(mainGrid.isDisabled()){
            mainGrid.setDisable(false);
            resultLabel.setVisible(false);
            turnLabel.setVisible(true);

            if(game.isTie){
                game.isTie = false;
            } else if(game.getPlayer1().wonGame){
                game.getPlayer1().wonGame = false;
            } else if(game.getPlayer2().wonGame){
                game.getPlayer2().wonGame = false;
            }
        }

        if(timeline != null){
            Background background = game.player1Turn ? GameUtils.getPlayer1MiniGridBackground() : GameUtils.getPlayer2MiniGridBackground();
            List<GridPane> winningMiniGrids = GameUtils.getWinningMiniGrids();
            for(GridPane winningMiniGrid : winningMiniGrids){
                if( !winningMiniGrid.equals(lastMiniGrid) ){ // don't color background of last minigrid
                    winningMiniGrid.setBackground(background);
                }
            }
            timeline.stop();
            timeline = null;
        }

        if(game.player1Turn) {
            player1UndoButton.setDisable(true);
        } else{
            player2UndoButton.setDisable(true);
        }
    }

    private void  handleStartNewGameButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to start a new game?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Button yesButton = (Button) alert.getDialogPane().lookupButton( ButtonType.YES );
        yesButton.setDefaultButton( false );

        Button noButton = (Button) alert.getDialogPane().lookupButton( ButtonType.NO );
        noButton.setDefaultButton( true );

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.YES) {
                clickableMiniGrids.addAll(mainGrid.getChildren());
                mainGrid.setDisable(false);
                GameUtils.clearGridLines(graphicsContext, mainGrid);
                for(Node miniGridNode : mainGrid.getChildren()){
                    GridPane miniGrid = (GridPane) miniGridNode;
                    GameUtils.revertStateOfMiniGrid(miniGrid);
                    for(Node buttonNode : miniGrid.getChildren()){
                        Button button = (Button) buttonNode;
                        button.setDisable(false);
                        button.setGraphic(null);
                        button.setEffect(null);
                    }
                }
                game.resetBackEndGame();
                toggleTurnLabel();
                turnLabel.setVisible(true);
                resultLabel.setVisible(false);
                if(timeline != null){
                    timeline.stop();
                    timeline = null;
                }
            }
        });
    }

    private void initializeTurnLabel() {
        turnLabel = new Label();
        turnLabel.setAlignment(Pos.CENTER);
        toggleTurnLabel();
    }

    private void initializeResultLabel() {
        resultLabel = new Label();
        resultLabel.setAlignment(Pos.CENTER);
        resultLabel.setVisible(false);
    }

    private Button createUndoButton(Player player){
        Button undoButton = new Button();
        undoButton.setText(String.format("Player %s Undo", player.getLabelValue()));
        undoButton.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: %fem; -fx-border-color: %s; -fx-border-width: %d;",
                player.getLabelColor(), UNDO_BUTTON_FONT_SIZE, player.getLabelColor(), UNDO_BUTTON_BORDER_WIDTH));
        return undoButton;
    }

    private void initializeGrid() {
        mainGrid = new GridPane();
        for (int i = 0; i < 3; i++) {
            GridPane miniGrid = null;
            for (int j = 0; j < 3; j++) {
                miniGrid = createMiniGrid();
                mainGrid.add(miniGrid, i, j);
            }
        }

    }

    private GridPane createMiniGrid() {
        GridPane miniGrid = new GridPane();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = createCellButton();
                miniGrid.add(button, i, j);
            }
        }
        miniGrid.setStyle(String.format("-fx-border-color: black; -fx-border-width: %d;", MINIGRID_BLACK_BORDER_WIDTH));
        return miniGrid;
    }

    private void updateLines(GridPane miniGrid, List<WinningTriple> winningCoordinates) {
        double cellSize = CELL_BUTTON_SIZE;

        graphicsContext.setStroke(Color.BLACK); // Set line color
        graphicsContext.setLineWidth(5); // Set line width

//        System.out.println("OverlayCanvas layout: " + overlayCanvas.getLayoutX() + " x " + overlayCanvas.getLayoutY());
//        System.out.println("OverlayCanvas dimensions: " + overlayCanvas.getWidth() + " x " + overlayCanvas.getHeight());
//
//        System.out.println("mainGrid layout: " + mainGrid.getLayoutX() + " x " + mainGrid.getLayoutY());
//        System.out.println("mainGrid dimensions: " + mainGrid.getWidth() + " x " + mainGrid.getHeight());
//
//        System.out.println("miniGrid layout: " + miniGrid.getLayoutX() + " x " + miniGrid.getLayoutY());
//        System.out.println("miniGrid dimensions: " + miniGrid.getWidth() + " x " + miniGrid.getHeight());



        for(WinningTriple winningTriple : winningCoordinates){
            List<WinningTriple.Coordinate> coordinates = winningTriple.getCoordinates();

            int startRow = coordinates.get(0).getX();
            int endRow = coordinates.get(2).getX();
            int startCol = coordinates.get(0).getY();
            int endCol = coordinates.get(2).getY();

            if(startRow == endRow){ // horizontal line
                graphicsContext.strokeLine(
                        miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.2),
                        miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.5),
                        miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.8),
                        miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.5) );
            }else if(startCol == endCol){ // vertical line
                graphicsContext.strokeLine(
                        miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.5),
                        miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.2),
                        miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.5),
                        miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.8) );
            }else{ // diagonal
                if(endRow > startRow){ // down diagonal
                    graphicsContext.strokeLine(
                            miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.2),
                            miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.2),
                            miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.8),
                            miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.8) );
                }else{ // up diagonal
                    graphicsContext.strokeLine(
                            miniGrid.getLayoutX() + (startCol * cellSize) + (cellSize * 0.2),
                            miniGrid.getLayoutY() + (startRow * cellSize) + (cellSize * 0.8),
                            miniGrid.getLayoutX() + (endCol * cellSize) + (cellSize * 0.8),
                            miniGrid.getLayoutY() + (endRow * cellSize) + (cellSize * 0.2) );
                }
            }

        }
    }


    private Button createCellButton() {
        Button button = new Button();
        button.setMinSize(CELL_BUTTON_SIZE, CELL_BUTTON_SIZE);
        button.setOnAction(e -> {
            try {
                handleCellButtonClick(button);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return button;
    }

    private void handleCellButtonClick(Button button) throws Exception {
        GridPane miniGrid = GameUtils.findParentGridPane(button);
        if (isButtonEmpty(button) && isMiniGridClickable(miniGrid)) {
            Player player = game.player1Turn ? game.getPlayer1() : game.getPlayer2();
            Label buttonLabel = new Label();
            buttonLabel.setText(player.getLabelValue());
            buttonLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: %dem;", player.getLabelColor(), player.getLabelSize()));
            button.setGraphic(buttonLabel);

            game.recordMove(button, miniGrid);
            updateMiniGridIfWonOrTie(miniGrid);
            lastMove = new LastMove(button, miniGrid, new HashSet<>(clickableMiniGrids)); // record move data in case of undo

            clearClickableMiniGridsHighlighting();
            clickableMiniGrids.clear();

            boolean hasGameEnded = game.checkGameForWinOrTie();

            game.player1Turn = !game.player1Turn;
            if(!hasGameEnded){
                updateClickableMiniGrids(button);
                toggleTurnLabel();
            }else{
                showEndGameResult();
                mainGrid.setDisable(true);
                turnLabel.setVisible(false);
            }

            if(player.getId() == 1){
                player1UndoButton.setDisable(false);
                player2UndoButton.setDisable(true);
            }else{
                player2UndoButton.setDisable(false);
                player1UndoButton.setDisable(true);
            }
//            game.printUltimateTicTacToeGrid();
        }
    }

    private void toggleTurnLabel(){
        Player player = game.player1Turn ? game.getPlayer1() : game.getPlayer2();
        turnLabel.setText(String.format("Player %s's Turn", player.getLabelValue()));
        turnLabel.setStyle(String.format("-fx-font-size: %d; -fx-font-weight: bold; -fx-text-fill: %s;", TURN_LABEL_FONT_SIZE, player.getLabelColor()));
    }

    private boolean isButtonEmpty(Button button) {
        return button.getGraphic() == null;
    }

    private void showEndGameResult(){
        if(game.isTie){
            resultLabel.setText("IT'S A TIE!");
            resultLabel.setStyle(String.format("-fx-font-size: %s; -fx-font-weight: bold; -fx-text-fill: black;", RESULT_LABEL_FONT_SIZE));
        } else{
            Player winner = game.getPlayer1().wonGame ? game.getPlayer1() : game.getPlayer2();
            resultLabel.setText(String.format("PLAYER %s WON!", winner.getLabelValue()));
            resultLabel.setStyle(String.format("-fx-font-size: %s; -fx-font-weight: bold; -fx-text-fill: %s;", RESULT_LABEL_FONT_SIZE, winner.getLabelColor()));
        }

        List<WinningTriple> winningCoordinates = game.getLastWinningCoordinates();
        if( !winningCoordinates.isEmpty() ){
            GameUtils.setWinningMiniGrids(mainGrid, winningCoordinates);
            List<GridPane> winningMiniGrids = GameUtils.getWinningMiniGrids();
            flashBackgrounds(winningMiniGrids);
        }

        FadeTransition fadeTransition = GameUtils.getFadeTransition(resultLabel);
        fadeTransition.play();
        resultLabel.setVisible(true);
    }

    private void updateMiniGridIfWonOrTie(GridPane miniGrid) throws Exception {
        if(game.checkMiniGridForWin(miniGrid)){
            if(game.player1Turn){
                updateLines(miniGrid, game.getLastWinningCoordinates());
                miniGrid.setBackground(GameUtils.getPlayer1MiniGridBackground());
                miniGrid.setStyle(String.format("-fx-border-color: %s;  -fx-border-width: %d;", PLAYER1_LABEL_COLOR, MINIGRID_COLOR_BORDER_WIDTH));
            }else{
                updateLines(miniGrid, game.getLastWinningCoordinates());
                miniGrid.setBackground(GameUtils.getPlayer2MiniGridBackground());
                miniGrid.setStyle(String.format("-fx-border-color: %s; -fx-border-width: %d;", PLAYER2_LABEL_COLOR, MINIGRID_COLOR_BORDER_WIDTH));
            }
            miniGrid.setDisable(true);
        }else if(game.isGridComplete(miniGrid)){
            miniGrid.setEffect(GameUtils.getBlurEffect());
            miniGrid.setDisable(true);
        }
    }

    private boolean isMiniGridClickable(GridPane miniGrid) {
        return clickableMiniGrids.contains(miniGrid);
    }

    private void clearClickableMiniGridsHighlighting(){
        clickableMiniGrids.forEach(node -> GameUtils.toggleMiniGridHighlighting((GridPane) node, false));
    }

    private void updateClickableMiniGrids(Button button) {
        int buttonX = GridPane.getRowIndex(button);
        int buttonY = GridPane.getColumnIndex(button);
        GridPane nextMiniGrid = GameUtils.getGridPaneGivenIndices(mainGrid, buttonX, buttonY);

        if(!nextMiniGrid.isDisabled()){
            GameUtils.toggleMiniGridHighlighting(nextMiniGrid, true);
            clickableMiniGrids.add(nextMiniGrid);
        }else{
            for(Node node : mainGrid.getChildren()){
                if(!node.isDisabled()){
                    GameUtils.toggleMiniGridHighlighting((GridPane) node, true);
                    clickableMiniGrids.add(node);
                }
            }
        }
    }

    private void flashBackgrounds(List<GridPane> winningMiniGrids) {
        Background coloredBackground = game.getPlayer1().wonGame ? GameUtils.getPlayer1MiniGridBackground() : GameUtils.getPlayer2MiniGridBackground();
        Background transparentBackground = GameUtils.getTransparentMiniGridBackground();
        List<KeyFrame> keyFrameList = new ArrayList<>();

        for(GridPane miniGrid : winningMiniGrids){
            keyFrameList.add(new KeyFrame(Duration.seconds(0.5), e -> miniGrid.setBackground(coloredBackground)));
            keyFrameList.add(new KeyFrame(Duration.seconds(1), e -> miniGrid.setBackground(transparentBackground)));
        }

        timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
