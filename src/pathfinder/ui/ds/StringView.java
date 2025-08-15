package pathfinder.ui.ds;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class StringView extends JPanel {
	private final JLabel statusLabel = new JLabel("Ready. Enter text and use the operations.");
	private final StringPanel stringPanel = new StringPanel(this::setStatusText);
	private final JTextField textField = new JTextField("racecar", 20);
	private final JTextField patternField = new JTextField("car", 10);
	private final JButton setTextButton = new JButton("Set Text");
	private final JButton reverseButton = new JButton("Reverse");
	private final JButton palindromeButton = new JButton("Palindrome?");
	private final JButton findButton = new JButton("Find Substring");
	private final JSlider speedSlider = new JSlider(0, 200, 20);

	// New controls
	private final JButton toUpperButton = new JButton("To Upper");
	private final JButton toLowerButton = new JButton("To Lower");
	private final JButton countButton = new JButton("Count Occurrences");
	private final JButton replaceButton = new JButton("Replace");
	private final JButton rotateLeftButton = new JButton("Rotate Left");
	private final JButton rotateRightButton = new JButton("Rotate Right");
	private final JButton removeVowelsButton = new JButton("Remove Vowels");
	private final JButton anagramButton = new JButton("Anagram?");
	private final JButton longestPalButton = new JButton("Longest Palindrome");
	private final JTextField replaceWithField = new JTextField("", 10);
	private final JSpinner rotateSteps = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

	public StringView() {
		super(new BorderLayout());
		JPanel controls = new JPanel();
		controls.add(new JLabel("Text:"));
		controls.add(textField);
		controls.add(setTextButton);
		controls.add(new JLabel("Pattern:"));
		controls.add(patternField);
		controls.add(findButton);
		controls.add(reverseButton);
		controls.add(palindromeButton);
		// new controls
		controls.add(toUpperButton);
		controls.add(toLowerButton);
		controls.add(countButton);
		controls.add(new JLabel("Replace with:"));
		controls.add(replaceWithField);
		controls.add(replaceButton);
		controls.add(new JLabel("Steps:"));
		controls.add(rotateSteps);
		controls.add(rotateLeftButton);
		controls.add(rotateRightButton);
		controls.add(removeVowelsButton);
		controls.add(anagramButton);
		controls.add(longestPalButton);
		controls.add(new JLabel("Speed:"));
		speedSlider.setInverted(true);
		controls.add(speedSlider);
		add(controls, BorderLayout.NORTH);
		add(stringPanel, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
		bottom.add(new JLabel("Legend: yellow = compared, green = match/confirmed"), BorderLayout.NORTH);
		bottom.add(statusLabel, BorderLayout.SOUTH);
		add(bottom, BorderLayout.SOUTH);

		wire();
	}

	private void wire() {
		setTextButton.addActionListener(e -> { stringPanel.setText(textField.getText()); setStatusText("Set text."); });
		reverseButton.addActionListener(e -> runAsync(() -> stringPanel.reverse(this::delay)));
		palindromeButton.addActionListener(e -> runAsync(() -> stringPanel.palindrome(this::delay)));
		findButton.addActionListener(e -> runAsync(() -> stringPanel.find(patternField.getText(), this::delay)));

		// new wiring
		toUpperButton.addActionListener(e -> runAsync(() -> stringPanel.toUpper(this::delay)));
		toLowerButton.addActionListener(e -> runAsync(() -> stringPanel.toLower(this::delay)));
		countButton.addActionListener(e -> runAsync(() -> stringPanel.countOccurrences(patternField.getText(), this::delay)));
		replaceButton.addActionListener(e -> runAsync(() -> stringPanel.replace(patternField.getText(), replaceWithField.getText(), this::delay)));
		rotateLeftButton.addActionListener(e -> runAsync(() -> stringPanel.rotateLeft((Integer) rotateSteps.getValue(), this::delay)));
		rotateRightButton.addActionListener(e -> runAsync(() -> stringPanel.rotateRight((Integer) rotateSteps.getValue(), this::delay)));
		removeVowelsButton.addActionListener(e -> runAsync(() -> stringPanel.removeVowels(this::delay)));
		anagramButton.addActionListener(e -> runAsync(() -> stringPanel.isAnagram(patternField.getText(), this::delay)));
		longestPalButton.addActionListener(e -> runAsync(() -> stringPanel.longestPalindromicSubstring(this::delay)));
	}

	private int delay() { return Math.max(1, speedSlider.getMaximum() - speedSlider.getValue() + 1); }
	private void setStatusText(String t) { SwingUtilities.invokeLater(() -> statusLabel.setText(t)); }
	private void runAsync(Runnable r) { new Thread(r).start(); }

	static class StringPanel extends JPanel {
		private char[] text = "racecar".toCharArray();
		private int left = -1, right = -1, i = -1, j = -1, matchL = -1, matchR = -1;
		private final java.util.function.Consumer<String> status;

		StringPanel(java.util.function.Consumer<String> status) {
			this.status = status;
			setPreferredSize(new Dimension(980, 520));
		}

		void setText(String s) {
			text = s == null ? new char[0] : s.toCharArray();
			left = right = i = j = matchL = matchR = -1; repaintOnEDT();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int cellW = Math.max(28, getWidth() / Math.max(1, text.length + 2));
			int baseY = 180;
			g.setFont(getFont().deriveFont(Font.BOLD, 14f));
			for (int k = 0; k < text.length; k++) {
				int x = k * cellW + 12;
				Color fill = new Color(230, 230, 230);
				if (k == i || k == j) fill = new Color(255, 193, 7);
				if (k >= matchL && k <= matchR && matchL != -1) fill = new Color(40, 167, 69);
				g.setColor(fill);
				g.fillRect(x, baseY, cellW - 8, 60);
				g.setColor(Color.DARK_GRAY);
				g.drawRect(x, baseY, cellW - 8, 60);
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(text[k]), x + (cellW / 2) - 4, baseY + 36);
				g.setColor(Color.GRAY);
				g.drawString("[" + k + "]", x + 6, baseY + 80);
			}
			if (left >= 0) drawPointer(g, left * cellW + cellW / 2, baseY - 30, "left");
			if (right >= 0) drawPointer(g, right * cellW + cellW / 2, baseY - 15, "right");
			if (i >= 0) drawPointer(g, i * cellW + cellW / 2, baseY - 45, "i");
			if (j >= 0) drawPointer(g, j * cellW + cellW / 2, baseY - 60, "j");
		}

		private void drawPointer(Graphics g, int centerX, int y, String label) {
			g.setColor(Color.BLACK);
			int[] xs = { centerX - 6, centerX + 6, centerX };
			int[] ys = { y, y, y + 8 };
			g.fillPolygon(xs, ys, 3);
			g.drawString(label, centerX - 14, y - 2);
		}

		private void repaintOnEDT() { if (SwingUtilities.isEventDispatchThread()) repaint(); else SwingUtilities.invokeLater(this::repaint); }
		private void stepDelay(IntSupplier d) { try { Thread.sleep(d.getAsInt()); } catch (InterruptedException ignored) {} }

		void reverse(IntSupplier d) {
			status.accept("Reverse string in place by swapping ends");
			int l = 0, r = text.length - 1;
			while (l < r) {
				left = l; right = r; i = l; j = r; repaintOnEDT(); stepDelay(d);
				char tmp = text[l]; text[l] = text[r]; text[r] = tmp;
				repaintOnEDT(); stepDelay(d);
				l++; r--;
			}
			left = right = i = j = -1; repaintOnEDT(); status.accept("Reversed.");
		}

		void palindrome(IntSupplier d) {
			status.accept("Palindrome check: compare symmetric characters");
			int l = 0, r = text.length - 1;
			while (l < r) {
				left = l; right = r; i = l; j = r; repaintOnEDT(); stepDelay(d);
				if (text[l] != text[r]) { status.accept("Mismatch: not a palindrome"); left = right = i = j = -1; repaintOnEDT(); return; }
				l++; r--;
			}
			status.accept("Palindrome!"); left = right = i = j = -1; repaintOnEDT();
		}

		void find(String pattern, IntSupplier d) {
			if (pattern == null) pattern = "";
			char[] p = pattern.toCharArray();
			status.accept("Find substring using naive scan");
			for (int start = 0; start + p.length <= text.length; start++) {
				i = start; j = -1; matchL = matchR = -1; repaintOnEDT(); stepDelay(d);
				boolean ok = true;
				for (int k = 0; k < p.length; k++) {
					j = start + k; i = start + k; repaintOnEDT(); stepDelay(d);
					if (text[start + k] != p[k]) { ok = false; break; }
					matchL = start; matchR = start + k; repaintOnEDT(); stepDelay(d);
				}
				if (ok) { status.accept("Found at index " + start); left = right = i = j = -1; repaintOnEDT(); return; }
			}
			status.accept("Pattern not found"); left = right = i = j = matchL = matchR = -1; repaintOnEDT();
		}

		// New string operations
		void toUpper(IntSupplier d) {
			status.accept("Converting to UPPERCASE");
			for (int k = 0; k < text.length; k++) {
				i = k; j = -1; repaintOnEDT(); stepDelay(d);
				text[k] = Character.toUpperCase(text[k]); repaintOnEDT(); stepDelay(d);
			}
			i = j = -1; repaintOnEDT(); status.accept("Converted to UPPERCASE");
		}

		void toLower(IntSupplier d) {
			status.accept("Converting to lowercase");
			for (int k = 0; k < text.length; k++) {
				i = k; j = -1; repaintOnEDT(); stepDelay(d);
				text[k] = Character.toLowerCase(text[k]); repaintOnEDT(); stepDelay(d);
			}
			i = j = -1; repaintOnEDT(); status.accept("Converted to lowercase");
		}

		void countOccurrences(String pattern, IntSupplier d) {
			if (pattern == null || pattern.length() == 0) { status.accept("Count: empty pattern"); return; }
			char[] p = pattern.toCharArray();
			status.accept("Counting occurrences of '" + pattern + "'");
			int cnt = 0;
			for (int start = 0; start + p.length <= text.length; start++) {
				i = start; j = -1; matchL = matchR = -1; repaintOnEDT(); stepDelay(d);
				boolean ok = true;
				for (int k = 0; k < p.length; k++) {
					j = start + k; i = start + k; repaintOnEDT(); stepDelay(d);
					if (text[start + k] != p[k]) { ok = false; break; }
					matchL = start; matchR = start + k; repaintOnEDT(); stepDelay(d);
				}
				if (ok) { cnt++; status.accept("Found at " + start + " (count=" + cnt + ")"); repaintOnEDT(); stepDelay(d); }
			}
			status.accept("Total occurrences = " + cnt);
			i = j = matchL = matchR = -1; repaintOnEDT();
		}

		void replace(String oldPat, String newPat, IntSupplier d) {
			if (oldPat == null || oldPat.length() == 0) { status.accept("Replace: empty pattern"); return; }
			if (newPat == null) newPat = "";
			status.accept("Replacing '" + oldPat + "' with '" + newPat + "'");
			String s = new String(text);
			int idx = s.indexOf(oldPat);
			while (idx != -1) {
				// highlight match
				matchL = idx; matchR = idx + oldPat.length() - 1; i = matchL; j = matchR; repaintOnEDT(); stepDelay(d);
				s = s.substring(0, idx) + newPat + s.substring(idx + oldPat.length());
				text = s.toCharArray(); repaintOnEDT(); stepDelay(d);
				idx = s.indexOf(oldPat, idx + newPat.length());
			}
			status.accept("Replace complete"); i = j = matchL = matchR = -1; repaintOnEDT();
		}

		void rotateLeft(int steps, IntSupplier d) {
			if (text.length <= 1) { status.accept("Rotate: nothing to do"); return; }
			steps = steps % text.length; status.accept("Rotate left by " + steps);
			for (int s = 0; s < steps; s++) {
				char first = text[0];
				for (int k = 1; k < text.length; k++) {
					i = k; j = k - 1; matchL = matchR = -1; repaintOnEDT(); stepDelay(d);
					text[k - 1] = text[k]; repaintOnEDT(); stepDelay(d);
				}
				text[text.length - 1] = first; repaintOnEDT(); stepDelay(d);
			}
			i = j = -1; repaintOnEDT(); status.accept("Rotate complete");
		}

		void rotateRight(int steps, IntSupplier d) {
			if (text.length <= 1) { status.accept("Rotate: nothing to do"); return; }
			steps = steps % text.length; status.accept("Rotate right by " + steps);
			for (int s = 0; s < steps; s++) {
				char last = text[text.length - 1];
				for (int k = text.length - 1; k > 0; k--) {
					i = k; j = k - 1; matchL = matchR = -1; repaintOnEDT(); stepDelay(d);
					text[k] = text[k - 1]; repaintOnEDT(); stepDelay(d);
				}
				text[0] = last; repaintOnEDT(); stepDelay(d);
			}
			i = j = -1; repaintOnEDT(); status.accept("Rotate complete");
		}

		void removeVowels(IntSupplier d) {
			status.accept("Removing vowels");
			StringBuilder sb = new StringBuilder();
			for (int k = 0; k < text.length; k++) {
				i = k; repaintOnEDT(); stepDelay(d);
				char c = text[k];
				if ("aeiouAEIOU".indexOf(c) == -1) sb.append(c);
			}
			text = sb.toString().toCharArray(); i = -1; repaintOnEDT(); status.accept("Vowels removed");
		}

		void isAnagram(String other, IntSupplier d) {
			if (other == null) other = "";
			status.accept("Checking anagram vs '" + other + "'");
			int[] cnt = new int[256];
			for (int k = 0; k < text.length; k++) { i = k; repaintOnEDT(); stepDelay(d); cnt[text[k]]++; }
			for (int k = 0; k < other.length(); k++) { j = k; repaintOnEDT(); stepDelay(d); cnt[other.charAt(k)]--; }
			boolean ok = true; for (int v : cnt) if (v != 0) { ok = false; break; }
			if (ok) status.accept("Anagram!"); else status.accept("Not an anagram");
			i = j = -1; repaintOnEDT();
		}

		void longestPalindromicSubstring(IntSupplier d) {
			status.accept("Finding longest palindromic substring (center expansion)");
			int bestL = 0, bestR = 0;
			for (int center = 0; center < text.length; center++) {
				// odd length
				int l = center, r = center;
				while (l >= 0 && r < text.length && text[l] == text[r]) {
					i = l; j = r; matchL = l; matchR = r; repaintOnEDT(); stepDelay(d);
					if (r - l > bestR - bestL) { bestL = l; bestR = r; }
					l--; r++;
				}
				// even length
				l = center; r = center + 1;
				while (l >= 0 && r < text.length && text[l] == text[r]) {
					i = l; j = r; matchL = l; matchR = r; repaintOnEDT(); stepDelay(d);
					if (r - l > bestR - bestL) { bestL = l; bestR = r; }
					l--; r++;
				}
			}
			matchL = bestL; matchR = bestR; i = j = -1; repaintOnEDT(); status.accept("Longest palindromic substring: [" + bestL + "," + bestR + "]");
		}
		
	}

	@FunctionalInterface interface IntSupplier { int getAsInt(); }
}