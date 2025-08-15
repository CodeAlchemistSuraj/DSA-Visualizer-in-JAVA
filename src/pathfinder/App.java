package pathfinder;

import javax.swing.SwingUtilities;
import pathfinder.ui.VisualizerFrame;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			VisualizerFrame frame = new VisualizerFrame();
			frame.setVisible(true);
		});
	}
} 