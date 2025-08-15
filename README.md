# Pathfinder Visualizer

A focused educational Java Swing application for visualizing pathfinding algorithms and related data-structure demonstrations.

This repository provides an interactive grid-based pathfinding visualizer plus several data-structure visualizers (arrays, strings, stacks, trees, recursion examples). The codebase has been refactored toward a clearer MVC structure: models and algorithms are separated from views and controllers to make it easier to extend and test.

## Highlights
- Interactive grid with Start/End placement and walls.
- Pathfinding algorithms: Breadth-First Search (BFS), Dijkstra, A*, Best-First Search, Bidirectional Search, Floyd–Warshall (small grids), D* Lite (demo wrapper).
- Array visualizer with many operations; now backed by a model (`pathfinder.model.ArrayData`) and a controller (`pathfinder.controller.ArrayController`).
- Additional algorithm utilities implemented for learning (sorting, searching, graphs, BST, Union-Find, Bellman-Ford, etc.).
- Animator utility to centralize animation loops (`pathfinder.util.Animator`).

## Project structure (important files)
- `src/pathfinder/App.java` — application entry point.
- `src/pathfinder/ui/VisualizerFrame.java` — main window.
- `src/pathfinder/ui/PathfindingView.java` — grid and controls for pathfinding.
- `src/pathfinder/ui/GridPanel.java` — paints the grid and handles mouse tools.
- `src/pathfinder/model/Grid.java`, `src/pathfinder/model/Node.java` — core grid model types.
- `src/pathfinder/model/ArrayData.java` — array model (operations, events).
- `src/pathfinder/controller/PathfindingController.java` — coordinates UI and pathfinding algorithms.
- `src/pathfinder/algorithms/` — algorithm implementations (pathfinding + many DSA utilities).

## Build & run (Windows)
1. Compile all Java sources into `bin`:

```powershell
# From repository root
Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName } > filelist.txt
javac -d bin -sourcepath src @filelist.txt
```

2. Run the application (Swing GUI):

```powershell
java -cp bin pathfinder.App
```

Notes:
- The project is not a general-purpose DSA visualization library; it is focused on the Pathfinder visualizer and a set of educational visualizers included in the UI.
- For JDK 21 compatibility, compile with `--release 21` if you need classfile compatibility for that Java version:

```powershell
javac --release 21 -d bin -sourcepath src @filelist.txt
```

## Implemented algorithms (selected)
- Pathfinding: BFS, Dijkstra, A*, Best-First, Bidirectional Search, Floyd–Warshall (small grids), D* Lite (demo wrapper)
- Sorting & searching: MergeSort, QuickSort, HeapSort, InsertionSort, SelectionSort, BinarySearchUtil
- Graphs: DFS, Topological sort, Bellman-Ford
- Data structures: BST, Union-Find, Heap helpers
- Array and string operations implemented in models and visualizers (see `ArrayData` and `StringData`)

## MVC & design notes
- Models (`pathfinder.model`) contain pure data and fire `PropertyChangeEvent`s to notify views.
- Views (`pathfinder.ui`) render models and forward UI events to controllers.
- Controllers (`pathfinder.controller`) coordinate models, algorithms, and views, and run algorithms on background threads when needed.
- Algorithms implement `pathfinder.algorithms.IPathfindingAlgorithm` and are swapped into controllers via a factory (DIP).

## Contributing
- If you add algorithms or views, keep algorithm logic out of Swing UI classes. Put data/logic in `model` or `algorithms` and let the `controller` drive the animation.
- Run `javac` to compile and add unit tests where practical.

## Running headless tests
- The UI is Swing-based; to test algorithm code headlessly, write small JUnit tests against classes in `src/pathfinder/algorithms` or model classes in `src/pathfinder/model`.

## License
This project follows the original repository's licensing. Add or update a `LICENSE` file if you intend to change licensing.

---

If you want, I can:
- Add badges and a short setup section for specific JDK versions.
- Create a CONTRIBUTING.md and small unit tests for `ArrayData` and `BinarySearchUtil`.
- Open a PR with the current refactor and algorithm additions.

Which would you like next?
