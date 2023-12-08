package ltd.mingcloud.specification;

import org.junit.jupiter.api.Test;

/**
 * @author wynn5a
 */
public class TestSpecification {
  //boolean matched = Selector.of(new User("Alice", 32)).validate(Criteria.of(User::name).eq("name"));
  //CriteriaBuilder.and(Criteria.of(User::age).lt(32), Criteria.of(User::age).gt(20))
  //List.stream().filter(Criteria.of(User::name).eq("name"))
  //todo can create criteria for the property
  //todo criteria should support eq,lt,gt,le,ge operator
  //todo criteria can be composited using and, or, not
  //todo selector use criteria


  @Test
  public void should() {

  }

}

record User(String name, int age) {

}