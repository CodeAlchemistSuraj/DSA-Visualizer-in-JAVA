Pathfinder Visualizer
=====================

Interactive Data Structures & Algorithms visualizer implemented in Java Swing.

Features
- Pathfinding visualizations: BFS, Dijkstra, A* on a grid with start/end/walls.
- Sorting, Searching visualizations.
- Arrays: full set of operations animated (push, pop, unshift, shift, insert, delete, update, linear/binary search, find min/max, sum/avg, reverse, rotate, swap, count, clear, fill).
- Strings: many operations animated (reverse, palindrome check, substring find, case convert, count/replace, rotate, remove vowels, anagram check, longest palindromic substring).
- Stack, Trees, Recursion demos.
- Modernized UI theme and improved controls for learners.

Build (Windows PowerShell)
1. Compile all sources into the bin folder:
   Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName } > sources.txt; foreach ($f in Get-Content sources.txt) { javac -d bin $f }

2. Run the app (module-aware):
   java --module-path bin -m Pathfinder/pathfinder.App

Notes
- Use the "Arrays" and "Strings" tabs to experiment with the new animated operations and the speed slider to control animation speed.
- If you prefer to run without the module flag, you can run: java -cp bin pathfinder.App

How to push to GitHub
1. Create a new repository on GitHub (e.g. "PathfinderVisualizer").
2. Add the remote and push:
   git remote set-url origin https://github.com/<your-username>/<repo>.git
   git push -u origin main

If you want, provide the GitHub repository URL and I will push the local repository for you (I cannot create a remote repo on your behalf without authentication).
