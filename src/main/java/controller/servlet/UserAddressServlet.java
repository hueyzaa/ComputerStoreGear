package controller.servlet;

import dao.UserAddressDAO;
import model.User;
import model.UserAddress;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/profile/addresses", "/profile/addresses/add", "/profile/addresses/edit", "/profile/addresses/delete", "/profile/addresses/set-default"})
public class UserAddressServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UserAddressServlet.class.getName());
    private UserAddressDAO userAddressDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userAddressDAO = new UserAddressDAO();
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
            case "/profile/addresses":
                displayAddresses(request, response, currentUser.getUserID());
                break;
            case "/profile/addresses/add":
                request.getRequestDispatcher("/address-form.jsp").forward(request, response);
                break;
            case "/profile/addresses/edit":
                displayEditAddressForm(request, response, currentUser.getUserID());
                break;
            case "/profile/addresses/delete":
                handleDeleteAddress(request, response, currentUser.getUserID());
                break;
            case "/profile/addresses/set-default":
                handleSetDefaultAddress(request, response, currentUser.getUserID());
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
            case "/profile/addresses/add":
                handleAddAddress(request, response, currentUser.getUserID());
                break;
            case "/profile/addresses/edit":
                handleUpdateAddress(request, response, currentUser.getUserID());
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void displayAddresses(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        List<UserAddress> addresses = userAddressDAO.getAddressesByUserId(userId);
        request.setAttribute("addresses", addresses);
        request.getRequestDispatcher("/addresses.jsp").forward(request, response);
    }

    private void displayEditAddressForm(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        String addressIdParam = request.getParameter("id");
        if (addressIdParam == null || addressIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID địa chỉ không được cung cấp.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            UUID addressId = UUID.fromString(addressIdParam);
            UserAddress address = userAddressDAO.getAddressById(addressId);

            if (address == null || !address.getUserID().equals(userId)) {
                request.setAttribute("errorMessage", "Địa chỉ không tìm thấy hoặc bạn không có quyền truy cập.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            request.setAttribute("address", address);
            request.getRequestDispatcher("/address-form.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid address ID format: " + addressIdParam + " - " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID địa chỉ không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleAddAddress(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        UserAddress newAddress = populateAddressFromRequest(request, userId);
        if (newAddress == null) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin địa chỉ.");
            request.getRequestDispatcher("/address-form.jsp").forward(request, response);
            return;
        }

        boolean success = userAddressDAO.addAddress(newAddress);
        if (success) {
            String message = URLEncoder.encode("Địa chỉ đã được thêm thành công.", StandardCharsets.UTF_8.toString());
            response.sendRedirect(request.getContextPath() + "/profile/addresses?message=" + message);
        } else {
            request.setAttribute("errorMessage", "Không thể thêm địa chỉ. Vui lòng thử lại.");
            request.setAttribute("address", newAddress); // Giữ lại dữ liệu đã nhập
            request.getRequestDispatcher("/address-form.jsp").forward(request, response);
        }
    }

    private void handleUpdateAddress(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        String addressIdParam = request.getParameter("addressId");
        if (addressIdParam == null || addressIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID địa chỉ không được cung cấp để cập nhật.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            UUID addressId = UUID.fromString(addressIdParam);
            UserAddress existingAddress = userAddressDAO.getAddressById(addressId);

            if (existingAddress == null || !existingAddress.getUserID().equals(userId)) {
                request.setAttribute("errorMessage", "Địa chỉ không tìm thấy hoặc bạn không có quyền cập nhật.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }

            UserAddress updatedAddress = populateAddressFromRequest(request, userId);
            updatedAddress.setAddressID(addressId); // Đảm bảo ID được giữ nguyên

            boolean success = userAddressDAO.updateAddress(updatedAddress);
            if (success) {
                String message = URLEncoder.encode("Địa chỉ đã được cập nhật thành công.", StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/profile/addresses?message=" + message);
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật địa chỉ. Vui lòng thử lại.");
                request.setAttribute("address", updatedAddress); // Giữ lại dữ liệu đã nhập
                request.getRequestDispatcher("/address-form.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid address ID format for update: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID địa chỉ không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleDeleteAddress(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        String addressIdParam = request.getParameter("id");
        if (addressIdParam == null || addressIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID địa chỉ không được cung cấp để xóa.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            UUID addressId = UUID.fromString(addressIdParam);
            boolean success = userAddressDAO.deleteAddress(addressId, userId);
            if (success) {
                String message = URLEncoder.encode("Địa chỉ đã được xóa thành công.", StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/profile/addresses?message=" + message);
            } else {
                request.setAttribute("errorMessage", "Không thể xóa địa chỉ. Vui lòng thử lại.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid address ID format for delete: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID địa chỉ không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void handleSetDefaultAddress(HttpServletRequest request, HttpServletResponse response, UUID userId) throws ServletException, IOException {
        String addressIdParam = request.getParameter("id");
        if (addressIdParam == null || addressIdParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "ID địa chỉ không được cung cấp để đặt mặc định.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            UUID addressId = UUID.fromString(addressIdParam);
            boolean success = userAddressDAO.setDefaultAddress(addressId, userId);
            if (success) {
                String message = URLEncoder.encode("Địa chỉ mặc định đã được cập nhật.", StandardCharsets.UTF_8.toString());
                response.sendRedirect(request.getContextPath() + "/profile/addresses?message=" + message);
            } else {
                request.setAttribute("errorMessage", "Không thể đặt địa chỉ mặc định. Vui lòng thử lại.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid address ID format for set default: " + e.getMessage());
            request.setAttribute("errorMessage", "Định dạng ID địa chỉ không hợp lệ.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private UserAddress populateAddressFromRequest(HttpServletRequest request, UUID userId) {
        String addressType = request.getParameter("addressType");
        String addressLine1 = request.getParameter("addressLine1");
        String addressLine2 = request.getParameter("addressLine2");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String postalCode = request.getParameter("postalCode");
        String country = request.getParameter("country");
        boolean isDefault = "on".equalsIgnoreCase(request.getParameter("isDefault"));

        if (addressType == null || addressType.trim().isEmpty() ||
            addressLine1 == null || addressLine1.trim().isEmpty() ||
            city == null || city.trim().isEmpty() ||
            country == null || country.trim().isEmpty()) {
            return null; // Indicate missing required fields
        }

        UserAddress address = new UserAddress();
        address.setUserID(userId);
        address.setAddressType(addressType.trim());
        address.setAddressLine1(addressLine1.trim());
        address.setAddressLine2(addressLine2 != null ? addressLine2.trim() : null);
        address.setCity(city.trim());
        address.setState(state != null ? state.trim() : null);
        address.setPostalCode(postalCode != null ? postalCode.trim() : null);
        address.setCountry(country.trim());
        address.setDefault(isDefault);
        return address;
    }
}
