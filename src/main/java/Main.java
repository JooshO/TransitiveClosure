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
    BorderPane outer;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        outer = new BorderPane();

        TextField matrixSize = new TextField();
        matrixSize.setPromptText("Matrix Size (nxn)");
        matrixSize.setOnAction(event -> outer.setCenter(createInputGrid(Integer.parseInt(matrixSize.getText()))));
        outer.setLeft(matrixSize);

        Button button = new Button("Calculate Closure");
        button.setOnAction(event -> {
            GridPane grid = (GridPane) outer.getCenter();
            Method method;
            try {
                method = grid.getClass().getDeclaredMethod("getNumberOfRows");
                method.setAccessible(true);
                Method method2 = grid.getClass().getDeclaredMethod("getNumberOfColumns");
                method2.setAccessible(true);
                int[][] matrix = new int[(int) method.invoke(grid)][(int) method2.invoke(grid)];
                for(int y = 0; y < matrix.length; y++) {
                    for (int x = 0; x < matrix.length; x++) {
                        TextField n = (TextField) getNodeFromGridPane(grid, y, x);
                        matrix[x][y] = Integer.valueOf(n.getText());
                    }
                }
                int[][] out = computeClosure(matrix);

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

        outer.setCenter(createInputGrid(3));
        outer.setRight(button);



        Scene scene = new Scene(outer, 500, 500);
        primaryStage.setTitle("Transitive Closure");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public GridPane createInputGrid(int size) {
        int SIZE = size;
        int length = SIZE;
        int width = SIZE;
        GridPane root = new GridPane();

        for(int y = 0; y < length; y++){
            for(int x = 0; x < width; x++){

                // Create a new TextField in each Iteration
                TextField tf = new TextField();
                tf.setPrefHeight(50);
                tf.setPrefWidth(50);
                tf.setAlignment(Pos.CENTER);
                tf.setEditable(true);
                tf.setText("0");

                // Iterate the Index using the loops
                root.setRowIndex(tf,y);
                root.setColumnIndex(tf,x);
                root.getChildren().add(tf);
            }
        }

        return root;
    }

    public static int[][] computeClosure(int[][] matrix) {
        int n = matrix.length;
        int[][] matrix1 = Arrays.copyOf(matrix, n);

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    if (matrix[i][k] == 1 && matrix[k][j] ==1 ) matrix1[i][j] = 1;
                    else matrix1[i][j] = matrix[i][j];
                }
        if (Arrays.equals(matrix, matrix1)) return matrix;
        else return computeClosure(matrix1);
    }
}
