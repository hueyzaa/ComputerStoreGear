package controller.servlet;

import dao.AuthDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.EmailService;
import util.FileService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/profile", "/profile/edit"})
public class ProfileServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProfileServlet.class.getName());
    private AuthDAO authDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        FileService fileService = new FileService(getServletContext());
        EmailService emailService = new EmailService(fileService);
        authDAO = new AuthDAO(emailService);
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
            case "/profile":
                displayProfile(request, response, currentUser);
                break;
            case "/profile/edit":
                displayProfile(request, response, currentUser); // Dùng chung displayProfile
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
            case "/profile/edit":
                handleUpdateProfile(request, response, currentUser);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void displayProfile(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        // Lấy lại thông tin người dùng mới nhất từ DB để đảm bảo dữ liệu luôn cập nhật
        User updatedUser = authDAO.getUserById(currentUser.getUserID());
        if (updatedUser != null) {
            request.getSession().setAttribute("user", updatedUser); // Cập nhật session
            request.setAttribute("userProfile", updatedUser);

            // Convert LocalDate to java.util.Date for fmt:formatDate
            if (updatedUser.getDateOfBirth() != null) {
                java.sql.Date sqlDateOfBirth = java.sql.Date.valueOf(updatedUser.getDateOfBirth());
                request.setAttribute("userProfileDateOfBirthUtil", sqlDateOfBirth);
            }
        } else {
            // Xử lý trường hợp không tìm thấy người dùng (có thể do lỗi DB hoặc người dùng bị xóa)
            request.getSession().invalidate(); // Hủy session
            response.sendRedirect(request.getContextPath() + "/login?message=Tài khoản của bạn không còn tồn tại. Vui lòng đăng nhập lại.");
            return;
        }
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumber = request.getParameter("phoneNumber");
        String dateOfBirth = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");

        // Basic validation
        StringBuilder errorMsg = new StringBuilder();
        if (username == null || username.trim().isEmpty()) errorMsg.append("Tên đăng nhập không được để trống. ");
        if (email == null || email.trim().isEmpty()) errorMsg.append("Email không được để trống. ");
        if (firstName == null || firstName.trim().isEmpty()) errorMsg.append("Tên không được để trống. ");
        if (lastName == null || lastName.trim().isEmpty()) errorMsg.append("Họ không được để trống. ");

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorMsg.append("Định dạng email không hợp lệ. ");
        }

        LocalDate dob = null;
        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
            try {
                dob = LocalDate.parse(dateOfBirth);
            } catch (DateTimeParseException e) {
                errorMsg.append("Định dạng ngày sinh không hợp lệ (YYYY-MM-DD). ");
            }
        }

        if (errorMsg.length() > 0) {
            request.setAttribute("errorMessage", errorMsg.toString());
            // Populate fields back to form
            User userToPopulate = new User();
            userToPopulate.setUserID(currentUser.getUserID());
            userToPopulate.setUsername(username);
            userToPopulate.setEmail(email);
            userToPopulate.setFirstName(firstName);
            userToPopulate.setLastName(lastName);
            userToPopulate.setPhoneNumber(phoneNumber);
            userToPopulate.setDateOfBirth(dob);
            userToPopulate.setGender(gender);
            request.setAttribute("userProfile", userToPopulate);
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
            return;
        }

        // Check for duplicate username/email if changed and not current user's
        if (!username.equals(currentUser.getUsername()) && authDAO.checkExistUsername(username)) {
            request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            displayProfile(request, response, currentUser); // Re-display form with error
            return;
        }
        if (!email.equals(currentUser.getEmail()) && authDAO.checkExistEmail(email)) {
            request.setAttribute("errorMessage", "Email đã tồn tại.");
            displayProfile(request, response, currentUser); // Re-display form with error
            return;
        }
        if (phoneNumber != null && !phoneNumber.equals(currentUser.getPhoneNumber()) && authDAO.checkExistPhoneNumber(phoneNumber)) {
            request.setAttribute("errorMessage", "Số điện thoại đã tồn tại.");
            displayProfile(request, response, currentUser); // Re-display form with error
            return;
        }

        User updatedUser = new User();
        updatedUser.setUserID(currentUser.getUserID());
        updatedUser.setUsername(username.trim());
        updatedUser.setEmail(email.trim());
        updatedUser.setFirstName(firstName.trim());
        updatedUser.setLastName(lastName.trim());
        updatedUser.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
        updatedUser.setDateOfBirth(dob);
        updatedUser.setGender(gender != null ? gender.trim() : null);

        if (authDAO.updateUserProfile(updatedUser)) {
            // Cập nhật session với thông tin người dùng mới nhất
            request.getSession().setAttribute("user", authDAO.getUserById(currentUser.getUserID()));
            response.sendRedirect(request.getContextPath() + "/profile?message=Hồ sơ của bạn đã được cập nhật thành công.");
        } else {
            request.setAttribute("errorMessage", "Không thể cập nhật hồ sơ. Vui lòng thử lại.");
            request.setAttribute("userProfile", updatedUser); // Giữ lại dữ liệu đã nhập
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        }
    }
}