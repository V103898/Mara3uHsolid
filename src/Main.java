import java.util.*;
import java.util.Optional;

// Product.java
class Product {
    private final String id;
    private final String name;
    private final String manufacturer;
    private final double price;
    private final Category category;

    public Product(String id, String name, String manufacturer, double price, Category category) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.category = category;
    }

    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public String getManufacturer() { return manufacturer; }
    public double getPrice() { return price; }
    public Category getCategory() { return category; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// Category.java
enum Category {
    ELECTRONICS, CLOTHING, FOOD, BOOKS, OTHER
}

// OrderStatus.java
enum OrderStatus {
    NEW, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

// Order.java
class Order {
    private final String id;
    private final User user;
    private final List<Product> products;
    private OrderStatus status;

    public Order(String id, User user, List<Product> products) {
        this.id = id;
        this.user = user;
        this.products = new ArrayList<>(products);
        this.status = OrderStatus.NEW;
    }

    // Геттеры
    public String getId() { return id; }
    public User getUser() { return user; }
    public List<Product> getProducts() { return new ArrayList<>(products); }
    public OrderStatus getStatus() { return status; }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}

// User.java
class User {
    private final String id;
    private final String name;
    private final String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Геттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
// ProductRepository.java
//Интерфейс ProductRepository позволяет добавлять новые реализации без изменения существующего кода.
//OCP
// Система открыта для расширения (через новые реализации интерфейсов), но закрыта для модификации (не требует изменений существующего кода).
interface ProductRepository {
    List<Product> findAll();
    List<Product> findByCategory(Category category);
    List<Product> findByNameContaining(String name);
    Optional<Product> findById(String id);
}
     //Раздельные интерфейсы ProductRepository и OrderRepository вместо одного большого ShopRepository.
//ISP
      //Клиенты не должны зависеть от интерфейсов, которые они не используют. Мелкие специализированные интерфейсы лучше!
// OrderRepository.java
interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
    List<Order> findByUser(User user);
}
// InMemoryProductRepository.java
 class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> products = new HashMap<>();

    public InMemoryProductRepository() {
        // Инициализация тестовыми данными
        addSampleProducts();
    }

    private void addSampleProducts() {
        addProduct(new Product("1", "Laptop", "Dell", 999.99, Category.ELECTRONICS));
        addProduct(new Product("2", "T-Shirt", "Nike", 29.99, Category.CLOTHING));
        addProduct(new Product("3", "Apple", "Fresh Farms", 0.99, Category.FOOD));
    }

    private void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public List<Product> findByCategory(Category category) {
        List<Product> result = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getCategory() == category) {
                result.add(product);
            }
        }
        return result;
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        List<Product> result = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(product);
            }
        }
        return result;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }
}

//Все реализации ProductRepository (например, InMemoryProductRepository) могут быть взаимозаменяемы без изменения корректности программы.
//LSP
//Подтипы могут заменять базовые типы, не нарушая работу программы.
// InMemoryOrderRepository.java
class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findByUser(User user) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getUser().equals(user)) {
                result.add(order);
            }
        }
        return result;
    }
}
// SimpleRecommendationService.java
class SimpleRecommendationService implements RecommendationService {
    private final ProductRepository productRepository;

    public SimpleRecommendationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> recommendProducts(User user) {
        // Простая реализация - возвращаем все электронные товары
        return productRepository.findByCategory(Category.ELECTRONICS);
    }
}

// RecommendationService.java
interface RecommendationService {
    List<Product> recommendProducts(User user);
}

        public class Main {
            public static void main(String[] args) {
                ProductRepository productRepository = new InMemoryProductRepository();
                OrderRepository orderRepository = new InMemoryOrderRepository();

                User user = new User("1", "ВАСЯ", "vasek@ya.ru");

                // Создание нового заказа
                List<Product> productsToOrder = productRepository.findByCategory(Category.ELECTRONICS);
                Order order = new Order("1", user, productsToOrder);
                orderRepository.save(order);

                // Вывод информации о заказах пользователя
                List<Order> userOrders = orderRepository.findByUser(user);
                for (Order userOrder : userOrders) {
                    System.out.println("Order ID: " + userOrder.getId() + ", Status: " + userOrder.getStatus());
                }
            }
        }
