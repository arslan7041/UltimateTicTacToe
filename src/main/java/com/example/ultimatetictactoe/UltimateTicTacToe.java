package com.example.ultimatetictactoe;

import com.example.ultimatetictactoe.artificialintelligence.ArtificialIntelligenceEngine;
import com.example.ultimatetictactoe.artificialintelligence.BestMove;
import com.example.ultimatetictactoe.artificialintelligence.MoveEvent;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.ultimatetictactoe.Constants.*;

public class UltimateTicTacToe extends Application {

    private UltimateTicTacToeBackEndGame game;
    private GridPane mainGrid;
    private Set<Node> clickableMiniGrids;
    private Label turnLabel;
    private Label resultLabel;
    private Button player1UndoButton;
    private Button player2UndoButton;
    private LastMove lastMove;

    private ArtificialIntelligenceEngine artificialIntelligenceEngine;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new UltimateTicTacToeBackEndGame();
        initializeGrid();
        clickableMiniGrids = new HashSet<>(mainGrid.getChildren());
        Canvas overlayCanvas = GameUtils.createOverlayCanvas(mainGrid);
        GameUtils.setGraphicsContextObject(overlayCanvas);

        StackPane stackPane = new StackPane(mainGrid, overlayCanvas);
        stackPane.setStyle("-fx-background-color:transparent;");
        stackPane.setPadding(new Insets(0, 50, 0, 0)); // padding to the right of grid

        initializeTurnLabel();
        initializeResultLabel();

        player1UndoButton = createAndSetActionOnUndoButton(game.getPlayer1());
        player2UndoButton = createAndSetActionOnUndoButton(game.getPlayer2());

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

        artificialIntelligenceEngine = new ArtificialIntelligenceEngine(game, clickableMiniGrids);
    }

    private void handleUndoButtonClick() {
        game.setPlayer1Turn(!game.isPlayer1Turn());
        toggleTurnLabel();
        Button lastButton = lastMove.getButton();
        GridPane lastMiniGrid = lastMove.getMiniGrid();
        Set<Node> lastClickableMiniGrids = lastMove.getClickableMiniGrids();

        clearClickableMiniGrids();
        clickableMiniGrids = lastClickableMiniGrids;
        if(clickableMiniGrids.size() < 9){
            for(Node node : clickableMiniGrids){
                GameUtils.toggleMiniGridHighlighting((GridPane) node, true);
            }
        }

        lastButton.setGraphic(null);
        game.undoMove(lastButton, lastMiniGrid);

        GameUtils.stopBackGroundFlashing();
        if(lastMiniGrid.isDisabled()){
            GameUtils.revertStateOfMiniGrid(lastMiniGrid);
            GameUtils.clearGridLines(lastMiniGrid);
            game.undoMiniGridCompletionAndWin(lastMiniGrid);
        }

        if(mainGrid.isDisabled()){
            mainGrid.setDisable(false);
            resultLabel.setVisible(false);
            turnLabel.setVisible(true);

            if(game.isTie()){
                game.setTie(false);
            } else if(game.getPlayer1().hasWonGame()){
                game.getPlayer1().hasWonGame(false);
            } else if(game.getPlayer2().hasWonGame()){
                game.getPlayer2().hasWonGame(false);
            }
        }

        if(game.isPlayer1Turn()) {
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
                GameUtils.clearGridLines(mainGrid);
                GameUtils.stopBackGroundFlashing();
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

    private Button createAndSetActionOnUndoButton(Player player){
        Button undoButton = new Button();
        undoButton.setText(String.format("Player %s Undo", player.getLabelValue()));
        undoButton.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: %fem; -fx-border-color: %s; -fx-border-width: %d;",
                player.getLabelColor(), UNDO_BUTTON_FONT_SIZE, player.getLabelColor(), UNDO_BUTTON_BORDER_WIDTH));
        undoButton.setDisable(true);
        undoButton.setOnAction(e -> handleUndoButtonClick());
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

    private void drawWinningLines(GridPane miniGrid, List<WinningTriple> winningCoordinates) {
        for(WinningTriple winningTriple : winningCoordinates){
            GameUtils.drawWinningLine(miniGrid, winningTriple.getCoordinates());
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
        button.addEventHandler(MoveEvent.MOVE_COMPLETED, eventHandler -> AIMoveHandler());
        return button;
    }

    private void handleCellButtonClick(Button button) throws Exception {
        GridPane miniGrid = GameUtils.findParentGridPane(button);
        if(isMiniGridClickable(miniGrid) && isButtonEmpty(button)) {
            Player player = game.isPlayer1Turn() ? game.getPlayer1() : game.getPlayer2();

            player.makeMove(button);
            game.recordMove(button, miniGrid);
            updateMiniGridIfWonOrTie(miniGrid, player);
            lastMove = new LastMove(button, miniGrid, new HashSet<>(clickableMiniGrids)); // record move data in case of undo
            clearClickableMiniGrids();
            boolean isGameOver = game.isGameOver();

            game.setPlayer1Turn(!game.isPlayer1Turn());
            if(!isGameOver){
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

            game.printUltimateTicTacToeGrid();
            if(!game.isPlayer1Turn()){
                button.fireEvent(new MoveEvent(MoveEvent.MOVE_COMPLETED));
            }
        }
    }

    public void AIMoveHandler(){
        PauseTransition pause = new PauseTransition(Duration.seconds(1)); // Adjust the duration as needed
        pause.setOnFinished(event -> {
            BestMove bestMove = null;
            try {
                bestMove = artificialIntelligenceEngine.getBestAIMove();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (bestMove.getMove() != null) {
                bestMove.getMove().getButton().fire();
            }
        });
        pause.play();
    }

    private void toggleTurnLabel(){
        Player player = game.isPlayer1Turn() ? game.getPlayer1() : game.getPlayer2();
        turnLabel.setText(String.format("Player %s's Turn", player.getLabelValue()));
        turnLabel.setStyle(String.format("-fx-font-size: %d; -fx-font-weight: bold; -fx-text-fill: %s;", TURN_LABEL_FONT_SIZE, player.getLabelColor()));
    }

    private boolean isButtonEmpty(Button button) {
        return button.getGraphic() == null;
    }

    private void showEndGameResult(){
        if (game.isTie()) {
            setCommonLabelStyles("IT'S A TIE!", "black", "black");
        } else {
            Player winner = game.getPlayer1().hasWonGame() ? game.getPlayer1() : game.getPlayer2();
            setCommonLabelStyles(String.format("PLAYER %s WON!", winner.getLabelValue()), winner.getLabelColor(), winner.getLabelColor());
        }

        List<WinningTriple> winningCoordinates = game.getLastWinningCoordinates();
        if( !winningCoordinates.isEmpty() ){
            GameUtils.setWinningMiniGrids(mainGrid, winningCoordinates);
            GameUtils.flashBackGrounds(game.getPlayer1().hasWonGame() ? GameUtils.getPlayer1MiniGridBackground() : GameUtils.getPlayer2MiniGridBackground());
        }

        FadeTransition fadeTransition = GameUtils.getFadeTransition(resultLabel);
        fadeTransition.play();
        resultLabel.setVisible(true);
    }

    private void setCommonLabelStyles(String text, String textColor, String borderColor) {
        resultLabel.setText(text);
        resultLabel.setStyle(String.format(
                        "-fx-font-size: %s; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: %s; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: %dpx; " +
                        "-fx-border-radius: %d; " +
                        "-fx-padding: %d;",
                RESULT_LABEL_FONT_SIZE, textColor, borderColor, RESULT_LABEL_BORDER_WIDTH, RESULT_LABEL_BORDER_RADIUS, RESULT_LABEL_PADDING));
    }

    private void updateMiniGridIfWonOrTie(GridPane miniGrid, Player player) throws Exception {
        if(game.checkMiniGridForWin(miniGrid)){
            drawWinningLines(miniGrid, game.getLastWinningCoordinates());
            miniGrid.setBackground(player.getId() == 1 ? GameUtils.getPlayer1MiniGridBackground() : GameUtils.getPlayer2MiniGridBackground());
            miniGrid.setStyle(String.format("-fx-border-color: %s;  -fx-border-width: %d;", player.getLabelColor(), MINIGRID_COLOR_BORDER_WIDTH));
            miniGrid.setDisable(true);
        }else if(game.isGridComplete(miniGrid)){
            miniGrid.setEffect(GameUtils.getBlurEffect());
            miniGrid.setDisable(true);
        }
    }

    private boolean isMiniGridClickable(GridPane miniGrid) {
        return clickableMiniGrids.contains(miniGrid);
    }

    private void clearClickableMiniGrids(){
        clickableMiniGrids.forEach(miniGrid -> GameUtils.toggleMiniGridHighlighting((GridPane) miniGrid, false));
        clickableMiniGrids.clear();
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
}
