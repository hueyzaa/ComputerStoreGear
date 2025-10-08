package controller.servlet;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.BrandDAO;
import model.Product;
import model.Category;
import model.Brand;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "ProductServlet", urlPatterns = {"/products"})
public class ProductServlet extends HttpServlet {

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
        this.brandDAO = new BrandDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        String categoryIdParam = request.getParameter("categoryId");
        String brandIdParam = request.getParameter("brandId");
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        int page = 1;
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                System.err.println("Invalid page number format: " + pageParam);
            }
        }

        int size = 9; // Default page size
        if (sizeParam != null && !sizeParam.trim().isEmpty()) {
            try {
                size = Integer.parseInt(sizeParam);
            } catch (NumberFormatException e) {
                System.err.println("Invalid page size format: " + sizeParam);
            }
        }

        // Default sorting
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "ProductName";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "asc";
        }

        UUID categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            try {
                categoryId = UUID.fromString(categoryIdParam);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid category ID format: " + categoryIdParam);
            }
        }

        UUID brandId = null;
        if (brandIdParam != null && !brandIdParam.trim().isEmpty()) {
            try {
                brandId = UUID.fromString(brandIdParam);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid brand ID format: " + brandIdParam);
            }
        }

        List<Product> productList = productDAO.searchAndFilterProducts(searchTerm, categoryId, brandId, page, size, sortBy, sortOrder);
        int totalProducts = productDAO.getTotalProductCount(searchTerm, categoryId, brandId);
        List<Category> categories = null;
        try {
            categories = categoryDAO.getAllCategoriesForAdmin();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<Brand> brands = brandDAO.getAllBrands();

        int totalPages = (int) Math.ceil((double) totalProducts / size);

        request.setAttribute("productList", productList);
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("selectedSearchTerm", searchTerm != null ? searchTerm : "");
        request.setAttribute("selectedCategoryId", categoryId != null ? categoryId.toString() : "");
        request.setAttribute("selectedBrandId", brandId != null ? brandId.toString() : "");
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/products.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
