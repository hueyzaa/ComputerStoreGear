<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
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
            min-height: calc(100vh - 150px); /* Adjust based on header/footer height */
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 50px 0;
        }
        .reset-password-card {
            background-color: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            max-width: 450px;
            width: 100%;
        }
        .reset-password-card h2 {
            text-align: center;
            color: #343a40;
            margin-bottom: 30px;
            font-weight: 700;
        }
        .reset-password-card .form-label {
            font-weight: 500;
        }
        .reset-password-card .btn-primary {
            width: 100%;
            padding: 10px;
            font-size: 1.1rem;
            margin-top: 15px;
        }
        .footer {
            background-color: #212529;
            color: white;
            padding: 30px 0;
            text-align: center;
            margin-top: 50px;
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

    <jsp:include page="header.jsp" />

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <div class="reset-password-card">
                        <h2>Đặt Lại Mật Khẩu</h2>
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
                        <c:if test="${not empty param.token}">
                            <form action="${pageContext.request.contextPath}/reset-password" method="post">
                                <input type="hidden" name="token" value="${param.token}">
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">Mật khẩu mới:</label>
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                                </div>
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới:</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                </div>
                                <button type="submit" class="btn btn-primary">Đặt Lại Mật Khẩu</button>
                            </form>
                        </c:if>
                        <c:if test="${empty param.token}">
                            <div class="alert alert-warning" role="alert">
                                Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>