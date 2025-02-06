package org.visualiser.graphvisualiser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.lang.Math;

public class GraphVisualizer extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Set<Integer> nodes = new HashSet<>();
    private Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
    private Map<Integer, double[]> nodePositions = new HashMap<>();

    private Map<String, Color> edgeColors = new HashMap<>();

    private final String filePath = "src/main/resources/graph.txt";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            loadGraph(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        randomizePositions();

        applyForceDirectedAlgorithm();

        Group root = new Group();
        drawEdges(root);
        drawNodes(root);

        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTYELLOW);
        primaryStage.setTitle("Graph Visualiser");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadGraph(String filePath) throws IOException, ValueException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) {
                throw new ValueException("Empty graph.txt File.");
            }

            int numNodes = Integer.parseInt(line.trim());

            String edgeLine;
            while ((edgeLine = br.readLine()) != null) {
                String[] parts = edgeLine.trim().split("\\s+");
                if (parts.length == 2) {
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);

                    adjacencyList.putIfAbsent(u, new ArrayList<>());
                    adjacencyList.putIfAbsent(v, new ArrayList<>());

                    adjacencyList.get(u).add(v);
                    adjacencyList.get(v).add(u);

                    nodes.add(u);
                    nodes.add(v);

                    String key1 = makeEdgeKey(u, v);
                    String key2 = makeEdgeKey(v, u);
                    if (!edgeColors.containsKey(key1) && !edgeColors.containsKey(key2)) {
                        Color c = generateUniqueColor();
                        edgeColors.put(key1, c);
                        edgeColors.put(key2, c);
                    }
                }
            }

            if (nodes.size() != numNodes) {
                throw new ValueException("Vertexes number (" + nodes.size() +
                        ") is diffent than declared one (" + numNodes + ").");
            }
        }

        System.out.println("Adjacency list: " + adjacencyList);
        System.out.println("Nodes loaded: " + nodes);
    }

    private String makeEdgeKey(int u, int v) {
        return u + "-" + v;
    }

    private Color generateUniqueColor() {
        Random rand = new Random();
        Color color;
        do {
            color = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        } while (edgeColors.containsValue(color));
        return color;
    }

    private void randomizePositions() {
        Random rand = new Random();
        for (Integer node : nodes) {
            double x = 50 + rand.nextDouble() * (WIDTH - 100);
            double y = 50 + rand.nextDouble() * (HEIGHT - 100);
            nodePositions.put(node, new double[]{x, y});
        }
    }
    private void applyForceDirectedAlgorithm() {
        int iterations = 20;
        double k = Math.sqrt((WIDTH * HEIGHT) / (double) nodes.size());

        for (int i = 0; i < iterations; i++) {
            Map<Integer, double[]> displacements = new HashMap<>();
            for (Integer v : nodes) {
                displacements.put(v, new double[]{0, 0});
            }

            for (Integer v : nodes) {
                for (Integer u : nodes) {
                    if (!u.equals(v)) {
                        double[] posV = nodePositions.get(v);
                        double[] posU = nodePositions.get(u);
                        double dx = posV[0] - posU[0];
                        double dy = posV[1] - posU[1];
                        double dist = Math.sqrt(dx*dx + dy*dy);
                        if (dist > 0) {
                            double rep = (k*k) / dist;
                            displacements.get(v)[0] += (dx/dist) * rep;
                            displacements.get(v)[1] += (dy/dist) * rep;
                        }
                    }
                }
            }

            for (Integer v : adjacencyList.keySet()) {
                for (Integer u : adjacencyList.get(v)) {
                    if (u > v) {
                        double[] posV = nodePositions.get(v);
                        double[] posU = nodePositions.get(u);
                        double dx = posV[0] - posU[0];
                        double dy = posV[1] - posU[1];
                        double dist = Math.sqrt(dx*dx + dy*dy);
                        if (dist > 0) {
                            double attr = (dist*dist) / k;
                            displacements.get(v)[0] -= (dx/dist)*attr;
                            displacements.get(v)[1] -= (dy/dist)*attr;
                            displacements.get(u)[0] += (dx/dist)*attr;
                            displacements.get(u)[1] += (dy/dist)*attr;
                        }
                    }
                }
            }

            double step = 0.1;
            for (Integer node : nodes) {
                double[] oldPos = nodePositions.get(node);
                double[] disp = displacements.get(node);

                double newX = oldPos[0] + disp[0]*step;
                double newY = oldPos[1] + disp[1]*step;

                newX = Math.max(50, Math.min(WIDTH-50, newX));
                newY = Math.max(50, Math.min(HEIGHT-50, newY));

                nodePositions.put(node, new double[]{newX, newY});
            }
        }
    }

    private void drawEdges(Group root) {
        for (Integer v : adjacencyList.keySet()) {
            for (Integer u : adjacencyList.get(v)) {
                if (u > v) {
                    double[] pv = nodePositions.get(v);
                    double[] pu = nodePositions.get(u);
                    Line line = new Line(pv[0], pv[1], pu[0], pu[1]);
                    line.setStroke(edgeColors.get(makeEdgeKey(v, u)));
                    line.setStrokeWidth(2);

                    root.getChildren().add(line);
                }
            }
        }
    }

    private void drawNodes(Group root) {
        for (Integer node : nodes) {
            double[] pos = nodePositions.get(node);
            double x = pos[0];
            double y = pos[1];

            Circle circle = new Circle(x, y, 20);
            circle.setFill(Color.DARKBLUE);
            circle.setStroke(Color.DARKBLUE);
            circle.setStrokeWidth(3);

            Text text = new Text(x - 7, y + 5, String.valueOf(node));
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Helvetica", 14));

            root.getChildren().addAll(circle, text);
        }
    }
}
