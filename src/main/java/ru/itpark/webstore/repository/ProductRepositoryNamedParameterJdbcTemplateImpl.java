package ru.itpark.webstore.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.itpark.webstore.domain.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepositoryNamedParameterJdbcTemplateImpl implements ProductRepository {
    private final NamedParameterJdbcTemplate template;

    public ProductRepositoryNamedParameterJdbcTemplateImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<Product> getAll() {
        return template.query(
                "SELECT id, name, price FROM products",
                (rs, i) -> {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    return new Product(id, name, price);
                }
        );
    }

    @Override
    public Optional<Product> getById(int id) {
        return template.query(
                "SELECT id, name, price FROM products WHERE id = :id LIMIT 1",
                Map.of("id", id),
                (rs, i) -> new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price")
                )
        ).stream().findFirst();
    }

    @Override
    public void save(Product item) {
        if (item.getId() == 0) {
            // INSERT
            template.update(
                    "INSERT INTO products(name, price) VALUES (:name, :price)",
                    Map.of(
                            "name", item.getName(),
                            "price", item.getPrice()
                    )
            );
            return;
        }

        // UPDATE
        template.update(
                "UPDATE products SET name = :name, price = :price WHERE id = :id",
                Map.of(
                        "id", item.getId(),
                        "name", item.getName(),
                        "price", item.getPrice()
                )
        );
    }

    @Override
    public void removeById(int id) {
        template.update(
                "DELETE FROM products WHERE id = :id",
                Map.of("id", id)
        );
    }
}
