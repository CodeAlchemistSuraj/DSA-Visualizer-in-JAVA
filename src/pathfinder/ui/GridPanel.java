package pathfinder.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import pathfinder.model.Grid;
import pathfinder.model.Node;

public class GridPanel extends JPanel {
	public enum Tool { WALL, START, END }

	private Grid grid;
	private final int cellSize;
	private Supplier<Tool> toolSupplier = () -> Tool.WALL;
	private boolean isDragging = false;
	private boolean dragAddingWalls = true;

	public GridPanel(int rows, int cols, int cellSize) {
		this.grid = new Grid(rows, cols);
		this.cellSize = cellSize;
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(cols * cellSize + 1, rows * cellSize + 1));
		installMouseHandlers();
	}

	public void setToolSupplier(Supplier<Tool> supplier) {
		this.toolSupplier = supplier != null ? supplier : this.toolSupplier;
	}

	public Grid getGrid() { return grid; }

	public boolean hasStartEnd() { return grid.getStartNode() != null && grid.getEndNode() != null; }

	public void resizeGrid(int rows, int cols) {
		this.grid = new Grid(rows, cols);
		setPreferredSize(new Dimension(cols * cellSize + 1, rows * cellSize + 1));
		revalidate();
		repaint();
	}

	public void repaintOnEDT() {
		if (SwingUtilities.isEventDispatchThread()) {
			repaint();
		} else {
			SwingUtilities.invokeLater(this::repaint);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int rows = grid.getRows();
		int cols = grid.getCols();

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Node node = grid.getNode(r, c);
				g.setColor(colorFor(node));
				g.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
			}
		}

		g.setColor(new Color(220, 220, 220));
		for (int r = 0; r <= rows; r++) {
			g.drawLine(0, r * cellSize, cols * cellSize, r * cellSize);
		}
		for (int c = 0; c <= cols; c++) {
			g.drawLine(c * cellSize, 0, c * cellSize, rows * cellSize);
		}
	}

	private Color colorFor(Node node) {
		if (node.isStart()) return new Color(40, 167, 69);
		if (node.isEnd()) return new Color(220, 53, 69);
		if (node.isInPath()) return new Color(255, 193, 7);
		if (node.isVisited()) return new Color(0, 123, 255, 170);
		if (node.isFrontier()) return new Color(0, 123, 255, 90);
		if (node.isWall()) return Color.DARK_GRAY;
		return Color.WHITE;
	}

	private void installMouseHandlers() {
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point cell = toCell(e.getX(), e.getY());
				if (cell == null) return;
				Tool tool = toolSupplier.get();

				if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) tool = Tool.START;
				if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) tool = Tool.END;

				switch (tool) {
					case START:
						grid.setStart(cell.y, cell.x);
						break;
					case END:
						grid.setEnd(cell.y, cell.x);
						break;
					case WALL:
						Node n = grid.getNode(cell.y, cell.x);
						if (!n.isStart() && !n.isEnd()) {
							dragAddingWalls = !n.isWall();
							n.setWall(dragAddingWalls);
						}
						isDragging = true;
						break;
				}
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (!isDragging) return;
				Point cell = toCell(e.getX(), e.getY());
				if (cell == null) return;
				Node n = grid.getNode(cell.y, cell.x);
				if (!n.isStart() && !n.isEnd()) {
					n.setWall(dragAddingWalls);
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				isDragging = false;
			}
		};

		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	private Point toCell(int x, int y) {
		int col = x / cellSize;
		int row = y / cellSize;
		if (row < 0 || row >= grid.getRows() || col < 0 || col >= grid.getCols()) return null;
		return new Point(col, row);
	}
} 