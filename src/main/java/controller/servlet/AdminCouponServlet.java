package controller.servlet;

import dao.CouponDAO;
import model.Coupon;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/admin/coupons", "/admin/coupons/add", "/admin/coupons/edit", "/admin/coupons/delete"})
public class AdminCouponServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminCouponServlet.class.getName());
    private CouponDAO couponDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        couponDAO = new CouponDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        switch (path) {
            case "/admin/coupons":
                listCoupons(request, response);
                break;
            case "/admin/coupons/add":
                showAddForm(request, response);
                break;
            case "/admin/coupons/edit":
                showEditForm(request, response);
                break;
            case "/admin/coupons/delete":
                deleteCoupon(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isAdmin(request, response)) return; // Kiểm tra quyền admin

        String path = request.getServletPath();
        switch (path) {
            case "/admin/coupons/add":
                addCoupon(request, response);
                break;
            case "/admin/coupons/edit":
                updateCoupon(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            LOGGER.warning("Unauthorized access attempt to admin coupon management by user: " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return false;
        }
        return true;
    }

    private void listCoupons(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Coupon> couponList = couponDAO.getAllCoupons();
        request.setAttribute("couponList", couponList);
        request.getRequestDispatcher("/admin-coupons.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/admin-coupon-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String couponIdParam = request.getParameter("id");
        if (couponIdParam == null || couponIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID mã giảm giá không được cung cấp.");
            listCoupons(request, response);
            return;
        }

        try {
            UUID couponId = UUID.fromString(couponIdParam);
            Coupon coupon = couponDAO.getCouponById(couponId);
            if (coupon == null) {
                request.setAttribute("errorMessage", "Không tìm thấy mã giảm giá.");
                listCoupons(request, response);
                return;
            }
            request.setAttribute("coupon", coupon);
            request.getRequestDispatcher("/admin-coupon-form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid coupon ID format: " + couponIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID mã giảm giá không hợp lệ.");
            listCoupons(request, response);
        }
    }

    private void addCoupon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Coupon coupon = populateCouponFromRequest(request);
        if (coupon == null) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin mã giảm giá.");
            showAddForm(request, response);
            return;
        }

        if (couponDAO.addCoupon(coupon)) {
            response.sendRedirect(request.getContextPath() + "/admin/coupons?message=" + URLEncoder.encode("Mã giảm giá đã được thêm thành công.", StandardCharsets.UTF_8.toString()));
        } else {
            request.setAttribute("errorMessage", "Không thể thêm mã giảm giá. Vui lòng kiểm tra mã code hoặc các thông tin khác.");
            request.setAttribute("coupon", coupon); // Giữ lại dữ liệu đã nhập
            showAddForm(request, response);
        }
    }

    private void updateCoupon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String couponIdParam = request.getParameter("couponId");
        if (couponIdParam == null || couponIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID mã giảm giá không được cung cấp để cập nhật.");
            listCoupons(request, response);
            return;
        }

        try {
            UUID couponId = UUID.fromString(couponIdParam);
            Coupon coupon = populateCouponFromRequest(request);
            if (coupon == null) {
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin mã giảm giá.");
                showEditForm(request, response);
                return;
            }
            coupon.setCouponID(couponId); // Đảm bảo ID được giữ nguyên

            if (couponDAO.updateCoupon(coupon)) {
                response.sendRedirect(request.getContextPath() + "/admin/coupons?message=" + URLEncoder.encode("Mã giảm giá đã được cập nhật thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật mã giảm giá. Vui lòng thử lại.");
                request.setAttribute("coupon", coupon); // Giữ lại dữ liệu đã nhập
                showEditForm(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid coupon ID format for update: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID mã giảm giá không hợp lệ.");
            listCoupons(request, response);
        }
    }

    private void deleteCoupon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String couponIdParam = request.getParameter("id");
        if (couponIdParam == null || couponIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID mã giảm giá không được cung cấp để xóa.");
            listCoupons(request, response);
            return;
        }

        try {
            UUID couponId = UUID.fromString(couponIdParam);
            if (couponDAO.deleteCoupon(couponId)) {
                response.sendRedirect(request.getContextPath() + "/admin/coupons?message=" + URLEncoder.encode("Mã giảm giá đã được xóa thành công.", StandardCharsets.UTF_8.toString()));
            } else {
                request.setAttribute("errorMessage", "Không thể xóa mã giảm giá. Vui lòng thử lại.");
                listCoupons(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid coupon ID format for delete: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID mã giảm giá không hợp lệ.");
            listCoupons(request, response);
        }
    }

    private Coupon populateCouponFromRequest(HttpServletRequest request) {
        try {
            Coupon coupon = new Coupon();
            coupon.setCouponCode(request.getParameter("couponCode"));
            coupon.setCouponName(request.getParameter("couponName"));
            coupon.setDescription(request.getParameter("description"));
            coupon.setDiscountType(request.getParameter("discountType"));
            coupon.setDiscountValue(new BigDecimal(request.getParameter("discountValue")));
            
            String minOrderAmount = request.getParameter("minOrderAmount");
            coupon.setMinOrderAmount(minOrderAmount != null && !minOrderAmount.trim().isEmpty() ? new BigDecimal(minOrderAmount) : null);

            String maxDiscountAmount = request.getParameter("maxDiscountAmount");
            coupon.setMaxDiscountAmount(maxDiscountAmount != null && !maxDiscountAmount.trim().isEmpty() ? new BigDecimal(maxDiscountAmount) : null);

            String usageLimit = request.getParameter("usageLimit");
            coupon.setUsageLimit(usageLimit != null && !usageLimit.trim().isEmpty() ? Integer.parseInt(usageLimit) : null);

            coupon.setActive("on".equalsIgnoreCase(request.getParameter("isActive")));
            coupon.setStartDate(LocalDateTime.parse(request.getParameter("startDate")));
            coupon.setEndDate(LocalDateTime.parse(request.getParameter("endDate")));
            
            return coupon;
        } catch (DateTimeParseException e) {
            LOGGER.warning("Date parsing error for coupon: " + e.getMessage());
            return null;
        } catch (Exception e) {
            LOGGER.warning("Error populating coupon from request: " + e.getMessage());
            return null; // Trả về null nếu có lỗi parsing hoặc thiếu trường
        }
    }
}
