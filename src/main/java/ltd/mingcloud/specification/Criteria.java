package ltd.mingcloud.specification;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

class Criteria<T, R> {

  private final Function<T, R> propertyExtractor;

  private Criteria(Function<T, R> propertyExtractor) {
    this.propertyExtractor = propertyExtractor;
  }

  public static <T, R> Criteria<T, R> of(Function<T, R> propertyExtractor) {
    if (propertyExtractor == null) {
      throw new IllegalArgumentException("Property Extractor should not be null");
    }
    return new Criteria<>(propertyExtractor);
  }

  public Predicate<T> eq(R value) {
    return t -> Objects.equals(propertyExtractor.apply(t), value);
  }

  public <V extends Comparable<V>> Predicate<T> lt(V value) {
    return compareValue(value, v -> v < 0);
  }

  private <V extends Comparable<V>> Predicate<T> compareValue(V value, Predicate<Integer> predicate) {
    return t -> {
      if (value == null) {
        throw new IllegalArgumentException("Cannot compare property to null");
      }

      try {
        @SuppressWarnings("unchecked") V propertyValue = (V) propertyExtractor.apply(t);
        if (propertyValue == null) {
          return false;
        }
        int compared = propertyValue.compareTo(value);
        return predicate.test(compared);
      } catch (ClassCastException e) {
        throw new IllegalArgumentException(
            "Property type is not comparable or value cannot cast to the type of property for comparing");
      }
    };
  }

  public <V extends Comparable<V>> Predicate<T> gt(V value) {
    return compareValue(value, v -> v > 0);
  }

  public <V extends Comparable<V>> Predicate<T> ge(V value) {
    return compareValue(value, v -> v >= 0);
  }

  public <V extends Comparable<V>> Predicate<T> le(V value) {
    return compareValue(value, v -> v <= 0);
  }


}
