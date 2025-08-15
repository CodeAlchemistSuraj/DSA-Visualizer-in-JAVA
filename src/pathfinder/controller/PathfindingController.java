package pathfinder.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.util.function.Supplier;
import pathfinder.algorithms.IPathfindingAlgorithm;
import pathfinder.model.Grid;
import pathfinder.ui.ControlPanel;
import pathfinder.ui.GridPanel;

/**
 * Controller responsible for coordinating pathfinding UI and model.
 * This is a drop-in, DI-friendly replacement for the event wiring currently inside `PathfindingView`.
 */
public class PathfindingController {
    private final GridPanel gridPanel;
    private final ControlPanel controlPanel;
    private final Supplier<IPathfindingAlgorithm> algorithmFactory;
    private SwingWorker<Void, Void> worker;

    public PathfindingController(GridPanel gridPanel, ControlPanel controlPanel, Supplier<IPathfindingAlgorithm> algorithmFactory) {
        this.gridPanel = gridPanel;
        this.controlPanel = controlPanel;
        this.algorithmFactory = algorithmFactory;
        wire();
    }

    private void wire() {
        controlPanel.onStart(() -> startSearch());
        controlPanel.onClear(() -> {
            if (worker != null && !worker.isDone()) worker.cancel(true);
            gridPanel.getGrid().clearWallsAndSearch();
            gridPanel.repaintOnEDT();
        });
        controlPanel.onResize(() -> {
            if (worker != null && !worker.isDone()) worker.cancel(true);
            int rows = controlPanel.getRows();
            int cols = controlPanel.getCols();
            gridPanel.resizeGrid(rows, cols);
            gridPanel.revalidate();
        });
        gridPanel.setToolSupplier(controlPanel::getActiveTool);
    }

    public void startSearch() {
        if (worker != null && !worker.isDone()) return;
        IPathfindingAlgorithm algorithm = algorithmFactory == null ? null : algorithmFactory.get();
        if (algorithm == null) {
            JOptionPane.showMessageDialog(gridPanel, "Please select an algorithm.");
            return;
        }
        if (!gridPanel.hasStartEnd()) {
            JOptionPane.showMessageDialog(gridPanel, "Please set both Start and End nodes (Shift+Click for Start, Ctrl+Click for End).");
            return;
        }
        controlPanel.setControlsEnabled(false);
        Grid grid = gridPanel.getGrid();
        grid.resetSearchState();
        gridPanel.repaint();
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                algorithm.findPath(grid, grid.getStartNode(), grid.getEndNode(), () -> {
                    try { Thread.sleep(controlPanel.getSpeedDelayMs()); } catch (InterruptedException ignored) {}
                    gridPanel.repaintOnEDT();
                });
                return null;
            }
            @Override
            protected void done() {
                gridPanel.repaintOnEDT();
                controlPanel.setControlsEnabled(true);
            }
        };
        worker.execute();
    }
}
