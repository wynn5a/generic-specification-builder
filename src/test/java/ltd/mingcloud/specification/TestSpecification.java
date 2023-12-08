package ltd.mingcloud.specification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

/**
 * @author wynn5a
 */
public class TestSpecification {

  public static final User ALICE = new User("Alice", 43);

  //boolean matched = Selector.of(new User("Alice", 32)).match(Criteria.of(User::name).eq("name"));
  //CriteriaBuilder.and(Criteria.of(User::age).lt(32), Criteria.of(User::age).gt(20))
  //List.stream().filter(Criteria.of(User::name).eq("name"))

  @Test
  public void should_create_criteria_using_of() {
    Criteria<User, String> c = Criteria.of(User::name);
    assertNotNull(c);
  }

  @Test
  public void should_throw_exception_create_criteria_using_of_null() {
    assertThrows(IllegalArgumentException.class, () -> Criteria.of(null));
  }

  //todo criteria should support eq,lt,gt,le,ge operator
  @Test
  public void criteria_should_support_eq_operator() {
    Predicate<User> predicate = Criteria.of(User::name).eq("Alice");
    assertTrue(predicate.test(ALICE));
  }

  @Test
  public void criteria_eq_operator_support_object() {
    Product prod = new Product(ALICE);
    Predicate<Product> p = Criteria.of(Product::owner).eq(ALICE);
    assertTrue(p.test(prod));
    Predicate<Product> p2 = Criteria.of(Product::owner).eq(new User("Bob", 23));
    assertFalse(p2.test(prod));
  }

  @Test
  public void criteria_should_support_operator_lt() {
    Predicate<User> p = Criteria.of(User::age).lt(50);
    assertTrue(p.test(ALICE));
  }

  @Test
  public void lt_should_throw_exception_when_property_cannot_compare_to_value() {
    Predicate<User> p2 = Criteria.of(User::name).lt(34);
    assertThrows(IllegalArgumentException.class, () -> p2.test(ALICE));
  }

  @Test
  public void lt_should_return_false_exception_when_property_value_is_null() {
    Predicate<User> p2 = Criteria.of(User::name).lt("null");
    assertFalse(p2.test(new User(null, 34)));
  }

  @Test
  public void lt_should_throw_exception_when_property_value_is_not_comparable() {
    Predicate<Product> p2 = Criteria.of(Product::owner).lt("null");
    assertThrows(IllegalArgumentException.class, () -> p2.test(new Product(ALICE)));
  }

  @Test
  public void lt_should_throw_exception_when_value_is_null() {
    Predicate<User> p2 = Criteria.of(User::name).lt(null);
    assertThrows(IllegalArgumentException.class, () -> p2.test(ALICE));
  }

  @Test
  public void criteria_should_support_operator_gt() {
    Predicate<User> p = Criteria.of(User::age).gt(20);
    assertTrue(p.test(ALICE));
  }

  @Test
  public void criteria_should_support_operator_ge() {
    Predicate<User> p = Criteria.of(User::age).ge(ALICE.age());
    assertTrue(p.test(ALICE));
  }

  @Test
  public void criteria_should_support_operator_le() {
    Predicate<User> p = Criteria.of(User::age).le(ALICE.age());
    assertTrue(p.test(ALICE));
  }
  //todo criteria can be composited using and, or, not
  //todo specification use criteria to match object
  //todo specification can be used in stream

}

record User(String name, int age) {

}

record Product(User owner) {

}

