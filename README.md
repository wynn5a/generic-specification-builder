# generic-specification-builder

## Reimplement specification pattern in Java

Simple code and easy to use

## Features

- [x] Support `and`, `or`, `not` combinators
- [x] Support `eq`, `lt`, `le`, `gt`, `ge` operations

## Usage

```
Predicate<Product> productPredicate = Specification.and(Criteria.of(Product::name).eq("iPhone 15"),
        Criteria.of(Product::owner).eq(ALICE),
        Specification.or(Criteria.of(Product::quality).le(10),
            Criteria.of(Product::quantity).ge(20)),
        Specification.not(Criteria.of(Product::type).eq("Book")));
        
Stream.of(products).filter(productPredicate).count();

```