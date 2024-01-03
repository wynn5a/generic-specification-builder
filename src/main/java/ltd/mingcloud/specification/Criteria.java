package ltd.mingcloud.specification;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

class Criteria<T, R> {

  private final Function<T, R> propertyExtractor;

  private Criteria(Function<T, R> propertyExtractor) {
    this.propertyExtractor = propertyExtractor;
  }

  /**
   * Creates a Criteria instance using the given property extractor.
   *
   * @param propertyExtractor the property extractor to use
   * @param <T>               the type of the object to extract the property value from
   * @param <R>               the type of the property value
   * @return a Criteria instance
   * @throws IllegalArgumentException if the property extractor is null
   */
  public static <T, R> Criteria<T, R> of(Function<T, R> propertyExtractor) {
    if (propertyExtractor == null) {
      throw new IllegalArgumentException("Property Extractor should not be null");
    }
    return new Criteria<>(propertyExtractor);
  }

  /**
   * Returns a Predicate that checks if the property value extracted from an object is equal to the given value.
   *
   * @param target the value to compare the property value with
   * @return a Predicate that performs the equality comparison
   */
  public Predicate<T> eq(R target) {
    return t -> Objects.equals(propertyExtractor.apply(t), target);
  }

  /**
   * Returns a Predicate that checks if the property value is less than the given value.
   *
   * @param target the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the less than comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> lt(V target) {
    return compare((propertyValue, v) -> propertyValue.compareTo(v) < 0, target);
  }

  /**
   * Returns a Predicate that comparing the property value and the given value.
   *
   * @param target the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the equality comparison
   * @throws IllegalArgumentException if the value or the property value is null,
   * <br /> or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> compare(BiPredicate<V, V> comparison, V target) {
    return t -> {
      if (target == null) {
        throw new IllegalArgumentException("Cannot compare property to null");
      }

      try {
        @SuppressWarnings("unchecked") V propertyValue = (V) propertyExtractor.apply(t);
        if (propertyValue == null) {
          return false;
        }
        return comparison.test(propertyValue, target);
      } catch (ClassCastException e) {
        throw new IllegalArgumentException(
            "Property type is not comparable or value cannot cast to the type of property for comparing");
      }
    };
  }

  /**
   * Returns a Predicate that checks if the property value is greater than the given value.
   *
   * @param target the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the greater than comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> gt(V target) {
    return compare((propertyValue, v) -> propertyValue.compareTo(v) > 0, target);
  }

  /**
   * Returns a Predicate that checks if the property value is greater than or equal to the given value.
   *
   * @param target the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the greater than or equal to comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> ge(V target) {
    return compare((propertyValue, v) -> propertyValue.compareTo(v) >= 0, target);
  }

  /**
   * Returns a Predicate that checks if the property value is less than or equal to the given value.
   *
   * @param target the value to compare the property value with
   * @param <V>   the type of the value, must implement the Comparable interface
   * @return a Predicate that performs the less than or equal to comparison
   * @throws IllegalArgumentException if the value or the property value is null, or if the property type is not comparable or the value cannot be cast to the property type
   */
  public <V extends Comparable<V>> Predicate<T> le(V target) {
    return compare((propertyValue, v) -> propertyValue.compareTo(v) <= 0, target);
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
