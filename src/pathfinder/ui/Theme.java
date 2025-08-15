package pathfinder.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Centralized UI theme helper — apply once at startup to modernize the look.
 */
public final class Theme {
	public static void apply() {
		try {
			// Try Nimbus if available for a modern look
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception ignored) {}

		// Base font
		Font base = new Font("Segoe UI", Font.PLAIN, 13);
		UIManager.put("Label.font", new FontUIResource(base));
		UIManager.put("Button.font", new FontUIResource(base.deriveFont(Font.BOLD, 13f)));
		UIManager.put("ToggleButton.font", new FontUIResource(base));
		UIManager.put("TextField.font", new FontUIResource(base));
		UIManager.put("Spinner.font", new FontUIResource(base));
		UIManager.put("ComboBox.font", new FontUIResource(base));
		UIManager.put("TabbedPane.font", new FontUIResource(base.deriveFont(Font.BOLD, 14f)));
		UIManager.put("Slider.font", new FontUIResource(base));

		// Colors
		Color primary = new Color(18, 100, 163); // deep blue
		Color accent = new Color(0, 123, 255);
		Color surface = new Color(245, 247, 250); // light surface
		Color card = new Color(255, 255, 255);
		Color muted = new Color(120, 130, 140);

		UIManager.put("Panel.background", surface);
		UIManager.put("TabbedPane.background", surface);
		UIManager.put("TabbedPane.selected", card);
		UIManager.put("OptionPane.background", surface);
		UIManager.put("Button.background", card);
		UIManager.put("Button.foreground", primary);
		UIManager.put("Label.foreground", Color.DARK_GRAY);
		UIManager.put("TextField.background", Color.WHITE);
		UIManager.put("TextField.foreground", Color.DARK_GRAY);
		UIManager.put("Spinner.background", Color.WHITE);
		UIManager.put("ComboBox.background", Color.WHITE);

		// Borders and misc
		UIManager.put("ToolTip.background", new Color(255, 250, 220));
		UIManager.put("ToolTip.foreground", Color.DARK_GRAY);

		// Provide some paddings for controls that read default insets
		UIManager.put("TabbedPane.contentBorderInsets", new javax.swing.plaf.InsetsUIResource(10, 10, 10, 10));

		// Accent colors for selection actions
		UIManager.put("info", accent);
		UIManager.put("nimbusBlueGrey", muted);
	}

	private Theme() {}
}
