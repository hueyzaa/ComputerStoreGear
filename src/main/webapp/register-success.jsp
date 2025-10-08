<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký Thành Công</title>
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
        .main-content {
            flex-grow: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .success-card {
            background-color: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            text-align: center;
            max-width: 600px;
            width: 100%;
        }
        .success-card h2 {
            color: #28a745;
            margin-bottom: 20px;
            font-weight: 700;
        }
        .success-card p {
            color: #555;
            line-height: 1.6;
            margin-bottom: 30px;
        }
        .success-card .btn {
            padding: 10px 25px;
            font-size: 1.1rem;
            border-radius: 5px;
        }
    </style>
</head>
<body>

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8">
                    <div class="success-card">
                        <h2>Đăng Ký Thành Công!</h2>
                        <c:if test="${not empty requestScope.successMessage}">
                            <p class="alert alert-success">${requestScope.successMessage}</p>
                        </c:if>
                        <p>Cảm ơn bạn đã đăng ký tài khoản. Vui lòng kiểm tra email của bạn để xác minh tài khoản và hoàn tất quá trình đăng ký.</p>
                        <div class="mt-4">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Quay lại trang Đăng nhập</a>
                        </div>
                    </div>
                </div>
                </div>
            </div>
        </div>
    </div>

</body>
</html>