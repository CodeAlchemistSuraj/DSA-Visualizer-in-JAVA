package pathfinder.controller;

import pathfinder.model.StringData;

/**
 * Controller for string operations. Keeps UI and model decoupled.
 */
public class StringController {
    private final StringData model;

    public StringController(StringData model) { this.model = model; }

    public void setValue(String v) { model.setValue(v); }
    public void reverse() { model.reverse(); }
    public boolean isPalindrome() { return model.isPalindrome(); }
}
