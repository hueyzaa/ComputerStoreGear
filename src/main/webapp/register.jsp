<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        html, body {
            height: 100%;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        .navbar {
            background-color: #212529 !important;
        }
        .navbar-brand, .nav-link {
            color: #ffffff !important;
        }
        .navbar-brand:hover, .nav-link:hover {
            color: #cccccc !important;
        }
        .main-content {
            flex-grow: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .register-card {
            background-color: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            width: 100%;
        }
        .register-card h2 {
            text-align: center;
            color: #343a40;
            margin-bottom: 30px;
            font-weight: 700;
        }
        .register-card .form-label {
            font-weight: 500;
        }
        .register-card .btn-primary {
            width: 100%;
            padding: 10px;
            font-size: 1.1rem;
            margin-top: 15px;
        }
        .register-card .text-center a {
            color: #007bff;
            text-decoration: none;
        }
        .register-card .text-center a:hover {
            text-decoration: underline;
        }
        .footer {
            background-color: #212529;
            color: white;
            padding: 30px 0;
            text-align: center;
            margin-top: auto;
        }
        .footer a {
            color: #cccccc;
            text-decoration: none;
        }
        .footer a:hover {
            color: #ffffff;
        }
    </style>
</head>
<body>

    

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 col-lg-6">
                    <div class="register-card">
                        <h2>Đăng Ký Tài Khoản</h2>
                        <c:if test="${not empty requestScope.errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                ${requestScope.errorMessage}
                            </div>
                        </c:if>
                        <c:if test="${not empty requestScope.successMessage}">
                            <div class="alert alert-success" role="alert">
                                ${requestScope.successMessage}
                            </div>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/register" method="post">
                            <div class="mb-3">
                                <label for="username" class="form-label">Tên đăng nhập:</label>
                                <input type="text" class="form-control" id="username" name="username" required value="${param.username}">
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email:</label>
                                <input type="email" class="form-control" id="email" name="email" required value="${param.email}">
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">Mật khẩu:</label>
                                <input type="password" class="form-control" id="password" name="password" required>
                            </div>
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label">Xác nhận mật khẩu:</label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="firstName" class="form-label">Tên:</label>
                                    <input type="text" class="form-control" id="firstName" name="firstName" value="${param.firstName}">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="lastName" class="form-label">Họ:</label>
                                    <input type="text" class="form-control" id="lastName" name="lastName" value="${param.lastName}">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="phoneNumber" class="form-label">Số điện thoại:</label>
                                <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" value="${param.phoneNumber}">
                            </div>
                            <div class="mb-3">
                                <label for="dateOfBirth" class="form-label">Ngày sinh:</label>
                                <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" value="${param.dateOfBirth}">
                            </div>
                            <div class="mb-3">
                                <label for="gender" class="form-label">Giới tính:</label>
                                <select class="form-select" id="gender" name="gender">
                                    <option value="">Chọn giới tính</option>
                                    <option value="Male" ${param.gender == 'Male' ? 'selected' : ''}>Nam</option>
                                    <option value="Female" ${param.gender == 'Female' ? 'selected' : ''}>Nữ</option>
                                    <option value="Other" ${param.gender == 'Other' ? 'selected' : ''}>Khác</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary">Đăng Ký</button>
                        </form>
                        <div class="text-center mt-3">
                            Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay</a>
                        </div>
                    </div>
                </div>
                </div>
            </div>
        </div>
    </div>

    

</body>
</html>