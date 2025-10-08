package dao;

import model.Product;
import model.Category;
import model.Brand;
import model.ProductImage;
import util.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDAO extends DBContext {

    private final ProductImageDAO productImageDAO = new ProductImageDAO(new util.FileService()); // Khởi tạo FileService mặc định

    /**
     * Lấy tất cả sản phẩm từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng Product.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.SKU, p.CategoryID, p.BrandID, p.Description, p.ShortDescription, " +
                     "p.Price, p.ComparePrice, p.CostPrice, p.Weight, p.Dimensions, p.StockQuantity, p.MinStockLevel, p.MaxStockLevel, " +
                     "p.IsActive, p.IsFeatured, p.ViewCount, p.SalesCount, p.AverageRating, p.ReviewCount, p.CreatedDate, p.ModifiedDate, " +
                     "c.CategoryName, b.BrandName " +
                     "FROM Products p " +
                     "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                     "LEFT JOIN Brands b ON p.BrandID = b.BrandID " +
                     "WHERE p.IsActive = 1"; // Chỉ lấy sản phẩm đang hoạt động
        
        try (Connection conn = getConnection(); // Lấy kết nối mới từ DBContext
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductID(UUID.fromString(rs.getString("ProductID")));
                product.setProductName(rs.getString("ProductName"));
                product.setSku(rs.getString("SKU"));
                
                String categoryIdStr = rs.getString("CategoryID");
                if (categoryIdStr != null) {
                    product.setCategoryID(UUID.fromString(categoryIdStr));
                    Category category = new Category();
                    category.setCategoryID(UUID.fromString(categoryIdStr));
                    category.setCategoryName(rs.getString("CategoryName"));
                    product.setCategory(category);
                }

                String brandIdStr = rs.getString("BrandID");
                if (brandIdStr != null) {
                    product.setBrandID(UUID.fromString(brandIdStr));
                    Brand brand = new Brand();
                    brand.setBrandID(UUID.fromString(brandIdStr));
                    brand.setBrandName(rs.getString("BrandName"));
                    product.setBrand(brand);
                }
                
                product.setDescription(rs.getString("Description"));
                product.setShortDescription(rs.getString("ShortDescription"));
                product.setPrice(rs.getBigDecimal("Price"));
                product.setComparePrice(rs.getBigDecimal("ComparePrice"));
                product.setCostPrice(rs.getBigDecimal("CostPrice"));
                product.setWeight(rs.getBigDecimal("Weight"));
                product.setDimensions(rs.getString("Dimensions"));
                product.setStockQuantity(rs.getInt("StockQuantity"));
                product.setMinStockLevel(rs.getInt("MinStockLevel"));
                product.setMaxStockLevel(rs.getInt("MaxStockLevel"));
                product.setActive(rs.getBoolean("IsActive"));
                product.setFeatured(rs.getBoolean("IsFeatured"));
                product.setViewCount(rs.getInt("ViewCount"));
                product.setSalesCount(rs.getInt("SalesCount"));
                product.setAverageRating(rs.getBigDecimal("AverageRating"));
                product.setReviewCount(rs.getInt("ReviewCount"));
                
                product.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                
                if (rs.getTimestamp("ModifiedDate") != null) {
                    product.setModifiedDate(rs.getTimestamp("ModifiedDate").toLocalDateTime());
                }

                // Lấy hình ảnh chính cho sản phẩm
                try {
                    ProductImage mainImage = productImageDAO.getMainImageByProductId(product.getProductID());
                    if (mainImage != null) {
                        List<ProductImage> images = new ArrayList<>();
                        images.add(mainImage);
                        product.setImages(images);
                    }
                } catch (SQLException e) {
                    System.err.println("Error fetching main image for product " + product.getProductID() + ": " + e.getMessage());
                }

                products.add(product);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching all products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Lấy tất cả sản phẩm từ cơ sở dữ liệu, bao gồm cả sản phẩm không hoạt động (dành cho Admin).
     * @return Danh sách các đối tượng Product.
     */
    public List<Product> getAllProductsForAdmin() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.SKU, p.CategoryID, p.BrandID, p.Description, p.ShortDescription, " +
                     "p.Price, p.ComparePrice, p.CostPrice, p.Weight, p.Dimensions, p.StockQuantity, p.MinStockLevel, p.MaxStockLevel, " +
                     "p.IsActive, p.IsFeatured, p.ViewCount, p.SalesCount, p.AverageRating, p.ReviewCount, p.CreatedDate, p.ModifiedDate, " +
                     "c.CategoryName, b.BrandName " +
                     "FROM Products p " +
                     "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                     "LEFT JOIN Brands b ON p.BrandID = b.BrandID";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductID(UUID.fromString(rs.getString("ProductID")));
                product.setProductName(rs.getString("ProductName"));
                product.setSku(rs.getString("SKU"));
                
                String categoryIdStr = rs.getString("CategoryID");
                if (categoryIdStr != null) {
                    product.setCategoryID(UUID.fromString(categoryIdStr));
                    Category category = new Category();
                    category.setCategoryID(UUID.fromString(categoryIdStr));
                    category.setCategoryName(rs.getString("CategoryName"));
                    product.setCategory(category);
                }

                String brandIdStr = rs.getString("BrandID");
                if (brandIdStr != null) {
                    product.setBrandID(UUID.fromString(brandIdStr));
                    Brand brand = new Brand();
                    brand.setBrandID(UUID.fromString(brandIdStr));
                    brand.setBrandName(rs.getString("BrandName"));
                    product.setBrand(brand);
                }
                
                product.setDescription(rs.getString("Description"));
                product.setShortDescription(rs.getString("ShortDescription"));
                product.setPrice(rs.getBigDecimal("Price"));
                product.setComparePrice(rs.getBigDecimal("ComparePrice"));
                product.setCostPrice(rs.getBigDecimal("CostPrice"));
                product.setWeight(rs.getBigDecimal("Weight"));
                product.setDimensions(rs.getString("Dimensions"));
                product.setStockQuantity(rs.getInt("StockQuantity"));
                product.setMinStockLevel(rs.getInt("MinStockLevel"));
                product.setMaxStockLevel(rs.getInt("MaxStockLevel"));
                product.setActive(rs.getBoolean("IsActive"));
                product.setFeatured(rs.getBoolean("IsFeatured"));
                product.setViewCount(rs.getInt("ViewCount"));
                product.setSalesCount(rs.getInt("SalesCount"));
                product.setAverageRating(rs.getBigDecimal("AverageRating"));
                product.setReviewCount(rs.getInt("ReviewCount"));
                
                product.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                
                if (rs.getTimestamp("ModifiedDate") != null) {
                    product.setModifiedDate(rs.getTimestamp("ModifiedDate").toLocalDateTime());
                }

                // Lấy hình ảnh chính cho sản phẩm
                try {
                    ProductImage mainImage = productImageDAO.getMainImageByProductId(product.getProductID());
                    if (mainImage != null) {
                        List<ProductImage> images = new ArrayList<>();
                        images.add(mainImage);
                        product.setImages(images);
                    }
                } catch (SQLException e) {
                    System.err.println("Error fetching main image for product (admin view) " + product.getProductID() + ": " + e.getMessage());
                }

                products.add(product);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching all products for admin: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(UUID productId) {
        Product product = null;
        String sql = "SELECT p.ProductID, p.ProductName, p.SKU, p.CategoryID, p.BrandID, p.Description, p.ShortDescription, " +
                     "p.Price, p.ComparePrice, p.CostPrice, p.Weight, p.Dimensions, p.StockQuantity, p.MinStockLevel, p.MaxStockLevel, " +
                     "p.IsActive, p.IsFeatured, p.ViewCount, p.SalesCount, p.AverageRating, p.ReviewCount, p.CreatedDate, p.ModifiedDate, " +
                     "c.CategoryName, b.BrandName " +
                     "FROM Products p " +
                     "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                     "LEFT JOIN Brands b ON p.BrandID = b.BrandID " +
                     "WHERE p.ProductID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setProductID(UUID.fromString(rs.getString("ProductID")));
                    product.setProductName(rs.getString("ProductName"));
                    product.setSku(rs.getString("SKU"));
                    
                    String categoryIdStr = rs.getString("CategoryID");
                    if (categoryIdStr != null) {
                        product.setCategoryID(UUID.fromString(categoryIdStr));
                        Category category = new Category();
                        category.setCategoryID(UUID.fromString(categoryIdStr));
                        category.setCategoryName(rs.getString("CategoryName"));
                        product.setCategory(category);
                    }

                    String brandIdStr = rs.getString("BrandID");
                    if (brandIdStr != null) {
                        product.setBrandID(UUID.fromString(brandIdStr));
                        Brand brand = new Brand();
                        brand.setBrandID(UUID.fromString(brandIdStr));
                        brand.setBrandName(rs.getString("BrandName"));
                        product.setBrand(brand);
                    }
                    
                    product.setDescription(rs.getString("Description"));
                    product.setShortDescription(rs.getString("ShortDescription"));
                    product.setPrice(rs.getBigDecimal("Price"));
                    product.setComparePrice(rs.getBigDecimal("ComparePrice"));
                    product.setCostPrice(rs.getBigDecimal("CostPrice"));
                    product.setWeight(rs.getBigDecimal("Weight"));
                    product.setDimensions(rs.getString("Dimensions"));
                    product.setStockQuantity(rs.getInt("StockQuantity"));
                    product.setMinStockLevel(rs.getInt("MinStockLevel"));
                    product.setMaxStockLevel(rs.getInt("MaxStockLevel"));
                    product.setActive(rs.getBoolean("IsActive"));
                    product.setFeatured(rs.getBoolean("IsFeatured"));
                    product.setViewCount(rs.getInt("ViewCount"));
                    product.setSalesCount(rs.getInt("SalesCount"));
                    product.setAverageRating(rs.getBigDecimal("AverageRating"));
                    product.setReviewCount(rs.getInt("ReviewCount"));
                    
                    product.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    
                    if (rs.getTimestamp("ModifiedDate") != null) {
                        product.setModifiedDate(rs.getTimestamp("ModifiedDate").toLocalDateTime());
                    }

                    // Lấy tất cả hình ảnh cho sản phẩm
                    try {
                        product.setImages(productImageDAO.getImagesByProductId(product.getProductID()));
                    } catch (SQLException e) {
                        System.err.println("Error fetching images for product " + product.getProductID() + ": " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by ID: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return product;
    }

    /**
     * Thêm một sản phẩm mới vào cơ sở dữ liệu.
     * @param product Đối tượng Product cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO Products (ProductID, ProductName, SKU, CategoryID, BrandID, Description, ShortDescription, " +
                     "Price, ComparePrice, CostPrice, Weight, Dimensions, StockQuantity, MinStockLevel, MaxStockLevel, " +
                     "IsActive, IsFeatured, ViewCount, SalesCount, AverageRating, ReviewCount, CreatedDate, ModifiedDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            UUID newId = UUID.randomUUID();
            product.setProductID(newId); // Gán ID cho đối tượng trước khi thêm

            ps.setString(1, newId.toString());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getSku());
            ps.setString(4, product.getCategoryID() != null ? product.getCategoryID().toString() : null);
            ps.setString(5, product.getBrandID() != null ? product.getBrandID().toString() : null);
            ps.setString(6, product.getDescription());
            ps.setString(7, product.getShortDescription());
            ps.setBigDecimal(8, product.getPrice());
            ps.setBigDecimal(9, product.getComparePrice());
            ps.setBigDecimal(10, product.getCostPrice());
            ps.setBigDecimal(11, product.getWeight());
            ps.setString(12, product.getDimensions());
            ps.setInt(13, product.getStockQuantity());
            ps.setInt(14, product.getMinStockLevel());
            ps.setInt(15, product.getMaxStockLevel());
            ps.setBoolean(16, product.isActive());
            ps.setBoolean(17, product.isFeatured());
            ps.setInt(18, product.getViewCount());
            ps.setInt(19, product.getSalesCount());
            ps.setBigDecimal(20, product.getAverageRating());
            ps.setInt(21, product.getReviewCount());
            ps.setTimestamp(22, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(23, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Cập nhật thông tin một sản phẩm hiện có.
     * @param product Đối tượng Product cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE Products SET ProductName = ?, SKU = ?, CategoryID = ?, BrandID = ?, Description = ?, ShortDescription = ?, " +
                     "Price = ?, ComparePrice = ?, CostPrice = ?, Weight = ?, Dimensions = ?, StockQuantity = ?, MinStockLevel = ?, MaxStockLevel = ?, " +
                     "IsActive = ?, IsFeatured = ?, ViewCount = ?, SalesCount = ?, AverageRating = ?, ReviewCount = ?, ModifiedDate = ? " +
                     "WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getSku());
            ps.setString(3, product.getCategoryID() != null ? product.getCategoryID().toString() : null);
            ps.setString(4, product.getBrandID() != null ? product.getBrandID().toString() : null);
            ps.setString(5, product.getDescription());
            ps.setString(6, product.getShortDescription());
            ps.setBigDecimal(7, product.getPrice());
            ps.setBigDecimal(8, product.getComparePrice());
            ps.setBigDecimal(9, product.getCostPrice());
            ps.setBigDecimal(10, product.getWeight());
            ps.setString(11, product.getDimensions());
            ps.setInt(12, product.getStockQuantity());
            ps.setInt(13, product.getMinStockLevel());
            ps.setInt(14, product.getMaxStockLevel());
            ps.setBoolean(15, product.isActive());
            ps.setBoolean(16, product.isFeatured());
            ps.setInt(17, product.getViewCount());
            ps.setInt(18, product.getSalesCount());
            ps.setBigDecimal(19, product.getAverageRating());
            ps.setInt(20, product.getReviewCount());
            ps.setTimestamp(21, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(22, product.getProductID().toString());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Xóa mềm một sản phẩm (đặt IsActive = false).
     * @param productId ID của sản phẩm cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteProduct(UUID productId) {
        String sql = "UPDATE Products SET IsActive = 0, ModifiedDate = ? WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, productId.toString());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Lấy tổng số sản phẩm.
     * @return Tổng số sản phẩm.
     */
    public int getTotalProductCount() {
        String sql = "SELECT COUNT(*) FROM Products WHERE IsActive = 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error getting total product count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy tổng số sản phẩm phù hợp với các tiêu chí tìm kiếm/lọc.
     * @param searchTerm Từ khóa tìm kiếm (tên sản phẩm, mô tả, SKU).
     * @param categoryId ID của danh mục để lọc.
     * @param brandId ID của thương hiệu để lọc.
     * @return Tổng số sản phẩm phù hợp.
     */
    public int getTotalProductCount(String searchTerm, UUID categoryId, UUID brandId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products p WHERE p.IsActive = 1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (p.ProductName LIKE ? OR p.Description LIKE ? OR p.SKU LIKE ?)");
            String likeTerm = "%" + searchTerm.trim() + "%";
            params.add(likeTerm);
            params.add(likeTerm);
            params.add(likeTerm);
        }

        if (categoryId != null) {
            sql.append(" AND p.CategoryID = ?");
            params.add(categoryId.toString());
        }

        if (brandId != null) {
            sql.append(" AND p.BrandID = ?");
            params.add(brandId.toString());
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting total product count: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Tìm kiếm và lọc sản phẩm dựa trên các tiêu chí.
     * @param searchTerm Từ khóa tìm kiếm (tên sản phẩm, mô tả, SKU).
     * @param categoryId ID của danh mục để lọc.
     * @param brandId ID của thương hiệu để lọc.
     * @param page Số trang hiện tại.
     * @param size Số lượng sản phẩm trên mỗi trang.
     * @param sortBy Tiêu chí sắp xếp (ví dụ: ProductName, Price, CreatedDate).
     * @param sortOrder Thứ tự sắp xếp (asc hoặc desc).
     * @return Danh sách các đối tượng Product phù hợp.
     */
    public List<Product> searchAndFilterProducts(String searchTerm, UUID categoryId, UUID brandId, int page, int size, String sortBy, String sortOrder) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.ProductID, p.ProductName, p.SKU, p.CategoryID, p.BrandID, p.Description, p.ShortDescription, " +
                                              "p.Price, p.ComparePrice, p.CostPrice, p.Weight, p.Dimensions, p.StockQuantity, p.MinStockLevel, p.MaxStockLevel, " +
                                              "p.IsActive, p.IsFeatured, p.ViewCount, p.SalesCount, p.AverageRating, p.ReviewCount, p.CreatedDate, p.ModifiedDate, " +
                                              "c.CategoryName, b.BrandName " +
                                              "FROM Products p " +
                                              "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                                              "LEFT JOIN Brands b ON p.BrandID = b.BrandID " +
                                              "WHERE p.IsActive = 1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (p.ProductName LIKE ? OR p.Description LIKE ? OR p.SKU LIKE ?)");
            String likeTerm = "%" + searchTerm.trim() + "%";
            params.add(likeTerm);
            params.add(likeTerm);
            params.add(likeTerm);
        }

        if (categoryId != null) {
            sql.append(" AND p.CategoryID = ?");
            params.add(categoryId.toString());
        }

        if (brandId != null) {
            sql.append(" AND p.BrandID = ?");
            params.add(brandId.toString());
        }

        // Sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            sql.append(" ORDER BY ").append(sortBy);
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                sql.append(" DESC");
            } else {
                sql.append(" ASC");
            }
        } else {
            sql.append(" ORDER BY p.ProductName ASC"); // Default sort
        }

        // Pagination
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * size);
        params.add(size);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    ps.setString(paramIndex++, (String) param);
                } else if (param instanceof UUID) {
                    ps.setString(paramIndex++, ((UUID) param).toString());
                } else if (param instanceof Integer) {
                    ps.setInt(paramIndex++, (Integer) param);
                } else {
                    ps.setObject(paramIndex++, param);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductID(UUID.fromString(rs.getString("ProductID")));
                    product.setProductName(rs.getString("ProductName"));
                    product.setSku(rs.getString("SKU"));
                    
                    String catIdStr = rs.getString("CategoryID");
                    if (catIdStr != null) {
                        product.setCategoryID(UUID.fromString(catIdStr));
                        Category category = new Category();
                        category.setCategoryID(UUID.fromString(catIdStr));
                        category.setCategoryName(rs.getString("CategoryName"));
                        product.setCategory(category);
                    }

                    String brIdStr = rs.getString("BrandID");
                    if (brIdStr != null) {
                        product.setBrandID(UUID.fromString(brIdStr));
                        Brand brand = new Brand();
                        brand.setBrandID(UUID.fromString(brIdStr));
                        brand.setBrandName(rs.getString("BrandName"));
                        product.setBrand(brand);
                    }
                    
                    product.setDescription(rs.getString("Description"));
                    product.setShortDescription(rs.getString("ShortDescription"));
                    product.setPrice(rs.getBigDecimal("Price"));
                    product.setComparePrice(rs.getBigDecimal("ComparePrice"));
                    product.setCostPrice(rs.getBigDecimal("CostPrice"));
                    product.setWeight(rs.getBigDecimal("Weight"));
                    product.setDimensions(rs.getString("Dimensions"));
                    product.setStockQuantity(rs.getInt("StockQuantity"));
                    product.setMinStockLevel(rs.getInt("MinStockLevel"));
                    product.setMaxStockLevel(rs.getInt("MaxStockLevel"));
                    product.setActive(rs.getBoolean("IsActive"));
                    product.setFeatured(rs.getBoolean("IsFeatured"));
                    product.setViewCount(rs.getInt("ViewCount"));
                    product.setSalesCount(rs.getInt("SalesCount"));
                    product.setAverageRating(rs.getBigDecimal("AverageRating"));
                    product.setReviewCount(rs.getInt("ReviewCount"));
                    
                    product.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    
                    if (rs.getTimestamp("ModifiedDate") != null) {
                        product.setModifiedDate(rs.getTimestamp("ModifiedDate").toLocalDateTime());
                    }

                    try {
                        ProductImage mainImage = productImageDAO.getMainImageByProductId(product.getProductID());
                        if (mainImage != null) {
                            List<ProductImage> images = new ArrayList<>();
                            images.add(mainImage);
                            product.setImages(images);
                        }
                    } catch (SQLException e) {
                        System.err.println("Error fetching main image for product " + product.getProductID() + ": " + e.getMessage());
                    }

                    products.add(product);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error searching and filtering products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }
}