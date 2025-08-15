package pathfinder.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Simple string model for visualizations. Notifies listeners on changes.
 */
public class StringData {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private String value;

    public StringData(String initial) { this.value = initial == null ? "" : initial; }

    public String getValue() { return value; }

    public void setValue(String v) { String old = this.value; this.value = v == null ? "" : v; pcs.firePropertyChange("value", old, this.value); }

    public void addPropertyChangeListener(PropertyChangeListener l) { pcs.addPropertyChangeListener(l); }
    public void removePropertyChangeListener(PropertyChangeListener l) { pcs.removePropertyChangeListener(l); }

    public void reverse() {
        String old = this.value;
        this.value = new StringBuilder(this.value).reverse().toString();
        pcs.firePropertyChange("reverse", old, this.value);
    }

    public boolean isPalindrome() {
        String s = this.value.replaceAll("\\s+", "").toLowerCase();
        return new StringBuilder(s).reverse().toString().equals(s);
    }
}
