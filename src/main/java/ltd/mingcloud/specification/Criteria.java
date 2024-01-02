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

  /**
   * Returns a Predicate that checks if the property value extracted from an object is equal to the given value.
   *
   * @param value the value to compare the property value with
   * @return a Predicate that performs the equality comparison
   */
  public Predicate<T> eq(R value) {
    return t -> Objects.equals(propertyExtractor.apply(t), value);
  }

  /**
   * Returns a Predicate that checks if the property value is less than the given value.
   *
   * @param value the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the less than comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> lt(V value) {
    return compareToValue(value, v -> v < 0);
  }

  /**
   * Creates a Predicate that compares a property value of type V with the specified value using the provided Predicate.
   *
   * @param value     the value to compare the property value with
   * @param predicate the Predicate that defines the comparison logic
   * @param <V>       the type of the property value, must implement the Comparable interface
   * @return a Predicate that performs the comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  private <V extends Comparable<V>> Predicate<T> compareToValue(V value, Predicate<Integer> predicate) {
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

  /**
   * Returns a Predicate that checks if the property value is greater than the given value.
   *
   * @param value the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the greater than comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> gt(V value) {
    return compareToValue(value, v -> v > 0);
  }

  /**
   * Returns a Predicate that checks if the property value is greater than or equal to the given value.
   *
   * @param value the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the greater than or equal to comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> ge(V value) {
    return compareToValue(value, v -> v >= 0);
  }

  /**
   * Returns a Predicate that checks if the property value is less than or equal to the given value.
   *
   * @param value the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the less than or equal to comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> le(V value) {
    return compareToValue(value, v -> v <= 0);
  }


  /**
   * Returns the property value extracted from the given object.
   *
   * @param target the object to extract the property value from
   * @return the property value
   */
  public R get(T target) {
    return propertyExtractor.apply(target);
  }
}
