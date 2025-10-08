package controller.servlet;

import dao.AuthDAO;
import dto.LoginRequestDTO;
import dto.RegisterRequestDTO;
import model.User;
import util.EmailService;
import util.FileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/register", "/verify", "/login", "/forgot-password", "/reset-password"})
public class AuthServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AuthServlet.class.getName());
    private AuthDAO authDAO;
    private static final int MAX_RESET_ATTEMPTS = 3;
    private static final long RESET_WINDOW_MS = 60 * 60 * 1000; // 1 giờ
    private final ConcurrentHashMap<String, ResetAttempt> resetAttempts = new ConcurrentHashMap<>();

    private static class ResetAttempt {
        AtomicInteger count = new AtomicInteger(0);
        long windowStart = System.currentTimeMillis();
    }

    @Override
    public void init() throws ServletException {
        FileService fileService = new FileService(getServletContext());
        EmailService emailService = new EmailService(fileService);
        authDAO = new AuthDAO(emailService);
        logger.info("AuthServlet initialized with AuthDAO and EmailService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        // Kiểm tra header Referer để giảm rủi ro bảo mật
        String referer = request.getHeader("Referer");
        if (referer == null || !referer.startsWith("http://localhost:9090/PCGearStore")) {
            logger.warning("Invalid Referer for path: " + path + ", referer: " + referer);
            request.setAttribute("errorMessage", "Invalid request source. Please try again.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            switch (path) {
                case "/register":
                    handleRegister(request, response);
                    break;
                case "/login":
                    handleLogin(request, response);
                    break;
                case "/forgot-password":
                    handleForgotPassword(request, response);
                    break;
                case "/reset-password":
                    handleResetPassword(request, response);
                    break;
                default:
                    logger.warning("Invalid POST endpoint: " + path);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid endpoint");
            }
        } catch (Exception e) {
            logger.severe("Unexpected error in POST request for " + path + ": " + e.getMessage());
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        

        String path = request.getServletPath();
        try {
            switch (path) {
                case "/register":
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                    break;
                case "/login":
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    break;
                case "/verify":
                    handleVerify(request, response);
                    break;
                case "/forgot-password":
                    request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
                    break;
                case "/reset-password":
                    String token = request.getParameter("token");
                    if (token == null || token.trim().isEmpty()) {
                        logger.warning("Missing reset token in GET request");
                        request.setAttribute("errorMessage", "Reset token is missing.");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                    } else {
                        request.setAttribute("token", token.trim());
                        request.getRequestDispatcher("reset-password.jsp").forward(request, response);
                    }
                    break;
                default:
                    logger.info("Forwarding unknown GET path " + path + " to index.jsp");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.severe("Unexpected error in GET request for " + path + ": " + e.getMessage());
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumber = request.getParameter("phoneNumber");
        String dateOfBirth = request.getParameter("dateOfBirth");
        String gender = request.getParameter("gender");

        StringBuilder errorMsg = new StringBuilder();
        if (username == null || username.trim().isEmpty()) {
            errorMsg.append("Username is required. ");
        }
        if (email == null || email.trim().isEmpty()) {
            errorMsg.append("Email is required. ");
        }
        if (password == null || password.trim().isEmpty()) {
            errorMsg.append("Password is required. ");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            errorMsg.append("First name is required. ");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            errorMsg.append("Last name is required. ");
        }
        if (!errorMsg.isEmpty()) {
            request.setAttribute("errorMessage", errorMsg.toString());
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            request.setAttribute("errorMessage", "Invalid email format.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        LocalDate dob = null;
        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
            try {
                dob = LocalDate.parse(dateOfBirth);
            } catch (DateTimeParseException e) {
                request.setAttribute("errorMessage", "Invalid date of birth format (use YYYY-MM-DD).");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
        }

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(
                username.trim(), email.trim(), password.trim(), firstName.trim(), lastName.trim(),
                phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber.trim() : null,
                dob, gender != null && !gender.trim().isEmpty() ? gender.trim() : null
        );

        boolean success = authDAO.register(registerRequest);
        if (success) {
            logger.info("User registered successfully: " + username);
            request.setAttribute("message", "Registration successful! Please check your email to verify your account.");
            request.getRequestDispatcher("register-success.jsp").forward(request, response);
        } else {
            logger.warning("Registration failed for user: " + username);
            request.setAttribute("errorMessage", "Registration failed. Username, email, or phone number may already exist, or invalid input.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        LoginRequestDTO loginRequest = new LoginRequestDTO(username.trim(), password.trim());
        try {
            User user = authDAO.login(loginRequest);
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            logger.info("User logged in: " + username);
            if ("admin".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } catch (SQLException e) {
            logger.warning("Login failed for: " + username + " - " + e.getMessage());
            if (e.getMessage().equals("Email not verified")) {
                HttpSession session = request.getSession(true);
                session.setAttribute("tempLogin", loginRequest);
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private void handleVerify(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.trim().isEmpty()) {
            logger.warning("Missing verification token");
            request.setAttribute("errorMessage", "Verification token is missing.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        boolean success = authDAO.verifyEmail(token.trim());
        if (success) {
            logger.info("Email verified successfully for token: " + token);
            HttpSession session = request.getSession(true);
            LoginRequestDTO tempLogin = (LoginRequestDTO) session.getAttribute("tempLogin");
            if (tempLogin != null) {
                try {
                    User user = authDAO.login(tempLogin);
                    session.setAttribute("user", user);
                    session.removeAttribute("tempLogin");
                    logger.info("Auto-login after verification for user: " + tempLogin.getUsername());
                    response.sendRedirect("home.jsp");
                    return;
                } catch (SQLException e) {
                    logger.warning("Auto-login failed after verification: " + e.getMessage());
                }
            }
            request.setAttribute("successMessage", "Email verified successfully! You can now log in.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            logger.warning("Invalid or expired verification token: " + token);
            request.setAttribute("errorMessage", "Invalid or expired verification link. Please request a new verification email.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email is required.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            request.setAttribute("errorMessage", "Invalid email format.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }

        ResetAttempt attempt = resetAttempts.computeIfAbsent(email, k -> new ResetAttempt());
        synchronized (attempt) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - attempt.windowStart > RESET_WINDOW_MS) {
                attempt.count.set(0);
                attempt.windowStart = currentTime;
            }
            if (attempt.count.incrementAndGet() > MAX_RESET_ATTEMPTS) {
                logger.warning("Too many password reset attempts for email: " + email);
                request.setAttribute("errorMessage", "Too many reset attempts. Please try again later.");
                request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
                return;
            }
        }

        boolean success = authDAO.forgotPassword(email.trim());
        if (success) {
            logger.info("Password reset email sent to: " + email);
            request.setAttribute("successMessage", "A password reset link has been sent to your email. Please check your inbox or spam folder.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
        } else {
            logger.warning("Forgot password failed for email: " + email);
            request.setAttribute("errorMessage", "Email not found or account is inactive.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || token.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.trim().equals(confirmPassword.trim())) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            return;
        }

        boolean success = authDAO.resetPassword(token.trim(), newPassword.trim());
        if (success) {
            logger.info("Password reset successfully for token: " + token);
            request.setAttribute("successMessage", "Your password has been reset successfully. You can now log in.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            logger.warning("Invalid or expired reset token: " + token);
            request.setAttribute("errorMessage", "Invalid or expired reset link. Please request a new password reset.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
        }
    }
}