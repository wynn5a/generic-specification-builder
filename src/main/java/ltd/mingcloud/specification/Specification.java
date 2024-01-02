package ltd.mingcloud.specification;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author wynn5a
 */
public class Specification<T> {

  private final Predicate<T> predicate;

  public Specification(Predicate<T> predicate) {
    this.predicate = predicate;
  }

  /**
   * Returns a Predicate that performs a logical AND of the given predicates.
   *
   * @param predicates the predicates to combine
   * @param <T> the type of the input to the predicates
   * @return a Predicate that performs a logical AND of the given predicates
   */
  @SafeVarargs
  public static <T> Predicate<T> and(Predicate<T>... predicates) {
    checkPredicates(predicates);
    return Stream.of(predicates).reduce(Predicate::and).orElse(t -> false);
  }

  private static <T> void checkPredicates(Predicate<T>[] predicates) {
    if (Stream.of(predicates).anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException("Predicate should not be null");
    }
  }

  /**
   * Returns a Predicate that performs a logical OR of the given predicates.
   *
   * @param predicates the predicates to combine
   * @param <T> the type of the input to the predicates
   * @return a Predicate that performs a logical OR of the given predicates
   */
  @SafeVarargs
  public static <T> Predicate<T> or(Predicate<T>... predicates) {
    checkPredicates(predicates);
    return Stream.of(predicates).reduce(Predicate::or).orElse(t -> true);
  }

  /**
   * Returns a Predicate that performs a logical negation of the given predicate.
   *
   * @param predicate the predicate to negate
   * @param <T> the type of the input to the predicate
   * @return a Predicate that performs a logical negation of the given predicate
   */
  public static <T> Predicate<T> not(Predicate<T> predicate) {
    if (predicate == null) {
      throw new IllegalArgumentException("Predicate should not be null");
    }
    return predicate.negate();
  }

  public static <T> Specification<T> of(Predicate<T> predicate) {
    return new Specification<>(predicate);
  }

  public boolean match(T value) {
    return predicate.test(value);
  }
}
