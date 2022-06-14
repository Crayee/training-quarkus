package de.istec.training.example1.util;

public record Month(int year, int month) implements Comparable<Month> {

  public static Month of(int yyyyMM) {
    var year = yyyyMM / 100;
    return new Month(year, yyyyMM - year * 100);
  }

  public Month next() {
    return new Month(month == 12 ? year + 1 : year, month == 12 ? 1 : month + 1);
  }

  public Month previous() {
    return new Month(month == 1 ? year - 1 : year, month == 1 ? 12 : month - 1);
  }

  public boolean isAfter(Month m) {
    return compareTo(m) > 0;
  }

  public boolean isBefore(Month m) {
    return compareTo(m) < 0;
  }

  public Month max(Month compare) {
    return compare.isAfter(this) ? compare : this;
  }

  public Month min(Month compare) {
    return compare.isBefore(this) ? compare : this;
  }

  @Override
  public String toString() {
    return String.format("%04d-%02d", year, month);
  }

  @Override
  public int compareTo(Month o) {
    int compare = Integer.compare(year, o.year);
    if (compare == 0) compare = Integer.compare(month, o.month);
    return compare;
  }
}