# 🎨 Undirected Graph Visualizer - JavaFX

## 📌 Project Overview
This project is a **Graph Visualizer** built with **JavaFX**.
- It reads an **undirected graph** from a `graph.txt` file.
- Uses a **force-directed algorithm** to position nodes dynamically.
- Displays **nodes as circles with numbers**.
- Draws **edges in random colors** for better visualization.

## 🚀 How to Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/user/graph-visualiser.git
   cd graph-visualiser
   ```
2. **Run the application**:
   ```bash
   mvn clean compile exec:java
   ```

## 📂 Graph File Format (`graph.txt`)
The input file describes an **undirected graph**:
- **First line** → Number of nodes.
- **Each following line** → Edge `u v`, where `u` and `v` are connected nodes.

### 🔹 Example (`graph.txt`)
```
20
0 1
1 2
2 3
3 4
4 5
5 6
6 7
7 8
8 9
9 0
0 10
10 11
11 12
12 13
13 14
14 15
15 16
16 17
17 18
18 19
19 10
3 17
6 12
8 14
5 11
1 15
7 19
```

## 🛠️ **Technologies Used**
- **Java 21**
- **JavaFX 21** (for visualization)
- **Maven** (dependency management)
- **Force-Directed Algorithm** for node positioning

---

## 🚀 **Example Visualization**  
![graph_visualization_example.png](img%2Fgraph_visualization_example.png)