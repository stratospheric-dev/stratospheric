package dev.aws101.todo;

public enum Priority {
  HIGH(1),
  DEFAULT(2),
  LOW(3);

  private final int value;

  Priority(final int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
