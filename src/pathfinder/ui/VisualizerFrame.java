package pathfinder.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import pathfinder.ui.sort.SortingView;
import pathfinder.ui.search.SearchingView;
import pathfinder.ui.ds.ArrayView;
import pathfinder.ui.ds.StringView;
import pathfinder.ui.recursion.RecursionView;
import pathfinder.ui.trees.TreeView;
import pathfinder.ui.ds.StackView;

public class VisualizerFrame extends JFrame {
	public VisualizerFrame() {
		super("DSA Visualizer — Interactive");
		// Apply theme
		Theme.apply();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setMinimumSize(new Dimension(1200, 850));

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Pathfinding", new PathfindingView());
		tabs.addTab("Sorting", new SortingView());
		tabs.addTab("Searching", new SearchingView());
		tabs.addTab("Arrays", new ArrayView());
		tabs.addTab("Strings", new StringView());
		tabs.addTab("Recursion", new RecursionView());
		tabs.addTab("Trees", new TreeView());
		tabs.addTab("Stack", new StackView());
		add(tabs, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
	}
}