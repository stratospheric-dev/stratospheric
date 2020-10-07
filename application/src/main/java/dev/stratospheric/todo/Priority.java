package dev.stratospheric.todo;

public enum Priority {
  HIGH(1),
  DEFAULT(2),
  LOW(3);

  private final int displayValue;

  Priority(int displayValue) {
    this.displayValue = displayValue;
  }

  public int getDisplayValue() {
    return displayValue;
  }
}
