package com.example.ultimatetictactoe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

import static com.example.ultimatetictactoe.Constants.*;

public class UltimateTicTacToe extends Application {

    private UltimateTicTacToeBackEndGame game;
    private GridPane mainGrid;
    private Set<Node> clickableMiniGrids;
    private Label turnLabel;
    private Label resultLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new UltimateTicTacToeBackEndGame();
        mainGrid = new GridPane();
        initializeGrid();
        clickableMiniGrids = new HashSet<>(mainGrid.getChildren());

        ScrollPane scrollPane = new ScrollPane(mainGrid);
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setPadding(new Insets(0, 50, 0, 0)); // padding to the right of grid

        initializeTurnLabel();
        initializeResultLabel();

        Button player1UndoButton = createUndoButton(game.getPlayer1());
        Button player2UndoButton = createUndoButton(game.getPlayer2());

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
        verticalBox.setPadding(new Insets(275, 0, 0, 0)); // padding above to push resultLabel and startNewGameButton down

        HBox horizontalBox = new HBox(scrollPane, verticalBox);

        VBox rootVerticalBox = new VBox(turnLabel, horizontalBox);
        rootVerticalBox.setPadding(new Insets(0, 0, 0, 50)); // pad left of root

        Scene scene = new Scene(rootVerticalBox, SCROLLPANE_WIDTH, SCROLLPANE_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Ultimate Tic Tac Toe");
        primaryStage.show();
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
                for(Node miniGridNode : mainGrid.getChildren()){
                    GridPane miniGrid = (GridPane) miniGridNode;
                    miniGrid.setDisable(false);
                    miniGrid.setEffect(null);
                    miniGrid.setBackground(null);
                    miniGrid.setStyle(String.format("-fx-border-color: black; -fx-border-width: %d;", MINIGRID_BORDER_WIDTH));
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
        resultLabel.setVisible(false);  // Initially set to invisible
    }

    private Button createUndoButton(Player player){
        Button undoButton = new Button();
        undoButton.setText(String.format("Player %s undo (%d left)", player.getLabelValue(), player.undos));
        undoButton.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: %fem; -fx-border-color: %s; -fx-border-width: %d;",
                player.getLabelColor(), UNDO_BUTTON_FONT_SIZE, player.getLabelColor(), UNDO_BUTTON_BORDER_WIDTH));
        return undoButton;
    }

    private void initializeGrid() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                GridPane miniGrid = createMiniGrid();
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
        miniGrid.setStyle(String.format("-fx-border-color: black; -fx-border-width: %d;", MINIGRID_BORDER_WIDTH));
        return miniGrid;
    }

    private Button createCellButton() {
        Button button = new Button();
        button.setMinSize(CELL_BUTTON_SIZE, CELL_BUTTON_SIZE);
        button.setOnAction(e -> handleCellButtonClick(button));
        return button;
    }

    private void handleCellButtonClick(Button button) {
        GridPane miniGrid = findParentGridPane(button);
        if (isButtonEmpty(button) && isMiniGridClickable(miniGrid)) {
            Player player = game.player1Turn ? game.getPlayer1() : game.getPlayer2();
            Label buttonLabel = new Label();
            buttonLabel.setText(player.getLabelValue());
            buttonLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: %dem;", player.getLabelColor(), player.getLabelSize()));
            button.setGraphic(buttonLabel);

            backEndGameLogic(button, miniGrid);
            clearClickableMiniGrids(miniGrid);
            boolean gameHasEnded = showResultIfGameEnded();

            if(!gameHasEnded){
                updateClickableMiniGrids(button);
                game.player1Turn = !game.player1Turn;
                toggleTurnLabel();
            }else{
                mainGrid.setDisable(true);
                turnLabel.setVisible(false);
            }
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

    private GridPane findParentGridPane(Node node) {
        Parent parent = node.getParent();
        while (parent != null && !(parent instanceof GridPane)) {
            parent = parent.getParent();
        }
        return (GridPane) parent;
    }

    private boolean showResultIfGameEnded(){
        if(game.isTie || game.getPlayer1().wonGame || game.getPlayer2().wonGame){
            if(game.isTie){
                resultLabel.setText("IT'S A TIE!");
                resultLabel.setStyle(String.format("-fx-font-size: %s; -fx-font-weight: bold; -fx-text-fill: black;", RESULT_LABEL_FONT_SIZE));
            } else{
                Player winner = game.getPlayer1().wonGame ? game.getPlayer1() : game.getPlayer2();
                resultLabel.setText(String.format("PLAYER %s WON!", winner.getLabelValue()));
                resultLabel.setStyle(String.format("-fx-font-size: %s; -fx-font-weight: bold; -fx-text-fill: %s;", RESULT_LABEL_FONT_SIZE, winner.getLabelColor()));
            }
            resultLabel.setVisible(true);
            return true;
        }
        return false;
    }

    private void backEndGameLogic(Button button, GridPane miniGrid){
        game.recordMove(button, miniGrid);
        if(game.checkMiniGridForWin(miniGrid)){
            miniGrid.setDisable(true);
            if(game.player1Turn){
                miniGrid.setBackground(new Background(new BackgroundFill(Color.BLUE,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
                miniGrid.setStyle("-fx-border-color: darkblue;  -fx-border-width: 4;");
            }else{
                miniGrid.setBackground(new Background(new BackgroundFill(Color.rgb(255, 127, 127),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
                miniGrid.setStyle("-fx-border-color: darkred;  -fx-border-width: 4;");
            }
        }else if(game.isGridComplete(miniGrid)){
            GaussianBlur blur = new GaussianBlur();
            blur.setRadius(5);
            miniGrid.setEffect(blur);
            miniGrid.setDisable(true);
        }
        game.checkGameForWin();
    }

    private boolean isMiniGridClickable(GridPane miniGrid) {
        return clickableMiniGrids.contains(miniGrid);
    }

    private void clearClickableMiniGrids(GridPane currentMiniGrid){
        if(clickableMiniGrids.size() > 1){
            for(Node node : clickableMiniGrids){
                toggleHighlightingOfMiniGrid((GridPane) node, null);
            }
        }else{
            toggleHighlightingOfMiniGrid(currentMiniGrid, null);
        }
        clickableMiniGrids.clear();
    }

    private void updateClickableMiniGrids(Button button) {
        int buttonX = GridPane.getRowIndex(button);
        int buttonY = GridPane.getColumnIndex(button);
        GridPane nextMiniGrid = (GridPane)getNodeGivenIndices(buttonX, buttonY);
        DropShadow glow = new DropShadow();
        glow.setColor(Color.YELLOW);
        glow.setSpread(0.8); // Set the width of the glow

        if(!nextMiniGrid.isDisabled()){
            toggleHighlightingOfMiniGrid(nextMiniGrid, glow);
            clickableMiniGrids.add(nextMiniGrid);
        }else{
            for(Node node : mainGrid.getChildren()){
                if(!node.isDisabled()){
                    toggleHighlightingOfMiniGrid((GridPane) node, glow);
                    clickableMiniGrids.add(node);
                }
            }
        }
    }

    private void toggleHighlightingOfMiniGrid(GridPane miniGrid, DropShadow glow){
        for(Node node : miniGrid.getChildren()){
            node.setEffect(glow);
        }
    }

    private Node getNodeGivenIndices(int buttonX, int buttonY) {
        for(Node node : mainGrid.getChildren()){
            if(GridPane.getRowIndex(node) == buttonX && GridPane.getColumnIndex(node) == buttonY){
                return node;
            }
        }
        return null;
    }
}
