import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * This implements Warshall's Algorithm to calculate the transitive closure of a matrix
 * then slaps it in a JavaFX ui in the hackiest way possible
 */
public class Main extends Application {

    /**
     * Obligatory bootstrap method
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // create the borderpane containing everything
        BorderPane outer = new BorderPane();

        // create the TextField for setting matrix size
        TextField matrixSize = new TextField();
        matrixSize.setPromptText("Matrix Size (nxn)");
        //TODO: This currently doesn't check the input, so a non-int will crash the program
        matrixSize.setOnAction(event -> outer.setCenter(createInputGrid(Integer.parseInt(matrixSize.getText()))));
        outer.setLeft(matrixSize);

        // create the button to generate the closure
        Button button = new Button("Calculate Closure");
        button.setOnAction(event -> {
            // grab the GridPane and cast it
            GridPane grid = (GridPane) outer.getCenter();

            // What follows here is evil, hacky, anti-oob Java mess
            // Basically, we work around private methods to get the number of rows and create an array with that number
            try {
                // Create a Method by accessing the GridPane class and getting the private method "getNumberOfRows()"
                Method method = grid.getClass().getDeclaredMethod("getNumberOfRows");

                // then force that method to be accessible instead of being private
                method.setAccessible(true);

                // finally, invoke the method to get the number of rows and create an array
                int numRows = (int) method.invoke(grid);
                int[][] matrix = new int[numRows][numRows];

                // read off every TextField into the matrix
                for(int y = 0; y < matrix.length; y++) {
                    for (int x = 0; x < matrix.length; x++) {
                        TextField n = (TextField) getNodeFromGridPane(grid, y, x);
                        matrix[x][y] = Integer.parseInt(n.getText());
                    }
                }

                // compute the closure
                int[][] out = computeClosure(matrix);

                // set every node in the GridPane to match the closure
                for(int y = 0; y < matrix.length; y++) {
                    for (int x = 0; x < matrix.length; x++) {
                        TextField n = (TextField) getNodeFromGridPane(grid, y, x);
                        n.setText(out[x][y] + "");
                    }
                }

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        });
        outer.setRight(button);

        // create a 3x3 grid to start off with and put it in the center
        outer.setCenter(createInputGrid(3));

        // Concluding boilerplate
        Scene scene = new Scene(outer, 500, 500);
        primaryStage.setTitle("Transitive Closure");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * A helper method to retrieve the node a a given column and row
     * TODO: Currently pretty inefficient, try to improve
     * @param gridPane The GridPane to retrieve a node from
     * @param col Target column
     * @param row Target row
     * @return The node representing the GridPane
     */
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        // for each node in the GridPane, if it matches the row and col return it
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    /**
     * Creates a new gridpane to represent the matrix
     *
     * @param size The size n for the nxn matrix
     * @return An nxn gridpane of TextFields containing 0
     */
    public GridPane createInputGrid(int size) {
        // create the GridPane
        GridPane root = new GridPane();

        // loop through the indices creating TextFields and increasing the indices
        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){

                // Create a new TextField in each Iteration
                TextField tf = new TextField();
                tf.setPrefHeight(50);
                tf.setPrefWidth(50);
                tf.setAlignment(Pos.CENTER);
                tf.setEditable(true);
                tf.setText("0");

                // Iterate the Index using the loops
                GridPane.setRowIndex(tf,y);
                GridPane.setColumnIndex(tf,x);
                root.getChildren().add(tf);
            }
        }

        // return the GridPane
        return root;
    }


    /**
     * Implementation of Warshall's Algorithm for calculating the transitive closure
     *
     * O(n^3)
     *
     * @param matrix The input matrix of relations
     * @return The transitive closure of that matrix
     */
    public static int[][] computeClosure(int[][] matrix) {
        // grab the matrix length to avoid calling matrix.length over and over
        int n = matrix.length;

        // create a copy of the original matrix that we can modify
        int[][] matrix1 = Arrays.copyOf(matrix, n);

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // if there is a path from node i to k and from node k  to j
                    // then there is a path from i to j, so we set i, j to 1
                    if (matrix[i][k] == 1 && matrix[k][j] == 1) matrix1[i][j] = 1;
                }
            }
        }

        return matrix;
    }
}
