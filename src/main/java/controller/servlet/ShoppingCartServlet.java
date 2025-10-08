package controller.servlet;

import dao.ProductDAO;
import dao.ShoppingCartDAO;
import model.Product;
import model.ShoppingCart;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/clear"})
public class ShoppingCartServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ShoppingCartServlet.class.getName());
    private ShoppingCartDAO shoppingCartDAO;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        shoppingCartDAO = new ShoppingCartDAO();
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        switch (path) {
            case "/cart":
                displayCart(request, response, currentUser.getUserID());
                break;
            case "/cart/remove":
                handleRemoveCartItem(request, response, currentUser.getUserID());
                break;
            case "/cart/clear":
                handleClearCart(request, response, currentUser.getUserID());
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        switch (path) {
            case "/cart/add":
                handleAddCartItem(request, response, currentUser.getUserID());
                break;
            case "/cart/update":
                handleUpdateCartItem(request, response, currentUser.getUserID());
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void displayCart(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        List<ShoppingCart> cartItems = shoppingCartDAO.getCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            request.setAttribute("cartEmpty", true);
        }
        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("/cart.jsp").forward(request, response);
    }

    private void handleAddCartItem(HttpServletRequest request, HttpServletResponse response, UUID userId) throws IOException, ServletException {
        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        if (productIdParam == null || productIdParam.trim().isEmpty() || quantityParam == null || quantityParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thiếu thông tin sản phẩm hoặc số lượng.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            UUID productId = UUID.fromString(productIdParam);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                request.setAttribute("errorMessage", "Số lượng phải lớn hơn 0.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            Product product = productDAO.getProductById(productId);
            if (product == null || !product.isActive()) {
                request.setAttribute("errorMessage", "Sản phẩm không tồn tại hoặc không hoạt động.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            if (product.getStockQuantity() < quantity) {
                request.setAttribute("errorMessage", "Số lượng sản phẩm trong kho không đủ.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            boolean success = shoppingCartDAO.addOrUpdateCartItem(userId, productId, quantity);
            if (success) {
                LOGGER.info("Product " + productId + " added/updated in cart for user " + userId + ". Quantity: " + quantity);
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                request.setAttribute("errorMessage", "Không thể thêm sản phẩm vào giỏ hàng.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID or quantity format: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm hoặc số lượng không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleUpdateCartItem(HttpServletRequest request, HttpServletResponse response, UUID userId) throws IOException, ServletException {
        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        if (productIdParam == null || productIdParam.trim().isEmpty() || quantityParam == null || quantityParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thiếu thông tin sản phẩm hoặc số lượng.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            UUID productId = UUID.fromString(productIdParam);
            int quantity = Integer.parseInt(quantityParam);

            Product product = productDAO.getProductById(productId);
            if (product == null || !product.isActive()) {
                request.setAttribute("errorMessage", "Sản phẩm không tồn tại hoặc không hoạt động.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            if (quantity > product.getStockQuantity()) {
                request.setAttribute("errorMessage", "Số lượng sản phẩm trong kho không đủ.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            boolean success = shoppingCartDAO.updateCartItemQuantity(userId, productId, quantity);
            if (success) {
                LOGGER.info("Cart item quantity updated for user " + userId + " product " + productId + ". New quantity: " + quantity);
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật số lượng sản phẩm trong giỏ hàng.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID or quantity format: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm hoặc số lượng không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleRemoveCartItem(HttpServletRequest request, HttpServletResponse response, UUID userId) throws IOException, ServletException {
        String productIdParam = request.getParameter("productId");

        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Thiếu ID sản phẩm để xóa.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            UUID productId = UUID.fromString(productIdParam);
            boolean success = shoppingCartDAO.removeCartItem(userId, productId);
            if (success) {
                LOGGER.info("Product " + productId + " removed from cart for user " + userId + ".");
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                request.setAttribute("errorMessage", "Không thể xóa sản phẩm khỏi giỏ hàng.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product ID format for removal: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID sản phẩm không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleClearCart(HttpServletRequest request, HttpServletResponse response, UUID userId) throws IOException, ServletException {
        boolean success = shoppingCartDAO.clearCart(userId);
        if (success) {
            LOGGER.info("Cart cleared for user " + userId + ".");
            response.sendRedirect(request.getContextPath() + "/cart");
        } else {
            request.setAttribute("errorMessage", "Không thể xóa toàn bộ giỏ hàng.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
