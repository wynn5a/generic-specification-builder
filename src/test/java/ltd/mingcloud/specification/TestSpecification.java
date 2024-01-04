package ltd.mingcloud.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * @author wynn5a
 */
public class TestSpecification {

  static final User ALICE = new User("Alice", 43);

  //boolean matched = Selector.of(new User("Alice", 32)).match(Criteria.of(User::name).eq("name"));
  //CriteriaBuilder.and(Criteria.of(User::age).lt(32), Criteria.of(User::age).gt(20))
  //List.stream().filter(Criteria.of(User::name).eq("name"))

  @Test
  public void should_create_criteria_using_of() {
    Criteria<User, String> c = Criteria.of(User::name);
    assertNotNull(c);
    assertEquals("Alice", c.get(ALICE));
  }

  @Test
  public void should_throw_exception_create_criteria_using_of_null() {
    assertThrows(IllegalArgumentException.class, () -> Criteria.of(null));
  }

  @Test
  public void criteria_should_support_eq_operator() {
    assertTrue(Criteria.of(User::name).eq("Alice").test(ALICE));
    assertTrue(Criteria.of(User::age).eq(43).test(ALICE));
    assertFalse(Criteria.of(User::name).eq("Bob").test(ALICE));
    assertFalse(Criteria.of(User::age).eq(42).test(ALICE));
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
  public void should_return_false_when_property_value_is_null() {
    Predicate<User> p = Criteria.of(User::name).eq("null");
    assertFalse(p.test(new User(null, 34)));
  }

  @Test
  public void criteria_should_support_operator_lt() {
    Predicate<User> p = Criteria.of(User::age).lt(50);
    assertTrue(p.test(ALICE));
    assertFalse(p.test(new User("Bob", 50)));
    assertFalse(p.test(new User("Bob", 51)));
  }

  @Test
  public void lt_should_throw_exception_when_property_cannot_compare_to_value() {
    Predicate<User> p2 = Criteria.of(User::name).lt(34);
    assertThrows(IllegalArgumentException.class, () -> p2.test(ALICE));
  }

  @Test
  public void lt_should_return_false_when_property_value_is_null() {
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
    Predicate<User> p = Criteria.of(User::age).ge(43);
    assertTrue(p.test(ALICE));
    assertTrue(p.test(new User("Bob", 45)));
    assertFalse(p.test(new User("Bob", 42)));
  }

  @Test
  public void criteria_should_support_operator_le() {
    Predicate<User> p = Criteria.of(User::age).le(43);
    assertTrue(p.test(ALICE));
    assertTrue(p.test(new User("Bob", 42)));
    assertFalse(p.test(new User("Bob", 45)));
  }

  //todo criteria can be composited using and, or, not
  @Test
  public void criteria_should_be_composited_by_and() {
    Predicate<User> p = Specification.and(Criteria.of(User::age).eq(18), Criteria.of(User::name).eq("Alice"));
    assertTrue(p.test(new User("Alice", 18)));
    assertFalse(p.test(new User("Jim", 18)));

    Predicate<Product> productPredicate = Specification.and(Criteria.of(Product::name).eq("iPhone 15"),
        Criteria.of(Product::owner).eq(ALICE), Criteria.of(Product::quantity).le(10));
    assertTrue(productPredicate.test(new Product(ALICE, "iPhone 15", 2, "Mobile Phone")));
    assertFalse(productPredicate.test(new Product(ALICE, "iPhone 15", 12, "Mobile Phone")));
    assertFalse(productPredicate.test(new Product(ALICE, "iPhone 14", 2, "Mobile Phone")));
    assertFalse(productPredicate.test(new Product(new User("Bob", 43), "iPhone 14", 8, "Mobile Phone")));
  }

  @Test
  public void criteria_using_and_should_handle_null() {
    assertThrows(IllegalArgumentException.class, () -> Specification.and(Criteria.of(User::age).eq(18), null));
  }

  @Test
  public void criteria_should_be_composited_by_or() {
    Predicate<User> p = Specification.or(Criteria.of(User::age).eq(18), Criteria.of(User::name).eq("Alice"));
    assertTrue(p.test(new User("Alice", 18)));
    assertTrue(p.test(new User("Jim", 18)));
    assertFalse(p.test(new User("Jim", 19)));

    Predicate<Product> productPredicate = Specification.or(Criteria.of(Product::name).eq("iPhone 15"),
        Criteria.of(Product::owner).eq(ALICE), Criteria.of(Product::quantity).le(10));
    assertTrue(productPredicate.test(new Product(ALICE, "iPhone 15", 2, "Mobile Phone")));
    assertTrue(productPredicate.test(new Product(ALICE, "iPhone 15", 12, "Mobile Phone")));
    assertTrue(productPredicate.test(new Product(ALICE, "iPhone 14", 2, "Mobile Phone")));
    assertTrue(productPredicate.test(new Product(new User("Bob", 43), "iPhone 14", 8, "Mobile Phone")));
    assertFalse(productPredicate.test(new Product(new User("Bob", 43), "iPhone 14", 12, "Mobile Phone")));
  }

  @Test
  public void criteria_using_or_should_handle_null() {
    assertThrows(IllegalArgumentException.class, () -> Specification.or(Criteria.of(User::age).eq(18), null));
  }

  @Test
  public void criteria_should_be_composited_by_not() {
    Predicate<User> p = Specification.not(Criteria.of(User::age).eq(18));
    assertFalse(p.test(new User("Alice", 18)));
    assertTrue(p.test(new User("Jim", 19)));
  }

  @Test
  public void criteria_using_not_should_handle_null() {
    assertThrows(IllegalArgumentException.class, () -> Specification.not(null));
  }

  @Test
  public void criteria_should_be_composited_by_and_or_not() {
    Predicate<User> p = Specification.and(Criteria.of(User::age).eq(18),
        Specification.or(Criteria.of(User::name).eq("Alice"), Criteria.of(User::name).eq("Bob")));
    assertTrue(p.test(new User("Alice", 18)));
    assertTrue(p.test(new User("Bob", 18)));
    assertFalse(p.test(new User("Jim", 18)));

    Predicate<Product> productPredicate = Specification.and(Criteria.of(Product::name).eq("iPhone 15"),
        Criteria.of(Product::owner).eq(ALICE),
        Specification.or(Criteria.of(Product::quantity).le(10),
            Criteria.of(Product::quantity).ge(20)),
        Specification.not(Criteria.of(Product::type).eq("Book")));
    assertTrue(productPredicate.test(new Product(ALICE, "iPhone 15", 2, "Mobile Phone")));
    assertFalse(productPredicate.test(new Product(ALICE, "iPhone 15", 12, "Mobile Phone")));
    assertTrue(productPredicate.test(new Product(ALICE, "iPhone 15", 24, "Mobile Phone")));
    assertFalse(productPredicate.test(new Product(ALICE, "iPhone 15", 24, "Book")));
  }

  @Test
  public void specification_should_match_object() {
    Specification<User> spec = Specification.of(Criteria.of(User::age).eq(18));
    assertTrue(spec.match(new User("Alice", 18)));
    assertFalse(spec.match(new User("Jim", 19)));
  }

  @Test
  public void specification_should_match_object_using_and() {
    Specification<User> spec = Specification.of(
        Specification.and(Criteria.of(User::age).le(18), Criteria.of(User::name).eq("Alice")));
    assertTrue(spec.match(new User("Alice", 18)));
    assertFalse(spec.match(new User("Alice", 19)));
    assertFalse(spec.match(new User("Jim", 19)));
  }

  @Test
  public void specification_should_match_object_using_or() {
    Specification<User> spec = Specification.of(
        Specification.or(Criteria.of(User::age).le(18), Criteria.of(User::name).eq("Alice")));
    assertTrue(spec.match(new User("Tim", 18)));
    assertTrue(spec.match(new User("Alice", 19)));
    assertFalse(spec.match(new User("Jim", 19)));
  }

  @Test
  public void specification_should_match_object_using_not() {
    Specification<User> spec = Specification.of(Specification.not(Criteria.of(User::age).le(18)));
    assertFalse(spec.match(new User("Tim", 18)));
    assertTrue(spec.match(new User("Alice", 19)));
    assertTrue(spec.match(new User("Jim", 19)));
  }

  @Test
  public void specification_should_match_object_using_and_or_not() {
    Specification<User> spec = Specification.of(
        Specification.and(
            Criteria.of(User::age).le(18),
            Specification.or(
                Criteria.of(User::name).eq("Alice"),
                Criteria.of(User::name).eq("Bob"))));
    assertTrue(spec.match(new User("Alice", 18)));
    assertTrue(spec.match(new User("Bob", 18)));
    assertFalse(spec.match(new User("Jim", 18)));
  }

  @Test
  public void specification_should_be_used_in_stream() {
    Predicate<User> predicate = Specification.and(
        Criteria.of(User::age).le(18),
        Specification.or(
            Criteria.of(User::name).eq("Alice"),
            Criteria.of(User::name).eq("Bob")));
    assertEquals(2, Stream.of(new User("Alice", 18),
            new User("Bob", 18),
            new User("Jim", 18))
        .filter(predicate).count());
  }
}

record User(String name, int age) {

}

record Product(User owner, String name, int quantity, String type) {

  public Product(User owner) {
    this(owner, null, 0, null);
  }
}

